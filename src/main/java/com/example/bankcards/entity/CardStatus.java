package com.example.bankcards.entity;

public enum CardStatus {
    ACTIVE,
    EXPIRED,
    BLOCKED;

    public boolean canTransitionTo(CardStatus target) {
        return switch (this) {
            case ACTIVE -> target == BLOCKED;
            case BLOCKED -> target == ACTIVE;
            case EXPIRED -> false;
        };
    }
}

