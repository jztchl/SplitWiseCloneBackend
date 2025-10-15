package com.jztchl.splitwiseclonejava.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "groups")
public class Groups extends BaseModel{

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private Users createdBy;

    public Groups() {
    }




}
