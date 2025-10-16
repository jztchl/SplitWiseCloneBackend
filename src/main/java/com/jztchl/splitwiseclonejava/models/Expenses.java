package com.jztchl.splitwiseclonejava.models;

import jakarta.persistence.*;
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
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Groups groupId;
    private String description;
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "paid_by", nullable = false)
    private Users paidBy;
    @Enumerated(EnumType.STRING)
    private SplitType splitType;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseShare> shares = new ArrayList<>();

    public enum SplitType {
        EQUAL,
        PERCENTAGE,
        EXACT
    }

}