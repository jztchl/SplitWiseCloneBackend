package com.jztchl.splitwiseclonejava.dtos;

import java.math.BigDecimal;

import com.jztchl.splitwiseclonejava.models.Users;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseShareDto {
    Long id;
    Long userId;
    BigDecimal amountOwed;

}
