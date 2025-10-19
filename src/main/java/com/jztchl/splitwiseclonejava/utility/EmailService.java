package com.jztchl.splitwiseclonejava.utility;

import com.jztchl.splitwiseclonejava.models.ExpenseShare;
import com.jztchl.splitwiseclonejava.models.Expenses;
import com.jztchl.splitwiseclonejava.models.GroupMembers;
import com.jztchl.splitwiseclonejava.models.Groups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
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

    public void addedToGroupNotification(Groups groups) {
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

    public void newExpenseSharedNotification(Expenses expense) {
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


}

