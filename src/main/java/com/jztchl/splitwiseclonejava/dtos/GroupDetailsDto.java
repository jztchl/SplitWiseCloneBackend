package com.jztchl.splitwiseclonejava.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupDetailsDto {
    Long id;
    String name;
    String description;
    List<GroupMembersDto> members;

}
