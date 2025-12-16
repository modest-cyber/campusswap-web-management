-- 修复用户表字段类型
USE `campusswap`;

-- 修改 role 字段类型为 VARCHAR
ALTER TABLE `user` MODIFY COLUMN `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色（user-普通用户，admin-管理员）';

-- 修改 status 字段默认值和注释
ALTER TABLE `user` MODIFY COLUMN `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）';
