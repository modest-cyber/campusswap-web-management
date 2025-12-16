package com.modestcyber.service;

import com.modestcyber.context.UserContext;
import com.modestcyber.dto.request.LoginRequest;
import com.modestcyber.dto.request.RegisterRequest;
import com.modestcyber.dto.request.UpdatePasswordRequest;
import com.modestcyber.dto.request.UpdateUserInfoRequest;
import com.modestcyber.dto.response.LoginResponse;
import com.modestcyber.dto.response.UserInfoResponse;
import com.modestcyber.exception.BusinessException;
import com.modestcyber.mapper.UserMapper;
import com.modestcyber.pojo.User;
import com.modestcyber.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 根据账号查询用户（可能是用户名、邮箱或手机号）
        User user = findUserByAccount(request.getAccount());
        
        if (user == null) {
            throw new BusinessException("账号或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 构建响应
        UserInfoResponse userInfo = convertToUserInfoResponse(user);
        log.info("用户 {} 登录成功", user.getUsername());
        
        return new LoginResponse(token, userInfo);
    }

    /**
     * 用户注册
     */
    @Transactional
    public void register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userMapper.findByEmail(request.getEmail()) != null) {
            throw new BusinessException("邮箱已被注册");
        }

        // 检查手机号是否已存在
        if (userMapper.findByPhone(request.getPhone()) != null) {
            throw new BusinessException("手机号已被注册");
        }

        // 创建用户
        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("user");  // 默认角色为普通用户
        user.setStatus(1);  // 默认启用
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
        log.info("用户 {} 注册成功", user.getUsername());
    }

    /**
     * 获取个人信息
     */
    public UserInfoResponse getUserInfo() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        return convertToUserInfoResponse(user);
    }

    /**
     * 更新个人信息
     */
    @Transactional
    public void updateUserInfo(UpdateUserInfoRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查邮箱是否被其他用户使用
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            User existUser = userMapper.findByEmail(request.getEmail());
            if (existUser != null && !existUser.getId().equals(userId)) {
                throw new BusinessException("邮箱已被其他用户使用");
            }
            user.setEmail(request.getEmail());
        }

        // 检查手机号是否被其他用户使用
        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            User existUser = userMapper.findByPhone(request.getPhone());
            if (existUser != null && !existUser.getId().equals(userId)) {
                throw new BusinessException("手机号已被其他用户使用");
            }
            user.setPhone(request.getPhone());
        }

        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment());
        }

        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateInfo(user);
        log.info("用户 {} 更新个人信息", user.getUsername());
    }

    /**
     * 修改密码
     */
    @Transactional
    public void updatePassword(UpdatePasswordRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 更新密码
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        userMapper.updatePassword(userId, encodedPassword, LocalDateTime.now());
        log.info("用户 {} 修改密码成功", user.getUsername());
    }

    /**
     * 根据账号查找用户（支持用户名、邮箱、手机号）
     */
    private User findUserByAccount(String account) {
        User user = userMapper.findByUsername(account);
        if (user != null) {
            return user;
        }

        user = userMapper.findByEmail(account);
        if (user != null) {
            return user;
        }

        user = userMapper.findByPhone(account);
        return user;
    }

    /**
     * 转换为用户信息响应DTO
     */
    private UserInfoResponse convertToUserInfoResponse(User user) {
        UserInfoResponse response = new UserInfoResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}
