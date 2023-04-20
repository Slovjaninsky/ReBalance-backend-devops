ALTER TABLE `expense` DROP COLUMN `image`;

CREATE TABLE `images` (
  `global_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `image` VARCHAR(255) NOT NULL,
  FOREIGN KEY (`globalId`) REFERENCES `expense` (`globalId`)
);
