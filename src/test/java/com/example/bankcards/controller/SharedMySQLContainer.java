package com.example.bankcards.controller;

import org.testcontainers.containers.MySQLContainer;

public class SharedMySQLContainer extends MySQLContainer<SharedMySQLContainer> {
    private static final String IMAGE_VERSION = "mysql:8";
    private static SharedMySQLContainer container;

    private SharedMySQLContainer() {
        super(IMAGE_VERSION);
    }

    public static SharedMySQLContainer getInstance() {
        if (container == null) {
            container = new SharedMySQLContainer();
            container.start();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        // Не останавливаем контейнер, чтобы переиспользовать между тестами
    }
}