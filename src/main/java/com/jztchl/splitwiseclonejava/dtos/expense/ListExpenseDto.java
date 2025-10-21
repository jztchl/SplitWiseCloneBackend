package com.jztchl.splitwiseclonejava.dtos.expense;

import java.math.BigDecimal;

public interface ListExpenseDto {
    Long getId();

    String getdescription();

    BigDecimal getAmount();
}
