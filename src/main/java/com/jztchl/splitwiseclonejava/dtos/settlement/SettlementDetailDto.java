package com.jztchl.splitwiseclonejava.dtos.settlement;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SettlementDetailDto {
    Long id;
    String status;
    String note;
    String paidBy;
    Long paidById;
    Long expenseShareId;
    String paymentRef;
}
