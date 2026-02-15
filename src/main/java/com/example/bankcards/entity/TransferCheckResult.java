package com.example.bankcards.entity;

import java.util.List;

public record TransferCheckResult(List<Violation> violations, List<Long> expiredCardIdsToMark) {
}
