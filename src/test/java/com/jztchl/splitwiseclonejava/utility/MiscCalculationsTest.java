package com.jztchl.splitwiseclonejava.utility;

import com.jztchl.splitwiseclonejava.models.*;
import com.jztchl.splitwiseclonejava.repos.ExpenseRepository;
import com.jztchl.splitwiseclonejava.repos.ExpenseShareRepository;
import com.jztchl.splitwiseclonejava.repos.SettlementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("MiscCalculations Test")
class MiscCalculationsTest {
    @Mock
    private ExpenseShareRepository expenseShareRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private SettlementRepository settlementRepository;


    @InjectMocks
    private MiscCalculations miscCalculations;

    private Users currentUser;
    private Users testUser2;
    private Users testUser3;
    private Groups testGroup;

    private GroupMembers gm1;
    private GroupMembers gm2;
    private GroupMembers gm3;

    private Expenses expense1;

    ExpenseShare share1;
    ExpenseShare share2;

    private Doc paymentref;


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

        expense1 = new Expenses();
        expense1.setId(1L);
        expense1.setGroupId(testGroup);
        expense1.setAmount(BigDecimal.valueOf(100));
        expense1.setSplitType(Expenses.SplitType.EQUAL);
        expense1.setPaidBy(currentUser);


        share1 = new ExpenseShare();
        share1.setId(1L);
        share1.setExpense(expense1);
        share1.setUserId(testUser2);
        share1.setAmountOwed(BigDecimal.valueOf(50));
        share1.setPaid(false);

        share2 = new ExpenseShare();
        share2.setId(2L);
        share2.setExpense(expense1);
        share2.setUserId(testUser3);
        share2.setAmountOwed(BigDecimal.valueOf(50));
        share2.setPaid(false);

        paymentref = new Doc();
        paymentref.setUrl("https://example.com/paymentref");
    }

    @Test
    void updateStatusTest() {
        //Given
        share1.setPaid(true);
        share2.setPaid(true);

        Settlement settlement = new Settlement();
        settlement.setId(1L);
        settlement.setExpense(expense1);
        settlement.setPayer(testUser2);
        settlement.setAmount(BigDecimal.valueOf(50));
        settlement.setExpenseShare(share1);
        settlement.setPaymentRef(paymentref);
        settlement.setStatus(Settlement.SettlementStatus.CONFIRMED);

        Settlement settlement2 = new Settlement();
        settlement2.setId(2L);
        settlement2.setExpense(expense1);
        settlement2.setPayer(testUser3);
        settlement2.setAmount(BigDecimal
                .valueOf(50));
        settlement2.setExpenseShare(share2);
        settlement2.setPaymentRef(paymentref);
        settlement2.setStatus(Settlement.SettlementStatus.CONFIRMED);

        List<Settlement> settlements = List.of(settlement, settlement2);


        //When
        when(expenseShareRepository.findById(share1.getId())).thenReturn(Optional.of(share1));
        when(expenseShareRepository.findAllByExpense(share1.getExpense())).thenReturn(List.of(share1, share2));
        when(settlementRepository.findAllByStatusAndExpenseShare_Id(Settlement.SettlementStatus.CONFIRMED, share1.getId())).thenReturn(settlements);

        //Then
        miscCalculations.updateStatusExpense(share1.getId());


        //Then
        verify(expenseRepository, times(1)).save(share1.getExpense());
        assertEquals(true, share1.getExpense().getIsPaymentsDone());


    }
}