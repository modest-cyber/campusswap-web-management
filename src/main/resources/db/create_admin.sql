-- 创建管理员账号
USE `campusswap`;

-- 方式1：如果admin用户已存在，将其升级为管理员并重置密码
-- 新密码: admin123 (MD5加密)
UPDATE `user` 
SET 
    `role` = 'admin',
    `status` = 1,
    `password` = '0192023a7bbd73250516f069df18b500',
    `real_name` = '系统管理员',
    `department` = '信息技术中心',
    `update_time` = NOW()
WHERE `username` = 'admin';

-- 如果上面的密码不对，执行下面的语句（密码: admin123）
-- UPDATE `user` SET `password` = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi' WHERE `username` = 'admin';

-- 方式2：如果要创建新的管理员账号，使用不同的用户名
-- 密码: admin123 (BCrypt加密后)
-- INSERT INTO `user` (
--     `username`, 
--     `password`, 
--     `real_name`, 
--     `email`, 
--     `phone`, 
--     `student_id`, 
--     `department`, 
--     `role`, 
--     `status`, 
--     `create_time`, 
--     `update_time`
-- ) VALUES (
--     'sysadmin',
--     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHCBBPRfGQ2ZSfLtP0P5MS', -- admin123
--     '系统管理员',
--     'sysadmin@xsyu.edu.cn',
--     '13900139000',
--     'ADMIN001',
--     '信息技术中心',
--     'admin',
--     1,
--     NOW(),
--     NOW()
-- );
