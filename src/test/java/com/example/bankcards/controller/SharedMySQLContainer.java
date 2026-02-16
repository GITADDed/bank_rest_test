package com.example.bankcards.controller;

import org.testcontainers.containers.MySQLContainer;

public class SharedMySQLContainer extends MySQLContainer<SharedMySQLContainer> {

    private static final SharedMySQLContainer INSTANCE =
            new SharedMySQLContainer()
                    .withDatabaseName("bankcards")
                    .withUsername("bankcards_user")
                    .withPassword("strong_password");

    private SharedMySQLContainer() {
        super("mysql:8.4");
    }

    public static SharedMySQLContainer getInstance() {
        return INSTANCE;
    }

    static {
        INSTANCE.start();
    }
}