DROP TABLE notification_user;
DROP TABLE notifications;


CREATE TABLE notifications
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    type                INT                   NOT NULL,
    initiator_id        BIGINT                NOT NULL,
    added_id            BIGINT                NULL,
    expense_id          BIGINT                NULL,
    expense_description VARCHAR(255)          NULL,
    group_id            BIGINT                NULL,
    date                datetime              NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_ADDED FOREIGN KEY (added_id) REFERENCES users (id);

ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_GROUP FOREIGN KEY (group_id) REFERENCES app_group (id) ON DELETE CASCADE;

ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_INITIATOR FOREIGN KEY (initiator_id) REFERENCES users (id);


CREATE TABLE notification_user
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    seen            BIT(1)                NOT NULL,
    user_id         BIGINT                NOT NULL,
    notification_id BIGINT                NOT NULL,
    CONSTRAINT pk_notification_user PRIMARY KEY (id)
);

ALTER TABLE notification_user
    ADD CONSTRAINT FK_NOTIFICATION_USER_ON_NOTIFICATION FOREIGN KEY (notification_id) REFERENCES notifications (id) ON DELETE CASCADE;

ALTER TABLE notification_user
    ADD CONSTRAINT FK_NOTIFICATION_USER_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);