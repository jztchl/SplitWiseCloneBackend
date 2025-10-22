package com.jztchl.splitwiseclonejava.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "expense_shares")
public class ExpenseShare extends BaseModel {
    @NotNull
    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private Expenses expense;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users userId; // the member who owes money

    @NotNull
    private BigDecimal amountOwed;

    private boolean isPaid = false;
}
