
-- FinTrack initial schema migration
-- Derived from the supplied MySQL schema-only dump.
-- No DROP statements or existing application records are included.
-- Existing databases must be baselined at version 1; fresh databases run this migration.

CREATE TABLE `users` (
    `id` bigint NOT NULL,
    `created_at` datetime(6) DEFAULT NULL,
    `currency` varchar(255) DEFAULT NULL,
    `email` varchar(255) NOT NULL,
    `last_login` datetime(6) DEFAULT NULL,
    `monthly_income` decimal(38,2) DEFAULT NULL,
    `name` varchar(255) NOT NULL,
    `password` varchar(255) NOT NULL,
    `role` enum('ROLE_ADMIN','ROLE_USER') DEFAULT NULL,
    `status` enum('ACTIVE','BLOCKED','INACTIVE') DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `accounts` (
    `id` bigint NOT NULL,
    `account_name` varchar(255) DEFAULT NULL,
    `account_type` enum('BANK_ACCOUNT','CASH','CREDIT_CARD','WALLET') DEFAULT NULL,
    `created_at` datetime(6) DEFAULT NULL,
    `currency` enum('EUR','INR','USD') DEFAULT NULL,
    `current_balance` decimal(38,2) DEFAULT NULL,
    `status` enum('ACTIVE','INACTIVE') DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `user_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FKnjuop33mo69pd79ctplkck40n` (`user_id`),
    CONSTRAINT `FKnjuop33mo69pd79ctplkck40n`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `audit_logs` (
    `id` bigint NOT NULL,
    `action` varchar(255) DEFAULT NULL,
    `created_at` datetime(6) DEFAULT NULL,
    `description` varchar(255) DEFAULT NULL,
    `user_email` varchar(255) DEFAULT NULL,
    `user_id` bigint DEFAULT NULL,
    `user_name` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `budgets` (
    `id` bigint NOT NULL,
    `budget_name` varchar(255) DEFAULT NULL,
    `category` varchar(255) DEFAULT NULL,
    `end_date` date DEFAULT NULL,
    `limit_amount` decimal(38,2) DEFAULT NULL,
    `budget_month` int DEFAULT NULL,
    `start_date` date DEFAULT NULL,
    `status` enum('ACTIVE','COMPLETED','EXCEEDED','INACTIVE') DEFAULT NULL,
    `budget_year` int DEFAULT NULL,
    `user_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FKln0tm5tgf3f9q3sp9sa5m8m7b` (`user_id`),
    CONSTRAINT `FKln0tm5tgf3f9q3sp9sa5m8m7b`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `categories` (
    `id` bigint NOT NULL,
    `category` enum('EXPENSE','INCOME') DEFAULT NULL,
    `color_code` varchar(255) DEFAULT NULL,
    `created_at` datetime(6) DEFAULT NULL,
    `icon` varchar(255) DEFAULT NULL,
    `name` varchar(255) DEFAULT NULL,
    `status` enum('ACTIVE','INACTIVE') DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `user_id` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKghuylkwuedgl2qahxjt8g41kb` (`user_id`),
    CONSTRAINT `FKghuylkwuedgl2qahxjt8g41kb`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `goals` (
    `id` bigint NOT NULL,
    `deadline` date DEFAULT NULL,
    `goal_name` varchar(255) DEFAULT NULL,
    `icon` varchar(255) DEFAULT NULL,
    `priority` enum('HIGH','LOW','MEDIUM') DEFAULT NULL,
    `saved_amount` decimal(38,2) DEFAULT NULL,
    `status` enum('CANCELLED','COMPLETED','IN_PROGRESS','OVERDUE') DEFAULT NULL,
    `target_amount` decimal(38,2) DEFAULT NULL,
    `user_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FKb1mp6ulyqkpcw6bc1a2mr7v1g` (`user_id`),
    CONSTRAINT `FKb1mp6ulyqkpcw6bc1a2mr7v1g`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `notifications` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `action_label` varchar(255) DEFAULT NULL,
    `action_url` varchar(255) DEFAULT NULL,
    `created_at` datetime(6) DEFAULT NULL,
    `message` varchar(1000) NOT NULL,
    `status` enum('READ','UNREAD') NOT NULL,
    `title` varchar(255) NOT NULL,
    `type` enum('BUDGET','GOAL','SECURITY','SYSTEM') NOT NULL,
    `user_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK9y21adhxn0ayjhfocscqox7bh` (`user_id`),
    CONSTRAINT `FK9y21adhxn0ayjhfocscqox7bh`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `transactions` (
    `id` bigint NOT NULL,
    `account` varchar(255) DEFAULT NULL,
    `amount` decimal(38,2) DEFAULT NULL,
    `category` varchar(255) DEFAULT NULL,
    `description` varchar(255) DEFAULT NULL,
    `notes` varchar(255) DEFAULT NULL,
    `payment_mode` enum('BANK_TRANSFER','CARD','CASH','UPI','WALLET') DEFAULT NULL,
    `title` varchar(255) DEFAULT NULL,
    `transaction_date` date DEFAULT NULL,
    `transaction_status` enum('CANCELLED','COMPLETED','FAILED','PENDING') DEFAULT NULL,
    `type` enum('EXPENSE','INCOME') DEFAULT NULL,
    `user_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FKqwv7rmvc8va8rep7piikrojds` (`user_id`),
    CONSTRAINT `FKqwv7rmvc8va8rep7piikrojds`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Hibernate table-backed ID generator tables

CREATE TABLE `users_seq` (
    `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `accounts_seq` (
    `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `audit_logs_seq` (
    `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `budgets_seq` (
    `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `categories_seq` (
    `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `goals_seq` (
    `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `transactions_seq` (
    `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Initialize Hibernate ID generator values for a fresh database.
-- Values match the supplied existing database schema.

INSERT INTO `users_seq` (`next_val`) VALUES (101);
INSERT INTO `accounts_seq` (`next_val`) VALUES (101);
INSERT INTO `audit_logs_seq` (`next_val`) VALUES (101);
INSERT INTO `budgets_seq` (`next_val`) VALUES (101);
INSERT INTO `categories_seq` (`next_val`) VALUES (101);
INSERT INTO `goals_seq` (`next_val`) VALUES (101);
INSERT INTO `transactions_seq` (`next_val`) VALUES (101);
