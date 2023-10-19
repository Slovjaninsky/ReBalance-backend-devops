SET foreign_key_checks = 0;
DROP TABLE application_user;
DROP TABLE expense_group;
DROP TABLE expense;
DROP TABLE user_group;
DROP TABLE notification;
DROP TABLE image;
SET foreign_key_checks = 1;

CREATE TABLE users
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    username VARCHAR(255)          NULL,
    email    VARCHAR(255)          NULL,
    password VARCHAR(255)          NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);


CREATE TABLE `groups`
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    name     VARCHAR(255)          NULL,
    currency VARCHAR(255)          NULL,
    CONSTRAINT pk_groups PRIMARY KEY (id)
);


CREATE TABLE user_group
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    favorite BIT(1)                NOT NULL,
    user_id  BIGINT                NOT NULL,
    group_id BIGINT                NOT NULL,
    CONSTRAINT pk_user_group PRIMARY KEY (id)
);

ALTER TABLE user_group
    ADD CONSTRAINT FK_USER_GROUP_ON_GROUP FOREIGN KEY (group_id) REFERENCES `groups` (id);

ALTER TABLE user_group
    ADD CONSTRAINT FK_USER_GROUP_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);


CREATE TABLE expenses
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    amount        DOUBLE                NOT NULL,
    `description` VARCHAR(255)          NULL,
    date          date                  NOT NULL,
    category      VARCHAR(255)          NULL,
    user_id       BIGINT                NOT NULL,
    group_id      BIGINT                NOT NULL,
    CONSTRAINT pk_expenses PRIMARY KEY (id)
);

ALTER TABLE expenses
    ADD CONSTRAINT FK_EXPENSES_ON_GROUP FOREIGN KEY (group_id) REFERENCES `groups` (id);

ALTER TABLE expenses
    ADD CONSTRAINT FK_EXPENSES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);


CREATE TABLE expense_user
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    amount     DOUBLE                NULL,
    user_id    BIGINT                NOT NULL,
    expense_id BIGINT                NOT NULL,
    CONSTRAINT pk_expense_user PRIMARY KEY (id)
);

ALTER TABLE expense_user
    ADD CONSTRAINT FK_EXPENSE_USER_ON_EXPENSE FOREIGN KEY (expense_id) REFERENCES expenses (id);

ALTER TABLE expense_user
    ADD CONSTRAINT FK_EXPENSE_USER_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);


CREATE TABLE categories
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    name     VARCHAR(255)          NULL,
    group_id BIGINT                NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

ALTER TABLE categories
    ADD CONSTRAINT FK_CATEGORIES_ON_GROUP FOREIGN KEY (group_id) REFERENCES `groups` (id);


CREATE TABLE images
(
    global_id  BIGINT       NOT NULL,
    image_path VARCHAR(255) NULL,
    CONSTRAINT pk_images PRIMARY KEY (global_id)
);


CREATE TABLE notifications
(
    notification_id BIGINT AUTO_INCREMENT NOT NULL,
    user_id         BIGINT                NULL,
    user_from_id    BIGINT                NULL,
    expense_id      BIGINT                NULL,
    group_id        BIGINT                NULL,
    amount          DOUBLE                NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (notification_id)
);