package com.jztchl.splitwiseclonejava.services;

import com.jztchl.splitwiseclonejava.dtos.expense.CreateExpenseDto;
import com.jztchl.splitwiseclonejava.dtos.expense.ExpenseDetailDto;
import com.jztchl.splitwiseclonejava.dtos.expense.ExpenseShareDto;
import com.jztchl.splitwiseclonejava.dtos.expense.ListExpenseDto;
import com.jztchl.splitwiseclonejava.models.*;
import com.jztchl.splitwiseclonejava.repos.ExpenseRepository;
import com.jztchl.splitwiseclonejava.repos.GroupMembersRepository;
import com.jztchl.splitwiseclonejava.repos.GroupRepository;
import com.jztchl.splitwiseclonejava.repos.SettlementRepository;
import com.jztchl.splitwiseclonejava.utility.EmailService;

import com.jztchl.splitwiseclonejava.utility.MiscCalculations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class ExpenseService {

    private final JwtService jwtService;
    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final EmailService emailService;
    private final MiscCalculations miscCalculations;


    public ExpenseService(JwtService jwtService, ExpenseRepository expenseRepository, GroupRepository groupRepository,
                          GroupMembersRepository groupMembersRepository, EmailService emailService,
                          MiscCalculations miscCalculations) {
        this.jwtService = jwtService;
        this.expenseRepository = expenseRepository;
        this.groupRepository = groupRepository;
        this.groupMembersRepository = groupMembersRepository;
        this.emailService = emailService;
        this.miscCalculations = miscCalculations;
    }

    @Transactional
    public ExpenseDetailDto createExpense(CreateExpenseDto dto) {
        Users currentUser = jwtService.getCurrentUser();
        Groups group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new RuntimeException(String.format("Group not found id: %d", dto.getGroupId())));
        if (groupMembersRepository.findByGroupIdAndUserId(group, currentUser).isEmpty()) {
            throw new RuntimeException("You do not have permission to create expense for this group");
        }
        Expenses expense = new Expenses();
        ExpenseDetailDto expenseDetailDto = new ExpenseDetailDto();
        expense.setAmount(dto.getAmount());
        expense.setGroupId(group);
        expense.setDescription(dto.getDescription());
        expense.setSplitType(dto.getSplitType());
        expense.setPaidBy(currentUser);
        expense = expenseRepository.save(expense);

        expenseDetailDto.setId(expense.getId());
        expenseDetailDto.setDescription(expense.getDescription());
        expenseDetailDto.setAmount(expense.getAmount());
        expenseDetailDto.setSplitType(expense.getSplitType());
        expenseDetailDto.setPaidBy(Long.valueOf(expense.getPaidBy().getId()));
        List<ExpenseShareDto> shareDtos = new ArrayList<>();

        switch (dto.getSplitType()) {
            case Expenses.SplitType.EQUAL: {

                BigDecimal amountOwed = dto.getAmount().divide(BigDecimal.valueOf(group.getMembers().size() - 1), 2,
                        RoundingMode.HALF_UP);
                for (GroupMembers member : group.getMembers()) {
                    if (member.getUserId().equals(currentUser)) {
                        continue;
                    }
                    ExpenseShare share = new ExpenseShare();
                    share.setExpense(expense);
                    share.setUserId(member.getUserId());
                    share.setAmountOwed(amountOwed);
                    share.setPaid(false);
                    expense.getShares().add(share);
                    ExpenseShareDto shareDto = new ExpenseShareDto();
                    shareDto.setUserId(Long.valueOf(member.getUserId().getId()));
                    shareDto.setAmountOwed(amountOwed);
                    shareDtos.add(shareDto);
                }

                break;

            }
            case Expenses.SplitType.PERCENTAGE: {
                if (dto.getSplitDetails() == null) {
                    throw new RuntimeException("Split details are required for percentage split");
                }
                List<BigDecimal> percentages = new ArrayList<>(dto.getSplitDetails().values());
                BigDecimal totalPercent = percentages.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalPercent.compareTo(BigDecimal.valueOf(100)) != 0) {
                    throw new RuntimeException("Total percentage must equal 100");
                }

                List<Long> memberIds = new ArrayList<>(
                        dto.getSplitDetails().keySet().stream().map(Long::valueOf).collect(Collectors.toList()));
                List<GroupMembers> membersList = groupMembersRepository.findAllByGroupfindUserIds(group, memberIds);

                if (membersList.size() != memberIds.size()) {
                    throw new RuntimeException("Invalid member ids in split details");
                }
                for (GroupMembers member : membersList) {
                    long userId = member.getUserId().getId();
                    System.out.println(userId);
                    // Convert to Long for map lookup
                    BigDecimal percent = dto.getSplitDetails()
                            .get(Long.valueOf(userId));
                    if (percent == null) {
                        throw new RuntimeException("No percentage found for user ID: " + userId);
                    }
                    BigDecimal amountOwed = dto.getAmount()
                            .multiply(percent)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                    ExpenseShare share = new ExpenseShare();
                    share.setExpense(expense);
                    share.setUserId(member.getUserId());
                    share.setAmountOwed(amountOwed);
                    share.setPaid(false);
                    expense.getShares().add(share);
                    ExpenseShareDto shareDto = new ExpenseShareDto();
                    shareDto.setUserId(Long.valueOf(member.getUserId().getId()));
                    shareDto.setAmountOwed(amountOwed);
                    shareDtos.add(shareDto);
                }
                break;

            }
            case Expenses.SplitType.EXACT: {
                if (dto.getSplitDetails() == null) {
                    throw new RuntimeException("Split details are required for exact split");
                }
                List<BigDecimal> amounts = new ArrayList<>(dto.getSplitDetails().values());
                BigDecimal totalAmount = amounts.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                System.out.println(totalAmount);
                System.out.println(dto.getAmount());

                if (totalAmount.compareTo(expense.getAmount()) > 0) {
                    throw new RuntimeException("Total amount cannot exceed the actual amount");
                }
                List<Long> memberIds = new ArrayList<>(
                        dto.getSplitDetails().keySet().stream().map(Long::valueOf).collect(Collectors.toList()));
                List<GroupMembers> membersList = groupMembersRepository.findAllByGroupfindUserIds(group, memberIds);

                if (membersList.size() != memberIds.size()) {
                    throw new RuntimeException("Invalid member ids in split details");
                }
                for (GroupMembers member : membersList) {
                    BigDecimal amountOwed = dto.getSplitDetails().get(Long.valueOf(member.getUserId().getId()));

                    ExpenseShare share = new ExpenseShare();
                    share.setExpense(expense);
                    share.setUserId(member.getUserId());
                    share.setAmountOwed(amountOwed);
                    share.setPaid(false);
                    expense.getShares().add(share);
                    ExpenseShareDto shareDto = new ExpenseShareDto();
                    shareDto.setUserId(Long.valueOf(member.getUserId().getId()));
                    shareDto.setAmountOwed(amountOwed);
                    shareDtos.add(shareDto);
                }
                break;
            }

        }

        Expenses finalExpense = expense;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {

                    emailService.newExpenseSharedNotification(finalExpense);
                } catch (Exception e) {
                    System.err.println("Error in post-commit processing: " + e.getMessage());
                }
            }
        });
        expenseDetailDto.setShares(shareDtos);
        expenseRepository.save(expense);
        return expenseDetailDto;
    }

    public ExpenseDetailDto getExpenseDetail(Long expenseId) {
        Expenses expense = expenseRepository.findByIdWithDetails(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (groupMembersRepository.findByGroupIdAndUserId(expense.getGroupId(), jwtService.getCurrentUser())
                .isEmpty()) {
            throw new RuntimeException("You do not have permission to view this expense");
        }

        ExpenseDetailDto dto = new ExpenseDetailDto();
        dto.setId(expense.getId());
        dto.setAmount(expense.getAmount());
        dto.setDescription(expense.getDescription());
        dto.setSplitType(expense.getSplitType());
        List<ExpenseShareDto> shareDtos = new ArrayList<>();
        for (ExpenseShare share : expense.getShares()) {
            ExpenseShareDto shareDto = new ExpenseShareDto();
            shareDto.setId(share.getId());
            shareDto.setUserId(Long.valueOf(share.getUserId().getId()));
            shareDto.setAmountOwed(share.getAmountOwed());
            if (share.isPaid()) {
                shareDto.setPaid(true);
                shareDto.setAmountRemaining(BigDecimal.valueOf(0));
            } else {
                shareDto.setPaid(false);
                BigDecimal remainingAmount = share.getAmountOwed().subtract(miscCalculations.calculateAmountTillNow(share));
                shareDto.setAmountRemaining(remainingAmount);
            }
            shareDtos.add(shareDto);
        }
        dto.setShares(shareDtos);
        dto.setPaidBy(Long.valueOf(expense.getPaidBy().getId()));
        return dto;

    }

    public List<ListExpenseDto> listExpenses(Long id) {
        Groups group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Group not found id: %d", id)));
        if (groupMembersRepository.findByGroupIdAndUserId(group, jwtService.getCurrentUser())
                .isEmpty()) {
            throw new RuntimeException("You do not have permission to view this group's expenses");
        }

        return expenseRepository.findAllByGroupId(group);
    }


}
