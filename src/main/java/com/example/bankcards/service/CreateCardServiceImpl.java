package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardStatus;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.Violation;
import com.example.bankcards.validation.validators.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateCardServiceImpl implements CreateCardService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final RequestValidator<CardRequest> requestValidator;

    @Override
    public CardResponse createCard(CardRequest request) {
        requestValidator.validate(request);
        String last4 = request.pan().substring(request.pan().length() - 4);
        Card card = new Card(
                userRepository.findById(
                        request.ownerId()).orElseThrow(() -> new NotFoundException(
                                List.of(new Violation("ownerId", "User with id " + request.ownerId() + " not found."))
                        )),
                last4,
                request.expiryMonth(),
                request.expiryYear(),
                CardStatus.ACTIVE,
                BigDecimal.ZERO
        );

        card = cardRepository.save(card);

        return new CardResponse(card.getId(),
                "**** **** **** " + card.getLast4(),
                card.getLast4(),
                card.getExpiryMonth(),
                card.getExpiryYear(),
                card.getStatus(),
                card.getBalance());
    }
}
