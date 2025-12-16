-- ============================================
-- 西安石油大学校园二手商品交易网数据库完整初始化脚本
-- 说明：此脚本包含数据库创建、表结构创建和初始数据
-- 执行方式：mysql -u root -p < init.sql
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `campusswap` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `campusswap`;

-- ============================================
-- 1. 用户表（user）
-- ============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `student_id` VARCHAR(20) DEFAULT NULL COMMENT '学号/工号',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `department` VARCHAR(100) DEFAULT NULL COMMENT '所属院系',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色（0-普通用户，1-管理员）',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态（0-正常，1-禁用）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 商品分类表（category）
-- ============================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID（0表示一级分类）',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- ============================================
-- 3. 商品表（product）
-- ============================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '发布者ID',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `title` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `description` TEXT COMMENT '商品描述',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `images` JSON DEFAULT NULL COMMENT '商品图片（JSON数组）',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '商品状态（0-待审核，1-在售，2-已下架，3-已售出，4-审核拒绝）',
  `condition` VARCHAR(20) DEFAULT NULL COMMENT '商品成色（全新/九成新/八成新等）',
  `transaction_type` TINYINT NOT NULL DEFAULT 0 COMMENT '交易方式（0-面交，1-邮寄，2-均可）',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
  `favorite_count` INT NOT NULL DEFAULT 0 COMMENT '收藏量',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_price` (`price`),
  CONSTRAINT `fk_product_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- ============================================
-- 4. 订单表（order）
-- ============================================
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号（唯一）',
  `buyer_id` BIGINT NOT NULL COMMENT '买家ID',
  `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '购买数量',
  `total_price` DECIMAL(10,2) NOT NULL COMMENT '总价',
  `transaction_type` TINYINT NOT NULL DEFAULT 0 COMMENT '交易方式（0-面交，1-邮寄，2-均可）',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态（0-待支付，1-待发货，2-待收货，3-已完成，4-已取消）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `address` VARCHAR(500) DEFAULT NULL COMMENT '收货地址（邮寄时）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_buyer_id` (`buyer_id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_order_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_order_seller` FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_order_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ============================================
-- 5. 收藏表（favorite）
-- ============================================
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_favorite_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- ============================================
-- 6. 评价表（review）
-- ============================================
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `reviewer_id` BIGINT NOT NULL COMMENT '评价者ID',
  `reviewed_id` BIGINT NOT NULL COMMENT '被评价者ID',
  `rating` TINYINT NOT NULL COMMENT '评分（1-5星）',
  `comment` VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_reviewer_id` (`reviewer_id`),
  KEY `idx_reviewed_id` (`reviewed_id`),
  CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`),
  CONSTRAINT `fk_review_reviewer` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_review_reviewed` FOREIGN KEY (`reviewed_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- ============================================
-- 插入商品分类初始数据
-- ============================================

-- 一级分类
INSERT INTO `category` (`name`, `parent_id`, `sort_order`, `status`) VALUES
('电子产品', 0, 1, 1),
('图书教材', 0, 2, 1),
('生活用品', 0, 3, 1),
('服装配饰', 0, 4, 1),
('运动用品', 0, 5, 1),
('其他', 0, 6, 1);

-- 电子产品二级分类
INSERT INTO `category` (`name`, `parent_id`, `sort_order`, `status`) VALUES
('手机', 1, 1, 1),
('电脑', 1, 2, 1),
('平板', 1, 3, 1),
('耳机音响', 1, 4, 1),
('其他电子', 1, 5, 1);

-- 图书教材二级分类
INSERT INTO `category` (`name`, `parent_id`, `sort_order`, `status`) VALUES
('教材', 2, 1, 1),
('参考书', 2, 2, 1),
('小说文学', 2, 3, 1),
('工具书', 2, 4, 1),
('其他书籍', 2, 5, 1);

-- 生活用品二级分类
INSERT INTO `category` (`name`, `parent_id`, `sort_order`, `status`) VALUES
('日用品', 3, 1, 1),
('化妆品', 3, 2, 1),
('小家电', 3, 3, 1),
('收纳用品', 3, 4, 1),
('其他生活用品', 3, 5, 1);

-- 服装配饰二级分类
INSERT INTO `category` (`name`, `parent_id`, `sort_order`, `status`) VALUES
('男装', 4, 1, 1),
('女装', 4, 2, 1),
('鞋帽', 4, 3, 1),
('配饰', 4, 4, 1),
('其他服装', 4, 5, 1);

-- 运动用品二级分类
INSERT INTO `category` (`name`, `parent_id`, `sort_order`, `status`) VALUES
('球类', 5, 1, 1),
('健身器材', 5, 2, 1),
('运动鞋', 5, 3, 1),
('运动服', 5, 4, 1),
('其他运动用品', 5, 5, 1);

-- ============================================
-- 插入默认管理员账号
-- 用户名：admin
-- 密码：admin123（已BCrypt加密）
-- 注意：首次登录后请立即修改密码！
-- ============================================
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `department`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJwC', '系统管理员', '13800138000', 'admin@xsyu.edu.cn', '信息中心', 1, 0);

-- ============================================
-- 初始化完成
-- ============================================
SELECT 'Database initialization completed successfully!' AS message;

