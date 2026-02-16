package com.example.bankcards.controller;

import org.testcontainers.containers.MySQLContainer;

public final class SharedMySQLContainer extends MySQLContainer<SharedMySQLContainer> {

    private static final SharedMySQLContainer INSTANCE = new SharedMySQLContainer();

    private SharedMySQLContainer() {
        super("mysql:8.4");
        withDatabaseName("bankcards");
        withUsername("bankcards_user");
        withPassword("strong_password");
        start(); // Start the container immediately
    }

    public static SharedMySQLContainer getInstance() {
        return INSTANCE;
    }

    @Override
    public void stop() {
        // Do nothing, JVM handles shutdown
    }
}