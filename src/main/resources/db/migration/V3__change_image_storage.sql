ALTER TABLE `expense` DROP COLUMN `image`;

CREATE TABLE `images` (
  `global_id` BIGINT PRIMARY KEY,
  `image` VARCHAR(255) NOT NULL
);
