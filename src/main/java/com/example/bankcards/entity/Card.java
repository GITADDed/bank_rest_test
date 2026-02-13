package com.example.bankcards.entity;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String last4;

    @Column(name = "expiry_month", nullable = false)
    private Integer expiryMonth;

    @Column(name = "expiry_year", nullable = false)
    private Integer expiryYear;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @Column(nullable = false)
    private BigDecimal balance;

    protected Card() {}

    public Card(User owner, String last4, Integer expiryMonth, Integer expiryYear, CardStatus status, BigDecimal balance) {
        this.owner = owner;
        this.last4 = last4;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.status = status;
        this.balance = balance;
    }

    public CardResponse toDTO() {
        return new CardResponse(id,
                "**** **** **** " + last4,
                last4,
                expiryMonth,
                expiryYear,
                status,
                balance);
    }
}
