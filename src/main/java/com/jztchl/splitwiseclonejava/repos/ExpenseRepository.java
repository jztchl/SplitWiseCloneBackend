package com.jztchl.splitwiseclonejava.repos;

import com.jztchl.splitwiseclonejava.models.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expenses, Long> {
    @Query("SELECT e FROM Expenses e " +
            "JOIN FETCH e.paidBy " +
            "LEFT JOIN FETCH e.shares s " +
            "LEFT JOIN FETCH s.userId " +
            "WHERE e.id = :expenseId")
    Optional<Expenses> findByIdWithDetails(@Param("expenseId") Long expenseId);

}

