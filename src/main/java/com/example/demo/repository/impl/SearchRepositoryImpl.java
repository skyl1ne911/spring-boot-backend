package com.example.demo.repository.impl;

import com.example.demo.model.Expense;
import com.example.demo.repository.SearchRepository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.AggregateIterable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import org.bson.Document;


import java.util.List;

@Component
public class SearchRepositoryImpl implements SearchRepository {

    @Value("${spring.data.mongodb.database}")
    private String nameDB;

    private final MongoClient mongoClient;
    private final MongoConverter mongoConverter;

    @Autowired
    public SearchRepositoryImpl(MongoClient mongoClient, MongoConverter mongoConverter) {
        this.mongoClient = mongoClient;
        this.mongoConverter = mongoConverter;
    }

    @Override
    public List<Expense> findByDesc(String desc) {
        MongoDatabase database = mongoClient.getDatabase(nameDB);
        MongoCollection<Document> collection = database.getCollection("Expenses");

        List<Expense> expenses = new ArrayList<>();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
                        new Document("$search",
                            new Document("text", new Document("query", desc).append("path", "desc"))),
                        new Document("$sort",
                                new Document("amount", 1L)),
                        new Document("$limit", 50L)
            )
        );

        result.forEach(doc -> expenses.add(mongoConverter.read(Expense.class, doc)));

        return expenses;
    }

    @Override
    public List<Expense> findByDate(long userId, String date) {
        MongoDatabase database = mongoClient.getDatabase(nameDB);
        MongoCollection<Document> collection = database.getCollection("Expenses");

        List<Expense> expenses = new ArrayList<>();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
                    new Document("$search",
                            new Document("compound",
                                    new Document("must", Arrays.asList(
                                        new Document("equals", new Document("value", userId).append("path", "userId")),
                                        new Document("text", new Document("query", date).append("path", "date"))
                                    ))
                            )
                    ),
                    new Document("$sort", new Document("amount", -1L)),
                    new Document("$limit", 50L)
            )
        );

        result.forEach(doc -> expenses.add(mongoConverter.read(Expense.class, doc)));

        return expenses;
    }


}
