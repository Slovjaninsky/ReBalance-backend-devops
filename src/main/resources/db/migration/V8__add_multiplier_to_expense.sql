ALTER TABLE expense_user
    MODIFY amount DOUBLE NOT NULL;

ALTER TABLE expense_user
    ADD multiplier INT NOT NULL;