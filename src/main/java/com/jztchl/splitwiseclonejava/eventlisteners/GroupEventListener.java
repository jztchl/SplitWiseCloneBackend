package com.jztchl.splitwiseclonejava.eventlisteners;

import com.jztchl.splitwiseclonejava.events.GroupCreatedEvent;
import com.jztchl.splitwiseclonejava.utility.EmailService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class GroupEventListener {

    private final EmailService emailService;

    public GroupEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGroupCreatedEvent(GroupCreatedEvent event) {
        try {
            emailService.addedToGroupNotification(event.getGroupId());
            System.out.println("Group created successfully email notification will be sent shortly.");
        } catch (Exception e) {
            System.err.println("Error in post-commit event processing: " + e.getMessage());
        }
    }
}