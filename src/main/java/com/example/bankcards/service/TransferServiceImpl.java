package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.ConflictException;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.validation.validators.business.TransferValidator;
import com.example.bankcards.validation.validators.request.RequestValidator;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final RequestValidator<TransferRequest> requestValidator;
    private final TransferValidator transferValidator;
    private final CardAccessService cardAccessService;
    private final CardStatusService cardStatusService;

    @Transactional
    @Override
    public TransferResponse transfer(TransferRequest request, Long userId) {
        requestValidator.validate(request);


        Card from = cardAccessService.getOwnedCardOrThrow(request.fromCardId(), userId, "fromCardId");
        Card to = cardAccessService.getOwnedCardOrThrow(request.toCardId(), userId, "toCardId");

        TransferCheckResult result = transferValidator.validate(new TransferData(from, to, request));

        for (Long cardId : result.expiredCardIdsToMark()) {
            cardStatusService.markExpired(cardId);
        }

        if (!result.violations().isEmpty()) {
            throw new ConflictException(result.violations());
        }

        from.setBalance(from.getBalance().subtract(request.amount()));
        to.setBalance(to.getBalance().add(request.amount()));

        return transferRepository.save(new Transfer(from, to, request.amount(), TransferStatus.SUCCESS)).toDTO();
    }
}
