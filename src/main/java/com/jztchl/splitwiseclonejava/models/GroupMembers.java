package com.jztchl.splitwiseclonejava.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "group_members", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "group_id", "user_id" })
})
@Data
public class GroupMembers extends BaseModel {

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Groups groupId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users userId;

    private Date joinedAt;

    public GroupMembers() {

    }
}
