package com.jztchl.splitwiseclonejava.eventlisteners;

import com.jztchl.splitwiseclonejava.events.ExpenseCreatedEvent;
import com.jztchl.splitwiseclonejava.events.GroupCreatedEvent;
import com.jztchl.splitwiseclonejava.utility.EmailService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ExpenseEventListener {

    private final EmailService emailService;

    public ExpenseEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleExpenseCreatedEvent(ExpenseCreatedEvent event) {
        try {
            emailService.addedToGroupNotification(event.getExpenseId());
            System.out.println("Expense created successfully email notification will be sent shortly.");
        } catch (Exception e) {
            System.err.println("Error in post-commit event processing: " + e.getMessage());
        }
    }
}