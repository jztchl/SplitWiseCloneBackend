package com.jztchl.splitwiseclonejava.dtos;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseShareDto {
    Long id;
    Long userId;
    BigDecimal amountOwed;

}
