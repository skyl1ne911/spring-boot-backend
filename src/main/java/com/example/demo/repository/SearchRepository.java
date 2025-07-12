package com.example.demo.repository;


import com.example.demo.model.Expense;

import java.util.List;

public interface SearchRepository {

    List<Expense> findByDesc(String desc);

    List<Expense> findByDate(long userId, String date);

}
