package com.example.bankcards.entity;

import java.util.Optional;

public record TransferViolation(Violation violation, Optional<Long> expiredCardIdToMark) {
}
