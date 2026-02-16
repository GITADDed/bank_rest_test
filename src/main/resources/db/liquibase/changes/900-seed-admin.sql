--liquibase formatted sql
--changeset you:900-seed-admin context:dev
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM users WHERE username='admin';

INSERT INTO users (username, password_hash, deleted)
VALUES ('admin', '$2a$10$fFl37/m.DF0IEWOzJtFzx.H7nYBPc96xLJpLfMeE.B64tU9oH53wa', false);

INSERT INTO user_roles (user_id, role)
SELECT id, 'ADMIN' FROM users WHERE username='admin';