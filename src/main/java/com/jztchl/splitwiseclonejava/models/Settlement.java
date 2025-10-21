package com.jztchl.splitwiseclonejava.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlements")
public class Settlement extends BaseModel {
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Groups group;

    @ManyToOne
    @JoinColumn(name = "payer_id", nullable = false)
    private Users payer;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Users receiver;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private SettlementStatus status;

    @ManyToOne
    @JoinColumn(name = "payment_ref_id")
    private Doc paymentRef;
    private String note;

    private String paymentProofUrl;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime confirmedAt;

    private Long confirmedBy;

    public enum SettlementStatus {
        PENDING,
        CONFIRMED,
        REJECTED
    }
}
