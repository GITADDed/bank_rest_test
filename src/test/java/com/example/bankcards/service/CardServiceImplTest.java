package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.Violation;
import com.example.bankcards.exception.ConflictException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.PanHashUtil;
import com.example.bankcards.validation.validators.request.RequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private RequestValidator<CardRequest> requestValidator;

    @InjectMocks
    private CardServiceImpl service;

    @Test
    void createCard_returnsResponse_whenValid() {
        CardRequest request = new CardRequest(7L, "1234567890123456", 12, 2030);
        User owner = new User("user", "hash", null);
        String panHash = PanHashUtil.sha256Hex("", request.pan());

        when(cardRepository.existsByOwnerIdAndPanHashAndDeletedFalse(request.ownerId(), panHash))
                .thenReturn(false);
        when(userRepository.findById(request.ownerId())).thenReturn(Optional.of(owner));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card card = invocation.getArgument(0);
            ReflectionTestUtils.setField(card, "id", 55L);
            return card;
        });

        CardResponse response = service.createCard(request);

        verify(requestValidator).validate(request);
        verify(cardRepository).existsByOwnerIdAndPanHashAndDeletedFalse(request.ownerId(), panHash);
        verify(userRepository).findById(request.ownerId());
        verify(cardRepository).save(any(Card.class));

        assertEquals(55L, response.id());
        assertEquals("**** **** **** 3456", response.maskedPan());
        assertEquals("3456", response.last4());
        assertEquals(12, response.expiryMonth());
        assertEquals(2030, response.expiryYear());
        assertEquals(CardStatus.ACTIVE, response.status());
        assertEquals(BigDecimal.ZERO, response.balance());
    }

    @Test
    void createCard_throwsConflict_whenDuplicatePan() {
        CardRequest request = new CardRequest(7L, "1234567890123456", 12, 2030);
        String panHash = PanHashUtil.sha256Hex("", request.pan());

        when(cardRepository.existsByOwnerIdAndPanHashAndDeletedFalse(request.ownerId(), panHash))
                .thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () -> service.createCard(request));

        assertEquals("CONFLICT_ERROR", ex.getCode());
        assertEquals(1, ex.getViolations().size());
        Violation violation = ex.getViolations().get(0);
        assertEquals("pan", violation.field());
        assertEquals("Card already exists for this owner.", violation.message());
        verify(userRepository, never()).findById(any());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void createCard_throwsNotFound_whenOwnerMissing() {
        CardRequest request = new CardRequest(7L, "1234567890123456", 12, 2030);
        String panHash = PanHashUtil.sha256Hex("", request.pan());

        when(cardRepository.existsByOwnerIdAndPanHashAndDeletedFalse(request.ownerId(), panHash))
                .thenReturn(false);
        when(userRepository.findById(request.ownerId())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.createCard(request));

        assertEquals("NOT_FOUND_ERROR", ex.getCode());
        Violation violation = ex.getViolations().get(0);
        assertEquals("ownerId", violation.field());
        assertEquals("User with id " + request.ownerId() + " not found.", violation.message());
    }

    @Test
    void getAllCards_mapsEntitiesToDto() {
        Card card = buildCard(10L, "1111", CardStatus.ACTIVE, BigDecimal.TEN);
        Page<Card> page = new PageImpl<>(List.of(card));
        PageRequest pageable = PageRequest.of(0, 10);

        when(cardRepository.findAll(pageable)).thenReturn(page);

        Page<CardResponse> result = service.getAllCards(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("**** **** **** 1111", result.getContent().get(0).maskedPan());
        verify(cardRepository).findAll(pageable);
    }

    @Test
    void getMyAllCards_throwsNotFound_whenOwnerMissing() {
        Long ownerId = 9L;
        PageRequest pageable = PageRequest.of(0, 5);

        when(userRepository.findByIdAndDeletedFalse(ownerId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.getMyAllCards(ownerId, pageable));

        assertEquals("NOT_FOUND_ERROR", ex.getCode());
        Violation violation = ex.getViolations().get(0);
        assertEquals("ownerId", violation.field());
        assertEquals("User with id " + ownerId + " not found.", violation.message());
    }

    @Test
    void getMyAllCards_returnsMappedPage_whenOwnerExists() {
        Long ownerId = 9L;
        PageRequest pageable = PageRequest.of(0, 5);
        User owner = new User("owner", "hash", null);
        Page<Card> page = new PageImpl<>(List.of(buildCard(20L, "2222", CardStatus.BLOCKED, BigDecimal.ONE)));

        when(userRepository.findByIdAndDeletedFalse(ownerId)).thenReturn(Optional.of(owner));
        when(cardRepository.findAllByOwnerAndDeletedFalse(owner, pageable)).thenReturn(page);

        Page<CardResponse> result = service.getMyAllCards(ownerId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("**** **** **** 2222", result.getContent().get(0).maskedPan());
        verify(cardRepository).findAllByOwnerAndDeletedFalse(owner, pageable);
    }

    @Test
    void getCardById_throwsNotFound_whenMissing() {
        Long cardId = 100L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getCardById(cardId));

        assertEquals("NOT_FOUND_ERROR", ex.getCode());
        Violation violation = ex.getViolations().get(0);
        assertEquals("id", violation.field());
        assertEquals("Card with id " + cardId + " not found.", violation.message());
    }

    @Test
    void updateCardStatus_throwsConflict_whenTransitionInvalid() {
        Long cardId = 5L;
        Card card = buildCard(5L, "3333", CardStatus.EXPIRED, BigDecimal.ZERO);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        ConflictException ex = assertThrows(ConflictException.class,
                () -> service.updateCardStatus(cardId, CardStatus.ACTIVE));

        assertEquals("INVALID_STATUS_TRANSITION", ex.getCode());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void updateCardStatus_updatesAndSaves_whenTransitionValid() {
        Long cardId = 6L;
        Card card = buildCard(6L, "4444", CardStatus.ACTIVE, BigDecimal.ZERO);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        CardResponse response = service.updateCardStatus(cardId, CardStatus.BLOCKED);

        assertEquals(CardStatus.BLOCKED, response.status());
        verify(cardRepository).save(card);
    }

    @Test
    void deleteCard_marksDeletedAndSaves() {
        Long cardId = 7L;
        Card card = buildCard(7L, "5555", CardStatus.ACTIVE, BigDecimal.ZERO);

        when(cardRepository.findByIdAndDeletedFalse(cardId)).thenReturn(Optional.of(card));

        service.deleteCard(cardId);

        assertTrue(card.getDeleted());
        verify(cardRepository).save(card);
    }

    @Test
    void deleteCard_throwsNotFound_whenMissing() {
        Long cardId = 8L;

        when(cardRepository.findByIdAndDeletedFalse(cardId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.deleteCard(cardId));

        assertEquals("NOT_FOUND_ERROR", ex.getCode());
        Violation violation = ex.getViolations().get(0);
        assertEquals("id", violation.field());
        assertEquals("Card with id " + cardId + " not found.", violation.message());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void getCardByIdForUser_throwsNotFound_whenMissing() {
        Long cardId = 101L;
        Long userId = 7L;

        when(cardRepository.findByIdAndOwnerIdAndDeletedFalse(cardId, userId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.getCardById(cardId, userId));

        assertEquals("NOT_FOUND_ERROR", ex.getCode());
        Violation violation = ex.getViolations().get(0);
        assertEquals("id", violation.field());
        assertEquals("Card with id " + cardId + " not found.", violation.message());
    }

    @Test
    void getCardByIdForUser_returnsDto_whenFound() {
        Long cardId = 102L;
        Long userId = 7L;
        Card card = buildCard(cardId, "6666", CardStatus.ACTIVE, BigDecimal.ONE);

        when(cardRepository.findByIdAndOwnerIdAndDeletedFalse(cardId, userId)).thenReturn(Optional.of(card));

        CardResponse response = service.getCardById(cardId, userId);

        assertEquals(cardId, response.id());
        assertEquals("**** **** **** 6666", response.maskedPan());
        verify(cardRepository).findByIdAndOwnerIdAndDeletedFalse(cardId, userId);
    }

    private Card buildCard(Long id, String last4, CardStatus status, BigDecimal balance) {
        User owner = new User("owner", "hash", null);
        Card card = new Card(owner, "panHash", last4, 1, 2030, status, balance);
        ReflectionTestUtils.setField(card, "id", id);
        return card;
    }
}

