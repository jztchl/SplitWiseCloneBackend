package com.jztchl.splitwiseclonejava.services;

import com.jztchl.splitwiseclonejava.dtos.settlement.createSettlementDto;
import com.jztchl.splitwiseclonejava.models.ExpenseShare;
import com.jztchl.splitwiseclonejava.models.Settlement;
import com.jztchl.splitwiseclonejava.repos.DocRepository;
import com.jztchl.splitwiseclonejava.repos.ExpenseRepository;
import com.jztchl.splitwiseclonejava.repos.ExpenseShareRepository;
import com.jztchl.splitwiseclonejava.repos.SettlementRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SettlementService {

    private final JwtService jwtService;
    private final ExpenseShareRepository expenseShareRepository;
    private final ExpenseRepository expenseRepository;
    private final SettlementRepository settlementRepository;
    private final DocRepository docRepository;


    public SettlementService(JwtService jwtService, ExpenseShareRepository expenseShareRepository
            , DocRepository docRepository, ExpenseRepository expenseRepository
            , SettlementRepository settlementRepository) {
        this.jwtService = jwtService;
        this.expenseShareRepository = expenseShareRepository;
        this.expenseRepository = expenseRepository;
        this.settlementRepository = settlementRepository;
        this.docRepository = docRepository;
    }

    public createSettlementDto createSettlement(createSettlementDto dto) {
        if (!expenseShareRepository.existsByIdAndUserId(dto.getExpenseShareId(), jwtService.getCurrentUser())) {
            throw new RuntimeException("Expense share not found");
        }
        List<Settlement> settlements = new ArrayList<>();
        settlements = settlementRepository.findAllByStatusAndExpenseShare_Id(Settlement.SettlementStatus.CONFIRMED, dto.getExpenseShareId());

        BigDecimal totalPayment = BigDecimal.valueOf(0);
        for (Settlement settlement : settlements) {
            totalPayment = totalPayment.add(settlement.getAmount());
        }
        if (totalPayment.compareTo(dto.getAmount()) > 0) {
            throw new RuntimeException("Total payment cannot exceed the amount to be paid");
        }

        Settlement settlement = new Settlement();
        settlement.setExpense(expenseRepository.getReferenceById(dto.getExpenseId()));
        settlement.setPayer(jwtService.getCurrentUser());
        settlement.setAmount(dto.getAmount());
        settlement.setExpenseShare(expenseShareRepository.getReferenceById(dto.getExpenseShareId()));
        settlement.setPaymentRef(docRepository.getReferenceById(dto.getPaymentRef()));
        settlement.setNote(dto.getNote());
        settlement = settlementRepository.save(settlement);
        dto.setId(settlement.getId());
        dto.setStatus(String.valueOf(settlement.getStatus()));

        return dto;


    }
}
