package com.jztchl.splitwiseclonejava.dtos;

import com.jztchl.splitwiseclonejava.models.Expenses;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class CreateExpenseDto {
    private Long groupId;
    private String description;
    private BigDecimal amount;
    private Expenses.SplitType splitType;
    private Map<Long, BigDecimal> splitDetails;
}
