CREATE TABLE token
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    user_id BIGINT                NOT NULL,
    token   VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_token PRIMARY KEY (id)
);

ALTER TABLE token
    ADD CONSTRAINT uc_token_token UNIQUE (token);


ALTER TABLE users
    ADD `role` VARCHAR(6) NOT NULL;

ALTER TABLE users
    RENAME COLUMN username TO nickname;

ALTER TABLE expenses
    RENAME COLUMN user_id TO initiator_id;

ALTER TABLE expenses
    ADD COLUMN added_by_id BIGINT NOT NULL;

