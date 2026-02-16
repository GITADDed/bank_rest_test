package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ConflictException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.entity.Violation;
import com.example.bankcards.util.PanHashUtil;
import com.example.bankcards.validation.validators.request.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CardServiceImpl implements CardService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final RequestValidator<CardRequest> requestValidator;

    @Override
    public CardResponse createCard(CardRequest request) {
        requestValidator.validate(request);
        String last4 = request.pan().substring(request.pan().length() - 4);
        String panHash = PanHashUtil.sha256Hex("", request.pan());

        if (cardRepository.existsByOwnerIdAndPanHashAndDeletedFalse(request.ownerId(), panHash)) {
            throw new ConflictException(List.of(new Violation("pan", "Card already exists for this owner.")));
        }

        Card card = new Card(
                userRepository.findById(
                        request.ownerId()).orElseThrow(() -> new NotFoundException(
                        List.of(new Violation("ownerId", "User with id "
                                + request.ownerId() + " not found."))
                )),
                panHash,
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

    @Override
    public Page<CardResponse> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable).map(Card::toDTO);
    }

    @Override
    public Page<CardResponse> getMyAllCards(Long ownerId, Pageable pageable) {
        User user = userRepository.findByIdAndDeletedFalse(ownerId).orElseThrow(() -> new NotFoundException(
                List.of(new Violation("ownerId", "User with id " + ownerId + " not found."))
        ));

        Page<Card> page = cardRepository.findAllByOwnerAndDeletedFalse(user, pageable);

        return page.map(Card::toDTO);
    }

    @Override
    public CardResponse getCardById(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new NotFoundException(
                List.of(new Violation("id", "Card with id " + id + " not found."))
        )).toDTO();
    }

    @Override
    public CardResponse getCardById(Long id, Long userId) {
        return cardRepository.findByIdAndOwnerIdAndDeletedFalse(id, userId).orElseThrow(() -> new NotFoundException(
                List.of(new Violation("id", "Card with id " + id + " not found."))
        )).toDTO();
    }

    @Override
    public CardResponse updateCardStatus(Long id, CardStatus newStatus) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new NotFoundException(
                List.of(new Violation("id", "Card with id " + id + " not found."))
        ));

        if (!card.getStatus().canTransitionTo(newStatus)) {
            throw new ConflictException(List.of(), "Cannot change status from "
                    + card.getStatus() + " to " + newStatus + ".", "INVALID_STATUS_TRANSITION");
        }

        card.setStatus(newStatus);
        return cardRepository.save(card).toDTO();
    }

    @Override
    public void deleteCard(Long id) {
        Card card = cardRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException(
                List.of(new Violation("id", "Card with id " + id + " not found."))
        ));

        card.setDeleted(true);
        cardRepository.save(card);
    }
}
