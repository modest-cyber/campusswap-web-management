package com.modestcyber.controller;

import com.modestcyber.common.Result;
import com.modestcyber.dto.request.LoginRequest;
import com.modestcyber.dto.request.RegisterRequest;
import com.modestcyber.dto.request.UpdatePasswordRequest;
import com.modestcyber.dto.request.UpdateUserInfoRequest;
import com.modestcyber.dto.response.LoginResponse;
import com.modestcyber.dto.response.UserInfoResponse;
import com.modestcyber.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success();
    }

    /**
     * 获取个人信息
     */
    @GetMapping("/info")
    public Result<UserInfoResponse> getUserInfo() {
        UserInfoResponse response = userService.getUserInfo();
        return Result.success(response);
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@Valid @RequestBody UpdateUserInfoRequest request) {
        userService.updateUserInfo(request);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(request);
        return Result.success();
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.uploadAvatar(file);
        return Result.success(avatarUrl);
    }

    /**
     * 注销账号
     */
    @DeleteMapping
    public Result<Void> deleteAccount() {
        userService.deleteAccount();
        return Result.success();
    }
}
