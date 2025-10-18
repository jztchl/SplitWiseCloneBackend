package com.jztchl.splitwiseclonejava.controllers;

import com.jztchl.splitwiseclonejava.dtos.AddGroupMembers;
import com.jztchl.splitwiseclonejava.dtos.CreateGroupDto;
import com.jztchl.splitwiseclonejava.dtos.GroupDetailsDto;
import com.jztchl.splitwiseclonejava.dtos.GroupListDto;
import com.jztchl.splitwiseclonejava.models.Groups;
import com.jztchl.splitwiseclonejava.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController
{
    private final GroupService groupService;
    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }


    @GetMapping
    public List<GroupListDto> getAllGroups(){
        return groupService.getAllGroups();
    }

    @GetMapping("/{id}")
    public GroupDetailsDto getGroupById(@PathVariable Long id){
        return groupService.getGroupById(id);
    }

    @PostMapping("/create-group")
    public Groups createGroup(@RequestBody CreateGroupDto group){
        return groupService.createGroup(group);
    }

    @PostMapping("/{id}/add-member")
    public String addMember(@PathVariable Long id, @RequestBody AddGroupMembers members){
        groupService.addMemberToGroup(id,members.getMembers());
        return "ok";

    }
}
