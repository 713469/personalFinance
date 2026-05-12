DROP TABLE IF EXISTS budget;
DROP TABLE IF EXISTS bill;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS account;

CREATE TABLE account (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  type VARCHAR(32) NOT NULL,
  initial_balance DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
  current_balance DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
  remark VARCHAR(255) DEFAULT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_account_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  type VARCHAR(16) NOT NULL,
  icon VARCHAR(64) DEFAULT NULL,
  sort INT NOT NULL DEFAULT 0,
  status INT NOT NULL DEFAULT 1,
  deleted TINYINT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_category_type_status (type, status),
  KEY idx_category_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE bill (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  type VARCHAR(16) NOT NULL,
  amount DECIMAL(18, 2) NOT NULL,
  category_id BIGINT NOT NULL,
  account_id BIGINT NOT NULL,
  trade_time DATETIME NOT NULL,
  remark VARCHAR(255) DEFAULT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_bill_trade_time (trade_time),
  KEY idx_bill_category_id (category_id),
  KEY idx_bill_account_id (account_id),
  KEY idx_bill_deleted (deleted),
  CONSTRAINT fk_bill_account FOREIGN KEY (account_id) REFERENCES account(id),
  CONSTRAINT fk_bill_category FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE budget (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  month VARCHAR(7) NOT NULL COMMENT '预算月份，如 2026-05',
  type VARCHAR(20) NOT NULL COMMENT 'TOTAL/CATEGORY',
  category_id BIGINT NULL COMMENT '分类预算对应分类ID',
  amount DECIMAL(12,2) NOT NULL COMMENT '预算金额',
  remark VARCHAR(255),
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  KEY idx_budget_month (month),
  KEY idx_budget_deleted (deleted),
  CONSTRAINT fk_budget_category FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO account (name, type, initial_balance, current_balance, remark)
VALUES
  ('现金钱包', 'CASH', 1000.00, 1200.00, '日常小额现金'),
  ('微信', 'WECHAT', 0.00, 982.00, '日常消费账户'),
  ('招商银行', 'BANK', 0.00, 18600.00, '工资主账户');

INSERT INTO category (name, type, icon, sort, status)
VALUES
  ('餐饮', 'EXPENSE', 'utensils', 10, 1),
  ('交通', 'EXPENSE', 'car', 20, 1),
  ('购物', 'EXPENSE', 'bag', 30, 1),
  ('工资', 'INCOME', 'wallet', 10, 1),
  ('奖金', 'INCOME', 'gift', 20, 1);

INSERT INTO bill (type, amount, category_id, account_id, trade_time, remark)
VALUES
  ('EXPENSE', 42.00, 1, 1, '2026-05-11 12:20:00', '午餐'),
  ('EXPENSE', 6.00, 2, 2, '2026-05-11 08:36:00', '地铁通勤'),
  ('INCOME', 12000.00, 4, 3, '2026-05-10 18:10:00', '工资入账');

