package com.modestcyber.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成 admin 的哈希
        String password = "admin";
        String hash = encoder.encode(password);
        System.out.println("密码: " + password);
        System.out.println("BCrypt哈希: " + hash);
        System.out.println();
        
        // 验证已知哈希
        String knownHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHCBBPRfGQ2ZSfLtP0P5MS";
        System.out.println("验证已知哈希:");
        System.out.println("admin 匹配结果: " + encoder.matches("admin", knownHash));
        System.out.println("admin123 匹配结果: " + encoder.matches("admin123", knownHash));
        System.out.println("Admin@123 匹配结果: " + encoder.matches("Admin@123", knownHash));
    }
}
