package com.jztchl.splitwiseclonejava.services;

import com.jztchl.splitwiseclonejava.dtos.expense.CreateExpenseDto;
import com.jztchl.splitwiseclonejava.dtos.expense.ExpenseDetailDto;
import com.jztchl.splitwiseclonejava.models.Expenses;
import com.jztchl.splitwiseclonejava.models.GroupMembers;
import com.jztchl.splitwiseclonejava.models.Groups;
import com.jztchl.splitwiseclonejava.models.Users;
import com.jztchl.splitwiseclonejava.repos.ExpenseRepository;
import com.jztchl.splitwiseclonejava.repos.GroupMembersRepository;
import com.jztchl.splitwiseclonejava.repos.GroupRepository;
import com.jztchl.splitwiseclonejava.utility.EmailService;
import com.jztchl.splitwiseclonejava.utility.MiscCalculations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("ExpenseService Test")
class ExpenseServiceTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GroupMembersRepository groupMembersRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private MiscCalculations miscCalculations;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ExpenseService expenseService;

    private Users currentUser;
    private Users testUser2;
    private Users testUser3;
    private Groups testGroup;

    private GroupMembers gm1;
    private GroupMembers gm2;
    private GroupMembers gm3;

    @BeforeEach
    void setUp() {
        this.currentUser = new Users();
        currentUser.setId(1);
        currentUser.setName("User1");
        currentUser.setEmail("currentuser@example.com");

        testUser2 = new Users();
        testUser2.setId(2);
        testUser2.setName("User2");
        testUser2.setEmail("testuser2@example.com");

        testUser3 = new Users();
        testUser3.setId(3);
        testUser3.setName("User3");
        testUser3.setEmail("testuser3@example.com");

        testGroup = new Groups();
        testGroup.setId(1L);
        testGroup.setGroupName("Test Group");
        testGroup.setDescription("Test Group Description");
        testGroup.setCreatedBy(currentUser);

        gm1 = new GroupMembers();
        gm1.setGroupId(testGroup);
        gm1.setUserId(currentUser);

        gm2 = new GroupMembers();
        gm2.setGroupId(testGroup);
        gm2.setUserId(testUser2);

        gm3 = new GroupMembers();
        gm3.setGroupId(testGroup);
        gm3.setUserId(testUser3);

        testGroup.setMembers(List.of(gm1, gm2, gm3));
    }

    @Nested
    @DisplayName("Test Create Expense")
    class CreateExpenseTest {

        @Test
        @DisplayName("Should create an equal split expense successfully")
        void createExpense_EqualSplit_ShouldSucceed() {
            // Given
            CreateExpenseDto dto = new CreateExpenseDto();
            dto.setGroupId(testGroup.getId());
            dto.setDescription("Test Expense");
            dto.setAmount(BigDecimal.valueOf(100));
            dto.setSplitType(Expenses.SplitType.EQUAL);

            Expenses expenses = new Expenses();
            expenses.setId(1L);
            expenses.setGroupId(testGroup);
            expenses.setAmount(BigDecimal.valueOf(100));
            expenses.setSplitType(Expenses.SplitType.EQUAL);
            expenses.setPaidBy(currentUser);


            //when
            when(jwtService.getCurrentUser()).thenReturn(currentUser);
            when(groupRepository.findById(testGroup.getId())).thenReturn(Optional.of(testGroup));
            when(groupMembersRepository.findByGroupIdAndUserId(testGroup, currentUser)).thenReturn(Optional.of(gm1));
            when(expenseRepository.save(any(Expenses.class))).thenReturn(expenses);


            ExpenseDetailDto newExpenseDetailDto = expenseService.createExpense(dto);

            // Then
            assertNotNull(newExpenseDetailDto);
            verify(groupRepository, times(1)).findById(testGroup.getId());
            verify(expenseRepository, times(2)).save(any(Expenses.class));
            verify(groupMembersRepository, times(1)).findByGroupIdAndUserId(any(Groups.class), any(Users.class));

            assertEquals(newExpenseDetailDto.getId(), expenses.getId());
            assertEquals(0, newExpenseDetailDto.getAmount().compareTo(expenses.getAmount()));
            assertEquals(newExpenseDetailDto.getSplitType(), expenses.getSplitType());
            assertEquals(newExpenseDetailDto.getPaidBy(), Long.valueOf(currentUser.getId()));
            assertEquals(newExpenseDetailDto.getDescription(), expenses.getDescription());
            assertEquals(2, newExpenseDetailDto.getShares().size());
            assertEquals(0, BigDecimal.valueOf(50.00).compareTo(newExpenseDetailDto.getShares().get(0).getAmountOwed()));
            assertEquals(0, BigDecimal.valueOf(50.00).compareTo(newExpenseDetailDto.getShares().get(1).getAmountOwed()));
            assertEquals(Long.valueOf(testUser2.getId()), newExpenseDetailDto.getShares().get(0).getUserId());
            assertEquals(Long.valueOf(testUser3.getId()), newExpenseDetailDto.getShares().get(1).getUserId());
            assertFalse(newExpenseDetailDto.getShares().get(0).isPaid());
            assertFalse(newExpenseDetailDto.getShares().get(1).isPaid());
            assertEquals(0, BigDecimal.valueOf(0).compareTo(newExpenseDetailDto.getShares().get(0).getAmountRemaining()));
            assertEquals(0, BigDecimal.valueOf(0).compareTo(newExpenseDetailDto.getShares().get(1).getAmountRemaining()));


        }
    }
}
