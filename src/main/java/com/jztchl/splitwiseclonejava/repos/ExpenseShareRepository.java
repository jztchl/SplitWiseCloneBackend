package com.jztchl.splitwiseclonejava.repos;

import com.jztchl.splitwiseclonejava.models.ExpenseShare;
import com.jztchl.splitwiseclonejava.models.Expenses;
import com.jztchl.splitwiseclonejava.models.Users;
import com.jztchl.splitwiseclonejava.services.JwtService;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {

    boolean existsByIdAndUserId(Long expenseShareId, Users currentUser);

    List<ExpenseShare> findAllByExpense(@NotNull Expenses expense);

    List<ExpenseShare> findAllByUserId(@NotNull Users userId);

}
