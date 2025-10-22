package com.jztchl.splitwiseclonejava.repos;

import com.jztchl.splitwiseclonejava.models.Settlement;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    List<Settlement> findAllByStatusAndExpenseShare_Id(Settlement.SettlementStatus settlementStatus, @NotNull(message = "Expense share ID is required") Long expenseShareId);
}
