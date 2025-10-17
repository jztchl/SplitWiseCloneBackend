package com.jztchl.splitwiseclonejava.repos;

import com.jztchl.splitwiseclonejava.models.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expenses,Long> {

}
