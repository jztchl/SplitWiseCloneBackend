package com.jztchl.splitwiseclonejava.repos;

import com.jztchl.splitwiseclonejava.models.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseShareRepository extends JpaRepository <ExpenseShare,Long>{
}
