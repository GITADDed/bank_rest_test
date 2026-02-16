package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Violation;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardAccessServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardAccessServiceImpl service;

    @Test
    void getOwnedCardOrThrow_returnsCard_whenOwnedAndNotDeleted() {
        Long cardId = 10L;
        Long userId = 99L;
        String field = "cardId";
        Card card = org.mockito.Mockito.mock(Card.class);

        when(cardRepository.findByIdAndOwnerIdAndDeletedFalse(cardId, userId))
                .thenReturn(Optional.of(card));

        Card result = service.getOwnedCardOrThrow(cardId, userId, field);

        assertSame(card, result);
        verify(cardRepository).findByIdAndOwnerIdAndDeletedFalse(cardId, userId);
    }

    @Test
    void getOwnedCardOrThrow_throwsNotFound_whenMissing() {
        Long cardId = 42L;
        Long userId = 7L;
        String field = "cardId";

        when(cardRepository.findByIdAndOwnerIdAndDeletedFalse(cardId, userId))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.getOwnedCardOrThrow(cardId, userId, field));

        assertEquals("NOT_FOUND_ERROR", ex.getCode());
        assertEquals(1, ex.getViolations().size());
        Violation violation = ex.getViolations().get(0);
        assertEquals(field, violation.field());
        assertEquals("Card with id " + cardId + " not found on user.", violation.message());
        verify(cardRepository).findByIdAndOwnerIdAndDeletedFalse(cardId, userId);
    }
}