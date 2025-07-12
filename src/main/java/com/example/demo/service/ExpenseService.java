package com.example.demo.service;

import com.example.demo.dto.modeldto.DateTimeRequest;
import com.example.demo.dto.modeldto.ExpenseDto;
import com.example.demo.model.Expense;

import java.util.List;

public interface ExpenseService {

    List<Expense> getAllExpenses() throws IllegalAccessException;

    Expense addExpense(ExpenseDto expenseDto) throws IllegalAccessException;

    List<Expense> getExpenseByDateTime(String date) throws IllegalAccessException;

    List<Expense> descSearch(String desc);

}
