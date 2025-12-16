package com.modestcyber.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求DTO
 */
@Data
public class LoginRequest {
    @NotBlank(message = "账号不能为空")
    private String account;  // 用户名/邮箱/手机号
    
    @NotBlank(message = "密码不能为空")
    private String password;
}
