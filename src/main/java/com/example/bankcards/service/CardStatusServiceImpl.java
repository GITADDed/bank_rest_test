package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CardStatusServiceImpl implements CardStatusService {
    private final CardRepository cardRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markExpired(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow();
        if (card.getStatus() != CardStatus.EXPIRED) {
            card.setStatus(CardStatus.EXPIRED);
        }
    }
}

