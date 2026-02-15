package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.exception.UnauthorizedException;
import com.example.bankcards.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/transfers")
public class TransfersController {

    private final TransferService transferService;

    @PostMapping
    TransferResponse doTransfer(@RequestBody TransferRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("uid");

        if (userId == null)
            throw new UnauthorizedException();

        return transferService.transfer(request, userId);
    }
}
