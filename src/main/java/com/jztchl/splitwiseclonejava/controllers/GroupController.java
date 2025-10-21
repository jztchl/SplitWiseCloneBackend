package com.jztchl.splitwiseclonejava.controllers;

import com.jztchl.splitwiseclonejava.dtos.expense.ListExpenseDto;
import com.jztchl.splitwiseclonejava.dtos.group.AddGroupMembers;
import com.jztchl.splitwiseclonejava.dtos.group.CreateGroupDto;
import com.jztchl.splitwiseclonejava.dtos.group.GroupDetailsDto;
import com.jztchl.splitwiseclonejava.dtos.group.GroupListDto;
import com.jztchl.splitwiseclonejava.models.Groups;
import com.jztchl.splitwiseclonejava.services.ExpenseService;
import com.jztchl.splitwiseclonejava.services.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    private final GroupService groupService;
    private final ExpenseService expenseService;

    public GroupController(GroupService groupService, ExpenseService expenseService) {
        this.groupService = groupService;
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<GroupListDto> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{id}")
    public GroupDetailsDto getGroupById(@PathVariable Long id) {
        return groupService.getGroupById(id);
    }

    @PostMapping("/create-group")
    public Groups createGroup(@RequestBody CreateGroupDto group) {
        return groupService.createGroup(group);
    }

    @PostMapping("/{id}/add-member")
    public String addMember(@PathVariable Long id, @RequestBody AddGroupMembers members) {
        groupService.addMemberToGroup(id, members.getMembers());
        return "ok";

    }

    @GetMapping("/{id}/get-expenses")
    public List<ListExpenseDto> getExpense(@PathVariable Long id) {
        return expenseService.listExpenses(id);

    }
}
