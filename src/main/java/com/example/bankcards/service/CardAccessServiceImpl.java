package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Violation;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CardAccessServiceImpl implements CardAccessService {

    private final CardRepository cardRepository;

    @Override
    public Card getOwnedCardOrThrow(Long cardId, Long userId, String field) {
        return cardRepository.findByIdAndOwnerIdAndDeletedFalse(cardId, userId)
                .orElseThrow(() -> new NotFoundException(
                        List.of(new Violation(field, "Card with id " + cardId + " not found on user."))
                ));
    }
}
