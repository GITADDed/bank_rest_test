package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardStatusServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardStatusServiceImpl service;

    @Test
    void markExpired_setsStatus_whenNotExpired() {
        Long cardId = 1L;
        Card card = org.mockito.Mockito.mock(Card.class);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(card.getStatus()).thenReturn(CardStatus.ACTIVE);

        service.markExpired(cardId);

        verify(card).setStatus(CardStatus.EXPIRED);
    }

    @Test
    void markExpired_doesNothing_whenAlreadyExpired() {
        Long cardId = 2L;
        Card card = org.mockito.Mockito.mock(Card.class);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(card.getStatus()).thenReturn(CardStatus.EXPIRED);

        service.markExpired(cardId);

        verify(card, never()).setStatus(CardStatus.EXPIRED);
    }

    @Test
    void markExpired_throws_whenCardMissing() {
        Long cardId = 3L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.markExpired(cardId));
    }
}

