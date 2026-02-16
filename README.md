# Запуск
В корне проекта выполните команду:
```docker compose -f ./docker-compose.yml -p bank_rest up -d```

- Тестовый админ регстрируется при запуске приложения логин: admin, пароль: admin123
- Для запуска тестов ```docker compose -f ./docker-compose.yml -p bank_rest up -d test```
- Для остановки приложения ```docker compose -f ./docker-compose.yml -p bank_rest down```
- Для первого логина используйте get запрос с json {"username": "admin", "password": "admin123"} http://localhost:8080/api/v1/admin/login
- Для доступа к API перейдите по адресу http://localhost:8080/swagger-ui

