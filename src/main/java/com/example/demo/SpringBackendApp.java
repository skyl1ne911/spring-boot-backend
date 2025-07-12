package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@SpringBootApplication
@EnableMongoRepositories
public class SpringBackendApp {

	public static void main(String[] args) {
		SpringApplication.run(SpringBackendApp.class, args);
	}
}
