package com.jztchl.splitwiseclonejava.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "groups")
public class Groups extends BaseModel {
    @NotBlank(message = "Group name is required")
    @Column(nullable = false)
    private String groupName;

    @NotBlank(message = "Description is required")
    @Column(nullable = false)
    private String description;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private Users createdBy;

    @NotNull
    @OneToMany(mappedBy = "groupId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMembers> members = new ArrayList<>();

    public Groups() {
    }

}
