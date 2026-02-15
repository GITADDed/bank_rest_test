package com.example.bankcards.entity;

import com.example.bankcards.dto.TransferResponse;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "transfers")
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "from_card_id", nullable = false)
    private Card fromCard;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_card_id", nullable = false)
    private Card toCard;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransferStatus status;

    public Transfer() {
    }

    public Transfer(Card fromCard, Card toCard, BigDecimal amount, TransferStatus status) {
        this.fromCard = fromCard;
        this.toCard = toCard;
        this.amount = amount;
        this.status = status;
    }

    public TransferResponse toDTO() {
        return new TransferResponse(id, fromCard.getId(), toCard.getId(), status);
    }
}
