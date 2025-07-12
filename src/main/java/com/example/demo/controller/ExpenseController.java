package com.example.demo.controller;


import com.example.demo.dto.modeldto.DateTimeRequest;
import com.example.demo.dto.modeldto.ExpenseDto;
import com.example.demo.model.Expense;
import com.example.demo.repository.SearchRepository;
import com.example.demo.service.ExpenseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/secured/expenses")
@Tag(name = "Expense Controller", description = "Test Security API")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/get/all")
    public List<Expense> getAllExpenses() throws IllegalAccessException {
        return expenseService.getAllExpenses();
    }

    @GetMapping("/get/{text}")
    public List<Expense> search(@PathVariable String text) {
        return expenseService.descSearch(text);
    }

    @GetMapping("/get/date")
    public List<Expense> getByTime(String date) throws IllegalAccessException {
        return expenseService.getExpenseByDateTime(date);
    }

    @PostMapping("/add/expense")
    public ResponseEntity<?> addExpense(@RequestBody ExpenseDto expenseDto) {
        try {
            Expense expense = expenseService.addExpense(expenseDto);
            return ResponseEntity.ok(expense);

        } catch (IllegalAccessException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getCause());
        }
    }

}
