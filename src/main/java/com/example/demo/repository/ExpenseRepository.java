package com.example.demo.repository;


import com.example.demo.model.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {

    List<Expense> findByDate(String date);

    List<Expense> findByUserId(long id);

}
