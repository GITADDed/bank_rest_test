package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.TransferCheckResult;
import com.example.bankcards.entity.TransferData;
import com.example.bankcards.entity.TransferStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.Violation;
import com.example.bankcards.exception.ConflictException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.ValidationException;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.validation.validators.business.TransferValidator;
import com.example.bankcards.validation.validators.request.RequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private RequestValidator<TransferRequest> requestValidator;

    @Mock
    private TransferValidator transferValidator;

    @Mock
    private CardAccessService cardAccessService;

    @Mock
    private CardStatusService cardStatusService;

    @InjectMocks
    private TransferServiceImpl service;

    @Test
    void transfer_updatesBalances_savesTransfer_andMarksExpired() {
        Long userId = 11L;
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("10.00"));
        Card from = buildCard(1L, new BigDecimal("100.00"));
        Card to = buildCard(2L, new BigDecimal("50.00"));
        TransferCheckResult result = new TransferCheckResult(List.of(), List.of(3L, 4L));

        when(cardAccessService.getOwnedCardOrThrow(request.fromCardId(), userId, "fromCardId"))
                .thenReturn(from);
        when(cardAccessService.getOwnedCardOrThrow(request.toCardId(), userId, "toCardId"))
                .thenReturn(to);
        when(transferValidator.validate(any(TransferData.class))).thenReturn(result);
        when(transferRepository.save(any())).thenAnswer(invocation -> {
            Object transfer = invocation.getArgument(0);
            ReflectionTestUtils.setField(transfer, "id", 99L);
            return transfer;
        });

        TransferResponse response = service.transfer(request, userId);

        verify(requestValidator).validate(request);
        verify(transferValidator).validate(argThat(data -> data.from() == from && data.to() == to && data.request() == request));
        verify(cardStatusService).markExpired(3L);
        verify(cardStatusService).markExpired(4L);
        verify(transferRepository).save(any());

        assertEquals(new BigDecimal("90.00"), from.getBalance());
        assertEquals(new BigDecimal("60.00"), to.getBalance());
        assertEquals(99L, response.id());
        assertEquals(1L, response.fromCardId());
        assertEquals(2L, response.toCardId());
        assertEquals(TransferStatus.SUCCESS, response.status());
    }

    @Test
    void transfer_throwsConflict_whenViolationsPresent() {
        Long userId = 12L;
        TransferRequest request = new TransferRequest(5L, 6L, new BigDecimal("25.00"));
        Card from = buildCard(5L, new BigDecimal("100.00"));
        Card to = buildCard(6L, new BigDecimal("50.00"));
        TransferCheckResult result = new TransferCheckResult(
                List.of(new Violation("amount", "Insufficient funds.")),
                List.of(8L)
        );

        when(cardAccessService.getOwnedCardOrThrow(request.fromCardId(), userId, "fromCardId"))
                .thenReturn(from);
        when(cardAccessService.getOwnedCardOrThrow(request.toCardId(), userId, "toCardId"))
                .thenReturn(to);
        when(transferValidator.validate(any(TransferData.class))).thenReturn(result);

        ConflictException ex = assertThrows(ConflictException.class, () -> service.transfer(request, userId));

        assertEquals("CONFLICT_ERROR", ex.getCode());
        assertEquals(1, ex.getViolations().size());
        assertEquals(new BigDecimal("100.00"), from.getBalance());
        assertEquals(new BigDecimal("50.00"), to.getBalance());
        verify(cardStatusService).markExpired(8L);
        verify(transferRepository, never()).save(any());
    }

    @Test
    void transfer_throwsConflict_withoutExpiredMarks_whenViolationsPresent() {
        Long userId = 13L;
        TransferRequest request = new TransferRequest(7L, 8L, new BigDecimal("30.00"));
        Card from = buildCard(7L, new BigDecimal("100.00"));
        Card to = buildCard(8L, new BigDecimal("50.00"));
        TransferCheckResult result = new TransferCheckResult(
                List.of(new Violation("amount", "Insufficient funds.")),
                List.of()
        );

        when(cardAccessService.getOwnedCardOrThrow(request.fromCardId(), userId, "fromCardId"))
                .thenReturn(from);
        when(cardAccessService.getOwnedCardOrThrow(request.toCardId(), userId, "toCardId"))
                .thenReturn(to);
        when(transferValidator.validate(any(TransferData.class))).thenReturn(result);

        ConflictException ex = assertThrows(ConflictException.class, () -> service.transfer(request, userId));

        assertEquals("CONFLICT_ERROR", ex.getCode());
        assertEquals(new BigDecimal("100.00"), from.getBalance());
        assertEquals(new BigDecimal("50.00"), to.getBalance());
        verifyNoInteractions(cardStatusService);
        verify(transferRepository, never()).save(any());
    }

    @Test
    void transfer_validatesRequest_beforeSourceCardLookupFailure() {
        Long userId = 23L;
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("10.00"));
        NotFoundException ex = new NotFoundException(List.of(new Violation("fromCardId", "Missing")));

        when(cardAccessService.getOwnedCardOrThrow(request.fromCardId(), userId, "fromCardId"))
                .thenThrow(ex);

        assertThrows(NotFoundException.class, () -> service.transfer(request, userId));

        verify(requestValidator).validate(request);
        verifyNoInteractions(transferValidator, cardStatusService, transferRepository);
    }

    @Test
    void transfer_throwsValidationException_whenRequestInvalid() {
        Long userId = 20L;
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("10.00"));
        ValidationException ex = new ValidationException(List.of(new Violation("amount", "Invalid")));

        doThrow(ex).when(requestValidator).validate(request);

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> service.transfer(request, userId));

        assertEquals("VALIDATION_ERROR", thrown.getCode());
        verifyNoInteractions(cardAccessService, transferValidator, cardStatusService, transferRepository);
    }

    @Test
    void transfer_propagatesNotFound_whenSourceCardMissing() {
        Long userId = 21L;
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("10.00"));
        NotFoundException ex = new NotFoundException(List.of(new Violation("fromCardId", "Missing")));

        when(cardAccessService.getOwnedCardOrThrow(request.fromCardId(), userId, "fromCardId"))
                .thenThrow(ex);

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> service.transfer(request, userId));

        assertEquals("NOT_FOUND_ERROR", thrown.getCode());
        verify(cardAccessService, never())
                .getOwnedCardOrThrow(request.toCardId(), userId, "toCardId");
        verifyNoInteractions(transferValidator, cardStatusService, transferRepository);
    }

    @Test
    void transfer_propagatesNotFound_whenDestinationCardMissing() {
        Long userId = 22L;
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("10.00"));
        Card from = buildCard(1L, new BigDecimal("100.00"));
        NotFoundException ex = new NotFoundException(List.of(new Violation("toCardId", "Missing")));

        when(cardAccessService.getOwnedCardOrThrow(request.fromCardId(), userId, "fromCardId"))
                .thenReturn(from);
        when(cardAccessService.getOwnedCardOrThrow(request.toCardId(), userId, "toCardId"))
                .thenThrow(ex);

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> service.transfer(request, userId));

        assertEquals("NOT_FOUND_ERROR", thrown.getCode());
        verifyNoInteractions(transferValidator, cardStatusService, transferRepository);
    }

    @Test
    void transfer_propagatesConflict_whenValidatorThrows() {
        Long userId = 24L;
        TransferRequest request = new TransferRequest(9L, 10L, new BigDecimal("15.00"));
        Card from = buildCard(9L, new BigDecimal("100.00"));
        Card to = buildCard(10L, new BigDecimal("50.00"));
        ConflictException ex = new ConflictException(List.of(new Violation("amount", "Invalid.")));

        when(cardAccessService.getOwnedCardOrThrow(request.fromCardId(), userId, "fromCardId"))
                .thenReturn(from);
        when(cardAccessService.getOwnedCardOrThrow(request.toCardId(), userId, "toCardId"))
                .thenReturn(to);
        when(transferValidator.validate(any(TransferData.class))).thenThrow(ex);

        ConflictException thrown = assertThrows(ConflictException.class,
                () -> service.transfer(request, userId));

        assertEquals("CONFLICT_ERROR", thrown.getCode());
        assertEquals(new BigDecimal("100.00"), from.getBalance());
        assertEquals(new BigDecimal("50.00"), to.getBalance());
        verifyNoInteractions(cardStatusService, transferRepository);
    }

    private Card buildCard(Long id, BigDecimal balance) {
        User owner = new User("owner", "hash", null);
        Card card = new Card(owner, "panHash", "1234", 1, 2030, CardStatus.ACTIVE, balance);
        ReflectionTestUtils.setField(card, "id", id);
        return card;
    }
}
