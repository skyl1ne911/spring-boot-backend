package com.example.demo.service.impl;


import com.example.demo.dto.modeldto.ExpenseDto;
import com.example.demo.entity.User;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.model.Expense;
import com.example.demo.repository.ExpenseRepository;
import com.example.demo.repository.SearchRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ExpenseService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final SearchRepository searchRepository;

    @Autowired
    public ExpenseServiceImpl(UserRepository userRepository, ExpenseRepository expenseRepository, SearchRepository searchRepository) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.searchRepository = searchRepository;
    }

    @Override
    public List<Expense> getAllExpenses() throws IllegalAccessException {
        UserDetailsImpl userDetails = getUserDetails();

        return expenseRepository.findByUserId(userDetails.getUser().getId());
    }

    @Override
    public Expense addExpense(ExpenseDto expenseDto) throws IllegalAccessException {
        UserDetailsImpl userDetails = getUserDetails();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        Expense expense = new Expense(expenseDto.getDesc(), expenseDto.getAmount());
        expense.setUserId(userDetails.getUser().getId());
        expense.setDate(LocalDate.now().toString());
        expense.setTime(LocalTime.now().format(dateTimeFormatter));

        return expenseRepository.save(expense);
    }

    @Override
    public List<Expense> getExpenseByDateTime(String date) throws IllegalAccessException {
        UserDetailsImpl userDetails = getUserDetails();

        return searchRepository.findByDate(userDetails.getUser().getId(), date);
    }

    @Override
    public List<Expense> descSearch(String desc) {

        return searchRepository.findByDesc(desc);
    }

    private UserDetailsImpl getUserDetails() throws IllegalAccessException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return (UserDetailsImpl) authentication.getPrincipal();
        }
        throw new IllegalAccessException();
    }

}
