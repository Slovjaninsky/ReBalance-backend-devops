package com.example.databaseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.UnsupportedEncodingException;

@SpringBootApplication
public class DatabaseServiceApplication {

    public static void main(String[] args) throws UnsupportedEncodingException {
        SpringApplication.run(DatabaseServiceApplication.class, args);
    }

}
