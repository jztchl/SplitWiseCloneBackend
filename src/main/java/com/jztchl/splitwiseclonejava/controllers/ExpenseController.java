package com.jztchl.splitwiseclonejava.controllers;

import com.jztchl.splitwiseclonejava.dtos.expense.CreateExpenseDto;
import com.jztchl.splitwiseclonejava.dtos.expense.ExpenseDetailDto;
import com.jztchl.splitwiseclonejava.services.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/create-expense")
    public ExpenseDetailDto createExpense(@Valid @RequestBody CreateExpenseDto dto) {
        System.out.println(dto);
        return expenseService.createExpense(dto);
    }

    @GetMapping("/{id}")
    public ExpenseDetailDto getExpenseDetail(@PathVariable Long id) {
        return expenseService.getExpenseDetail(id);
    }

}
