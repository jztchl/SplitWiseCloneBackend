package com.jztchl.splitwiseclonejava.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class GroupCreatedEvent extends ApplicationEvent {
    private final Long groupId;

    public GroupCreatedEvent(Object source, Long groupId) {
        super(source);
        this.groupId = groupId;
    }

}