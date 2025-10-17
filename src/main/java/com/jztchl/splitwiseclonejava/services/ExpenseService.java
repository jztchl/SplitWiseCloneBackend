package com.jztchl.splitwiseclonejava.services;

import com.jztchl.splitwiseclonejava.dtos.CreateExpenseDto;
import com.jztchl.splitwiseclonejava.models.*;
import com.jztchl.splitwiseclonejava.repos.ExpenseRepository;
import com.jztchl.splitwiseclonejava.repos.ExpenseShareRepository;
import com.jztchl.splitwiseclonejava.repos.GroupMembersRepository;
import com.jztchl.splitwiseclonejava.repos.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


@Service
public class ExpenseService {

    private final JwtService jwtService;
    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final ExpenseShareRepository expenseShareRepository;
    @Autowired
    public ExpenseService(JwtService jwtService, ExpenseRepository expenseRepository, GroupRepository groupRepository,GroupMembersRepository groupMembersRepository, ExpenseShareRepository expenseShareRepository) {
        this.jwtService = jwtService;
        this.expenseRepository = expenseRepository;
        this.groupRepository = groupRepository;
        this.expenseShareRepository = expenseShareRepository;
        this.groupMembersRepository = groupMembersRepository;
    }

    @Transactional
    public Expenses createExpense(CreateExpenseDto dto){
        Users currentUser=jwtService.getCurrentUser();
        Groups group =groupRepository.findById(dto.getGroupId()).orElseThrow(() -> new RuntimeException(String.format("Group not found id: %d", dto.getGroupId())));
        if (groupMembersRepository.findByGroupIdAndUserId(group, currentUser).isEmpty()) {
            throw new RuntimeException("You don't have permission to create expense for this group");
        }
        Expenses expense=new Expenses();
        expense.setAmount(dto.getAmount());
        expense.setGroupId(group);
        expense.setDescription(dto.getDescription());
        expense.setSplitType(dto.getSplitType());
        expense.setPaidBy(currentUser);
        expense=expenseRepository.save(expense);

        switch (dto.getSplitType()){
            case Expenses.SplitType.EQUAL :{
                if (dto.getMemberIds()==null){
                    BigDecimal amountOwed=dto.getAmount().divide(BigDecimal.valueOf(group.getMembers().size()-1),2, RoundingMode.HALF_UP);
                    for (GroupMembers member:group.getMembers()){
                        if (member.getUserId().equals(currentUser)){
                            continue;
                        }
                        ExpenseShare share=new ExpenseShare();
                        share.setExpense(expense);
                        share.setUserId(member.getUserId());
                        share.setAmountOwed(amountOwed);
                        expense.getShares().add(share);
                    }
                }
                else{
                    List<GroupMembers> membersList=groupMembersRepository.findAllById(dto.getMemberIds());
                    if (membersList.size()!=dto.getMemberIds().size()){
                        throw new RuntimeException("Invalid member ids");
                    }
                    BigDecimal amountOwed=dto.getAmount().divide(BigDecimal.valueOf(membersList.size()),2, RoundingMode.HALF_UP);
                    for (GroupMembers member:membersList){
                        ExpenseShare share=new ExpenseShare();
                        share.setExpense(expense);
                        share.setUserId(member.getUserId());
                        share.setAmountOwed(amountOwed);
                        expense.getShares().add(share);
                    }
                }
                break;

            }
            case Expenses.SplitType.PERCENTAGE:{
                if (dto.getSplitDetails()==null){
                    throw new RuntimeException("Split details are required for percentage split");
                }
                List<BigDecimal> percentages = new ArrayList<>(dto.getSplitDetails().values());
                BigDecimal totalPercent = percentages.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalPercent.compareTo(BigDecimal.valueOf(100)) != 0) {
                    throw new RuntimeException("Total percentage must equal 100");
                }

                List<Long> memberIds = new ArrayList<>(dto.getSplitDetails().keySet());
                List<GroupMembers> membersList = groupMembersRepository.findAllById(memberIds);

                if (membersList.size() != memberIds.size()) {
                    throw new RuntimeException("Invalid member ids in split details");
                }
                for (GroupMembers member : membersList) {
                    BigDecimal percent = dto.getSplitDetails()
                            .get(member.getUserId().getId());
                    BigDecimal amountOwed = dto.getAmount()
                            .multiply(percent)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                    ExpenseShare share = new ExpenseShare();
                    share.setExpense(expense);
                    share.setUserId(member.getUserId());
                    share.setAmountOwed(amountOwed);
                    expense.getShares().add(share);
                }
                break;

        }
        case Expenses.SplitType.EXACT:{
            if (dto.getSplitDetails()==null){
                throw new RuntimeException("Split details are required for exact split");
            }
            List<BigDecimal> amounts = new ArrayList<>(dto.getSplitDetails().values());
            BigDecimal totalAmount = amounts.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalAmount.compareTo(expense.getAmount()) != 0) {
                throw new RuntimeException("Total amount cannot exceed the actual amount");
            }
            List<Long> memberIds = new ArrayList<>(dto.getSplitDetails().keySet());
            List<GroupMembers> membersList = groupMembersRepository.findAllById(memberIds);

            if (membersList.size() != memberIds.size()) {
                throw new RuntimeException("Invalid member ids in split details");
            }
            for (GroupMembers member : membersList) {
                BigDecimal amountOwed=dto.getSplitDetails().get(member.getUserId().getId());

                ExpenseShare share = new ExpenseShare();
                share.setExpense(expense);
                share.setUserId(member.getUserId());
                share.setAmountOwed(amountOwed);
                expense.getShares().add(share);
            }
            break;
        }

        }
        expenseRepository.save(expense);
        return expense;
    }


}
