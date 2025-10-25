package com.jztchl.splitwiseclonejava.services;

import com.jztchl.splitwiseclonejava.dtos.settlement.CreateSettlementDto;
import com.jztchl.splitwiseclonejava.models.*;
import com.jztchl.splitwiseclonejava.repos.*;
import com.jztchl.splitwiseclonejava.utility.EmailService;
import com.jztchl.splitwiseclonejava.utility.MiscCalculations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@DisplayName("SettlementService Tests")
@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {
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
    @Mock
    private ExpenseService expenseService;
    @Mock
    private ExpenseShareRepository expenseShareRepository;
    @Mock
    private DocRepository docRepository;
    @Mock
    private SettlementRepository settlementRepository;


    @InjectMocks
    SettlementService settlementService;


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

    @Nested
    @DisplayName("Test Create Settlement")
    class CreateSettlementTest {

        @Test
        @DisplayName("Should create a settlement successfully")
        void createSettlementTestShouldBeSuccessfull() {
            //Given
            CreateSettlementDto dto = new CreateSettlementDto();
            dto.setExpenseId(expense1.getId());
            dto.setExpenseShareId(share1.getId());
            dto.setAmount(BigDecimal.valueOf(50));
            dto.setPaymentRef(paymentref.getId());
            dto.setNote("Test Note");

            Settlement settlement = new Settlement();
            settlement.setId(1L);
            settlement.setExpense(expense1);
            settlement.setPayer(currentUser);
            settlement.setAmount(BigDecimal.valueOf(50));
            settlement.setExpenseShare(share1);
            settlement.setPaymentRef(paymentref);
            settlement.setStatus(Settlement.SettlementStatus.PENDING);
            settlement.setNote("Test Note");

            //When
            when(jwtService.getCurrentUser()).thenReturn(currentUser);
            when(expenseRepository.getReferenceById(expense1.getId())).thenReturn(expense1);
            when(expenseShareRepository.existsByIdAndUserId(share1.getId(), currentUser)).thenReturn(true);
            when(settlementRepository.save(Mockito.any(Settlement.class))).thenReturn(settlement);
            when(settlementRepository.findAllByStatusAndExpenseShare_Id(Settlement.SettlementStatus.CONFIRMED, dto.getExpenseShareId())).thenReturn(
                    List.of(settlement)
            );


            CreateSettlementDto resultDto = settlementService.createSettlement(dto);
            //Then
            assertNotNull(resultDto);
            assertEquals(settlement.getId(), resultDto.getId());


        }

        @Test
        @DisplayName("Should raise over payment ")
        void createSettlementTestShouldRaiseOverPayment() {
            //Given
            CreateSettlementDto dto = new CreateSettlementDto();
            dto.setExpenseId(expense1.getId());
            dto.setExpenseShareId(share1.getId());
            dto.setAmount(BigDecimal.valueOf(50));
            dto.setPaymentRef(paymentref.getId());
            dto.setNote("Test Note");

            Settlement settlement = new Settlement();
            settlement.setId(1L);
            settlement.setExpense(expense1);
            settlement.setPayer(currentUser);
            settlement.setAmount(BigDecimal.valueOf(50));
            settlement.setExpenseShare(share1);
            settlement.setPaymentRef(paymentref);
            settlement.setStatus(Settlement.SettlementStatus.CONFIRMED);
            settlement.setNote("Test Note");

            //When
            when(jwtService.getCurrentUser()).thenReturn(currentUser);
            when(expenseShareRepository.existsByIdAndUserId(share1.getId(), currentUser)).thenReturn(true);
            when(settlementRepository.findAllByStatusAndExpenseShare_Id(Settlement.SettlementStatus.CONFIRMED, dto.getExpenseShareId())).thenReturn(
                    List.of(settlement)
            );


            RuntimeException exception = assertThrows(RuntimeException.class, () -> settlementService.createSettlement(dto));
            //Then
            assertEquals("Total payment cannot exceed the amount to be paid", exception.getMessage());


        }

    }


}