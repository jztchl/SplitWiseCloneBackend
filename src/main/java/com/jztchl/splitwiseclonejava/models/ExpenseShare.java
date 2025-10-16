package com.jztchl.splitwiseclonejava.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "expense_shares")
public class ExpenseShare extends BaseModel{
    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private Expenses expense;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users userId; // the member who owes money

    private BigDecimal amountOwed;

    private boolean isPaid = false;
}
