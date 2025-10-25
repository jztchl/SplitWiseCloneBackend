package com.jztchl.splitwiseclonejava.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ExpenseCreatedEvent extends ApplicationEvent {
    private final Long expenseId;

    public ExpenseCreatedEvent(Object source, Long expenseId) {
        super(source);
        this.expenseId = expenseId;
    }

}