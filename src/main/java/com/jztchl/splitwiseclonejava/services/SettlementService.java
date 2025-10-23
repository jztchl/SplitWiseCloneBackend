package com.jztchl.splitwiseclonejava.services;

import com.jztchl.splitwiseclonejava.dtos.settlement.CreateSettlementDto;
import com.jztchl.splitwiseclonejava.dtos.settlement.ListSettlementDto;
import com.jztchl.splitwiseclonejava.dtos.settlement.MarkSettlementDto;
import com.jztchl.splitwiseclonejava.dtos.settlement.SettlementDetailDto;
import com.jztchl.splitwiseclonejava.models.Expenses;
import com.jztchl.splitwiseclonejava.models.Settlement;
import com.jztchl.splitwiseclonejava.repos.DocRepository;
import com.jztchl.splitwiseclonejava.repos.ExpenseRepository;
import com.jztchl.splitwiseclonejava.repos.ExpenseShareRepository;
import com.jztchl.splitwiseclonejava.repos.SettlementRepository;
import com.jztchl.splitwiseclonejava.utility.EmailService;
import com.jztchl.splitwiseclonejava.utility.MiscCalculations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SettlementService {

    private final JwtService jwtService;
    private final MiscCalculations miscCalculations;
    private final ExpenseShareRepository expenseShareRepository;
    private final ExpenseRepository expenseRepository;
    private final SettlementRepository settlementRepository;
    private final DocRepository docRepository;
    private final EmailService emailService;


    public SettlementService(JwtService jwtService, ExpenseShareRepository expenseShareRepository
            , DocRepository docRepository, ExpenseRepository expenseRepository
            , SettlementRepository settlementRepository
            , MiscCalculations miscCalculations, EmailService emailService) {
        this.jwtService = jwtService;
        this.expenseShareRepository = expenseShareRepository;
        this.expenseRepository = expenseRepository;
        this.settlementRepository = settlementRepository;
        this.docRepository = docRepository;
        this.miscCalculations = miscCalculations;
        this.emailService = emailService;

    }

    public CreateSettlementDto createSettlement(CreateSettlementDto dto) {
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
        emailService.newSettlementNotification(settlement.getId());

        return dto;


    }


    public String markSettlmentStatus(MarkSettlementDto dto) {
        Settlement settlement = settlementRepository.findById(dto.getSettlementId())
                .orElseThrow(() -> new RuntimeException("Settlement not found"));
        if (!settlement.getExpense().getPaidBy().equals(jwtService.getCurrentUser())) {
            throw new RuntimeException("You do not have permission to mark this settlement");
        }
        settlement.setStatus(Settlement.SettlementStatus.valueOf(dto.getStatus()));
        if (Settlement.SettlementStatus.CONFIRMED.equals(settlement.getStatus())) {
            settlement.setConfirmedAt(LocalDateTime.now());
        }
        settlementRepository.save(settlement);
        emailService.paymentConfirmedNotification(settlement.getId());
        miscCalculations.updateStatusExpense(settlement.getExpenseShare().getId());

        return "Settlement marked successfully";
    }

    public List<ListSettlementDto> listSettlements(Long expenseId) {
        Expenses expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (expense.getPaidBy().equals(jwtService.getCurrentUser())) {// list all settlements for this expense
            return settlementRepository.findAllByExpenseId(expenseId);
        } else {// list only settlements paid by current user
            return settlementRepository.findAllByExpenseIdAndExpenseShare_UserId(
                    expenseId,
                    jwtService.getCurrentUser()
            );
        }

    }


    public SettlementDetailDto getSettlement(Long id) {
        Settlement settlement = settlementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Settlement not found"));
        if (!((settlement.getExpense().getPaidBy().equals(jwtService.getCurrentUser()) ||
                settlement.getExpenseShare().getUserId().equals(jwtService.getCurrentUser())))) {
            throw new RuntimeException("You do not have permission to view this settlement");
        }
        SettlementDetailDto dto = new SettlementDetailDto();
        dto.setId(settlement.getId());
        dto.setStatus(settlement.getStatus().toString());
        dto.setNote(settlement.getNote());
        dto.setPaidBy(settlement.getPayer().getName());
        dto.setPaidById(Long.valueOf(settlement.getPayer().getId()));
        dto.setExpenseShareId(settlement.getExpenseShare().getId());
        dto.setPaymentRef(settlement.getPaymentRef().getUrl());
        return dto;
    }
}
