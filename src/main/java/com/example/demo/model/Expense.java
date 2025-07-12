package com.example.demo.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Expenses")
public class Expense {
    private long userId;
    private int amount;
    private String desc;
    private String date;
    private String time;


    public Expense(String desc, int amount) {
        this.desc = desc;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Expense{" +
                ", desc='" + desc + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", time=" + time +
                '}';
    }

}
