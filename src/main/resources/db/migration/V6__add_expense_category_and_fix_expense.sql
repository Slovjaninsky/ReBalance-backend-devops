ALTER TABLE expenses
    ADD CONSTRAINT FK_EXPENSES_ON_ADDED_BY FOREIGN KEY (added_by_id) REFERENCES users (id);


DROP TABLE categories;

CREATE TABLE categories
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);


CREATE TABLE group_category
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    last_used   datetime              NOT NULL,
    group_id    BIGINT                NOT NULL,
    category_id BIGINT                NOT NULL,
    CONSTRAINT pk_group_category PRIMARY KEY (id)
);

ALTER TABLE group_category
    ADD CONSTRAINT FK_GROUP_CATEGORY_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE;

ALTER TABLE group_category
    ADD CONSTRAINT FK_GROUP_CATEGORY_ON_GROUP FOREIGN KEY (group_id) REFERENCES app_group (id) ON DELETE CASCADE;


ALTER TABLE expenses
    DROP COLUMN `category`;

ALTER TABLE expenses
    ADD category_id BIGINT;

ALTER TABLE expenses
    ADD CONSTRAINT FK_EXPENSES_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES group_category (id) ON DELETE CASCADE;
