package com.jztchl.splitwiseclonejava.dtos.expense;

import com.jztchl.splitwiseclonejava.models.Expenses;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ExpenseDetailDto {
    Long id;
    String description;
    BigDecimal amount;
    Expenses.SplitType splitType;
    List<ExpenseShareDto> shares;
    Long paidBy;
}