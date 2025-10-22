package com.jztchl.splitwiseclonejava.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlements")
@EqualsAndHashCode(callSuper = true)
@Data
public class Settlement extends BaseModel {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "expense_share_id")
    private ExpenseShare expenseShare;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "expense_id")
    private Expenses expense;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "payer_id", nullable = false)
    private Users payer;

    @NotNull
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private SettlementStatus status = SettlementStatus.PENDING;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "payment_ref_id")
    private Doc paymentRef;

    private String note;

    private LocalDateTime confirmedAt;

    public enum SettlementStatus {
        PENDING,
        CONFIRMED,
        REJECTED
    }
}
