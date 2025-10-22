package com.jztchl.splitwiseclonejava.dtos.settlement;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateSettlementDto {
    private Long id;
    private String status;

    @NotNull(message = "Expense share ID is required")
    private Long expenseShareId;

    @NotNull(message = "Expense ID is required")
    private Long expenseId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Payment reference is required")
    @Positive(message = "Payment reference must be positive")
    private Long paymentRef;

    @Size(max = 500, message = "Note cannot exceed 500 characters")
    private String note;
}
