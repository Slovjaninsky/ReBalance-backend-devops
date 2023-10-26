CREATE TABLE `application_user` (
  `user_id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) UNIQUE NOT NULL,
  `password` VARCHAR(255) NOT NULL
);

CREATE TABLE `expense_group` (
  `group_id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL,
  `currency` VARCHAR(10) NOT NULL
);

CREATE TABLE `expense` (
  `expense_id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `amount` DOUBLE NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  `date_stamp` DATE NOT NULL,
  `category` VARCHAR(255) NOT NULL,
  `global_id` BIGINT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `group_id` BIGINT UNSIGNED NOT NULL,
  FOREIGN KEY (user_id) REFERENCES application_user(user_id),
  FOREIGN KEY (group_id) REFERENCES expense_group(group_id)
);

CREATE TABLE `user_group` (
  `user_id` BIGINT UNSIGNED NOT NULL,
  `group_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (user_id, group_id),
  FOREIGN KEY (user_id) REFERENCES application_user(user_id),
  FOREIGN KEY (group_id) REFERENCES expense_group(group_id)
);

CREATE TABLE `notification` (
  `notification_id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `user_from_id` BIGINT UNSIGNED NOT NULL,
  `expense_id` BIGINT UNSIGNED NULL,
  `group_id` BIGINT UNSIGNED NULL,
  `amount` DOUBLE NOT NULL,
  FOREIGN KEY (user_id) REFERENCES application_user(user_id),
  FOREIGN KEY (user_from_id) REFERENCES application_user(user_id),
  FOREIGN KEY (expense_id) REFERENCES expense(expense_id),
  FOREIGN KEY (group_id) REFERENCES expense_group(group_id)
);
