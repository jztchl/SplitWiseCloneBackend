package com.jztchl.splitwiseclonejava.dtos.group;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateGroupDto {
    private String groupName;
    private String groupDescription;
    private List<String> members;

    public CreateGroupDto() {
    }
}
