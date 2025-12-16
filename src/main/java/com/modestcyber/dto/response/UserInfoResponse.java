package com.modestcyber.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String avatar;
    private String studentId;
    private String department;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
}
