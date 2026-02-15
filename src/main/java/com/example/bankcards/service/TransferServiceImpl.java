package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.TransferStatus;
import com.example.bankcards.entity.Violation;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.validation.validators.RequestValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TransferServiceImpl implements TransferService {

    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;
    private final RequestValidator<TransferRequest> requestValidator;

    @Transactional
    @Override
    public TransferResponse transfer(TransferRequest request) {
        requestValidator.validate(request);

        Card from = cardRepository.findByIdAndDeletedFalse(request.fromCardId()).orElseThrow(() -> new NotFoundException(
                List.of(new Violation("fromCardId", "Card with id " + request.fromCardId() + " not found."))
        ));
        Card to = cardRepository.findByIdAndDeletedFalse(request.fromCardId()).orElseThrow(() -> new NotFoundException(
                List.of(new Violation("toCardId", "Card with id " + request.toCardId() + " not found."))
        ));

        if (!from.getOwner().equals(to.getOwner())) {
            throw new ForbiddenException(
                    List.of(new Violation("toCardId", "Cannot transfer to other user's card."))
            );
        }

        from.setBalance(from.getBalance().subtract(request.amount()));
        to.setBalance(to.getBalance().add(request.amount()));

        return transferRepository.save(new Transfer(from, to, request.amount(), TransferStatus.SUCCESS)).toDTO();
    }
}
