package com.jztchl.splitwiseclonejava.utility;

import com.jztchl.splitwiseclonejava.models.*;
import com.jztchl.splitwiseclonejava.repos.ExpenseRepository;
import com.jztchl.splitwiseclonejava.repos.GroupRepository;
import com.jztchl.splitwiseclonejava.repos.SettlementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;
    private final SettlementRepository settlementRepository;


    public EmailService(JavaMailSender javaMailSender, GroupRepository groupRepository,
                        ExpenseRepository expenseRepository, SettlementRepository settlementRepository) {
        this.javaMailSender = javaMailSender;
        this.groupRepository = groupRepository;
        this.expenseRepository = expenseRepository;
        this.settlementRepository = settlementRepository;
    }

    @Async
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
            System.out.println("Email sent to:" + to);
        } catch (Exception e) {
            System.out.println(e.getMessage() + "Failed to send email to:" + to);
        }
    }

    @Async
    public void addedToGroupNotification(Long groupId) {
        Groups groups = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
        final String subject = "You have been added to a group";
        String text = String.format("You have been added to %s", groups.getGroupName());
        for (GroupMembers member : groups.getMembers()) {
            sendEmail(
                    member.getUserId().getEmail(),
                    subject,
                    text
            );
        }

    }

    @Async
    public void newExpenseSharedNotification(Long expenseId) {
        Expenses expense = expenseRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("Expense not found"));
        String subject = "New expense shared with you";
        for (ExpenseShare share : expense.getShares()) {
            String text = String.format("""
                            A new expense have been shared with you , the total amount is %s. \
                            and the expense is paid by %s. you have to pay %s to %s as per the split\s
                            
                             with description
                             %s"""
                    , expense.getAmount().toString(), expense.getPaidBy().getName(),
                    share.getAmountOwed().toString(), expense.getPaidBy().getName()
                    , expense.getDescription());

            sendEmail(
                    share.getUserId().getEmail(),
                    subject,
                    text
            );
        }

    }

    @Async
    public void paymentConfirmedNotification(Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId).orElseThrow(() -> new RuntimeException("Settlement not found"));
        String subject = "Payment Confirmed";
        String text = String.format("""
                Payment of %s has been confirmed by %s.
                """, settlement.getAmount().toString(), settlement.getPayer().getName());
        sendEmail(settlement.getExpenseShare().getUserId().getEmail(), subject, text);
    }

    @Async
    public void expensePaymentsClearedNotification(Long expenseId) {
        Expenses expense = expenseRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("Expense not found"));
        String subject = "All payments have been cleared";
        String text = String.format("""
                All payments have been cleared for %s.
                """, expense.getDescription());
        sendEmail(expense.getPaidBy().getEmail(), subject, text);
    }
}

