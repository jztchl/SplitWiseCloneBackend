package com.jztchl.splitwiseclonejava.dtos.expense;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseShareDto {
    Long id;
    Long userId;
    BigDecimal amountOwed;
    private BigDecimal amountRemaining = BigDecimal.valueOf(0);// done
    boolean isPaid;

}
