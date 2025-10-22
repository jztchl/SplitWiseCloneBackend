package com.jztchl.splitwiseclonejava.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expenses")
@Data
@EqualsAndHashCode(callSuper = true)
public class Expenses extends BaseModel {
    @NotNull(message = "Group is required")
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Groups groupId;

    private String description;
    private BigDecimal amount;

    @NotNull(message = "Paid by is required")
    @ManyToOne
    @JoinColumn(name = "paid_by", nullable = false)
    private Users paidBy;

    @NotNull(message = "Split type is required")
    @Enumerated(EnumType.STRING)
    private SplitType splitType;

    private Boolean isPaymentsDone = Boolean.FALSE;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseShare> shares = new ArrayList<>();

    public enum SplitType {
        EQUAL,
        PERCENTAGE,
        EXACT
    }


}