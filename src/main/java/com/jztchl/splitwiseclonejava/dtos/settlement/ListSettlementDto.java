package com.jztchl.splitwiseclonejava.dtos.settlement;

import com.jztchl.splitwiseclonejava.dtos.ProfileDto;

import java.math.BigDecimal;

public interface ListSettlementDto {
    Long getId();

    String getStatus();

    BigDecimal getAmount();

    String getPayerName();


}
