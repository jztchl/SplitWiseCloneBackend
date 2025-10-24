package com.jztchl.splitwiseclonejava.utility;

import com.jztchl.splitwiseclonejava.models.ExpenseShare;
import com.jztchl.splitwiseclonejava.models.Settlement;
import com.jztchl.splitwiseclonejava.repos.ExpenseRepository;
import com.jztchl.splitwiseclonejava.repos.ExpenseShareRepository;
import com.jztchl.splitwiseclonejava.repos.SettlementRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MiscCalculations {
    private final SettlementRepository settlementRepository;
    private final ExpenseShareRepository expenseShareRepository;
    private final ExpenseRepository expenseRepository;
    private final EmailService emailService;
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(MiscCalculations.class);

    public MiscCalculations(SettlementRepository settlementRepository, ExpenseShareRepository expenseShareRepository
            , ExpenseRepository expensesRepository, EmailService emailService) {
        this.settlementRepository = settlementRepository;
        this.expenseShareRepository = expenseShareRepository;
        this.expenseRepository = expensesRepository;
        this.emailService = emailService;

    }

    @Async
    public void updateStatusExpense(Long expenseShareId) {
        ExpenseShare share = expenseShareRepository.findById(expenseShareId).orElseThrow(() -> new RuntimeException("Expense share not found"));
        BigDecimal totalPayment = calculateAmountTillNow(share);

        if (totalPayment.compareTo(share.getAmountOwed()) == 0) {
            share.setPaid(true);
            expenseShareRepository.save(share);
        }
        List<ExpenseShare> shares = expenseShareRepository.findAllByExpense(share.getExpense());
        for (ExpenseShare s : shares) {
            if (!s.isPaid()) {
                return;
            }

        }
        share.getExpense().setIsPaymentsDone(true);
        expenseRepository.save(share.getExpense());
        emailService.expensePaymentsClearedNotification(share.getExpense().getId());
        logger.info("Expense  id: {} payments cleared successfully", share.getId());


    }

    public BigDecimal calculateAmountTillNow(ExpenseShare share) {
        List<Settlement> settlements = settlementRepository.findAllByStatusAndExpenseShare_Id(Settlement.SettlementStatus.CONFIRMED, share.getId());
        BigDecimal totalPayment = BigDecimal.valueOf(0);
        for (Settlement st : settlements) {
            totalPayment = totalPayment.add(st.getAmount());
        }
        return totalPayment;
    }
}
