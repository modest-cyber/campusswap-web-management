package com.modestcyber.service;

import com.modestcyber.common.PageResult;
import com.modestcyber.dto.response.OrderResponse;
import com.modestcyber.dto.response.ProductResponse;
import com.modestcyber.dto.response.StatisticsOverviewResponse;
import com.modestcyber.dto.response.UserInfoResponse;
import com.modestcyber.exception.BusinessException;
import com.modestcyber.mapper.OrderMapper;
import com.modestcyber.mapper.ProductMapper;
import com.modestcyber.mapper.UserMapper;
import com.modestcyber.pojo.Order;
import com.modestcyber.pojo.Product;
import com.modestcyber.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员服务
 */
@Slf4j
@Service
public class AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    /**
     * 禁用/启用用户
     */
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 不能禁用管理员
        if ("admin".equals(user.getRole())) {
            throw new BusinessException("不能禁用管理员账号");
        }

        // 更新状态 (0-正常，1-禁用)
        userMapper.updateById(userId, status, LocalDateTime.now());
        log.info("管理员更新用户 {} 状态为: {}", userId, status);
    }

    /**
     * 商品审核
     */
    @Transactional
    public void auditProduct(Long productId, Integer status, String reason) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        productMapper.updateStatus(productId, status, LocalDateTime.now());
        log.info("管理员审核商品 {}: 状态={}, 原因={}", productId, status, reason);
    }

    /**
     * 商品下架
     */
    @Transactional
    public void takedownProduct(Long productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        productMapper.updateStatus(productId, 2, LocalDateTime.now());
        log.info("管理员下架商品: {}", productId);
    }

    /**
     * 删除商品
     */
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        productMapper.deleteById(productId);
        log.info("管理员删除商品: {}", productId);
    }

    /**
     * 查看所有订单
     */
    public PageResult<OrderResponse> listAllOrders(Integer status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        int offset = (pageNum - 1) * pageSize;

        // 这里需要添加查询所有订单的方法
        List<Order> orders = orderMapper.listBuyerOrders(null, status, offset, pageSize);
        Long total = orderMapper.countBuyerOrders(null, status);

        List<OrderResponse> responseList = orders.stream()
                .map(order -> {
                    OrderResponse response = new OrderResponse();
                    BeanUtils.copyProperties(order, response);
                    return response;
                })
                .collect(Collectors.toList());

        return new PageResult<>(responseList, pageNum, pageSize, total);
    }

    /**
     * 获取统计概览
     */
    public StatisticsOverviewResponse getStatisticsOverview() {
        StatisticsOverviewResponse response = new StatisticsOverviewResponse();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusDays(7);
        LocalDateTime monthStart = now.minusDays(30);

        // 用户统计
        response.setTotalUsers(getTotalUsers());
        response.setTodayUsers(getUsersByTime(todayStart, now));
        response.setWeekUsers(getUsersByTime(weekStart, now));
        response.setMonthUsers(getUsersByTime(monthStart, now));

        // 商品统计
        response.setTotalProducts(getTotalProducts());
        response.setTodayProducts(getProductsByTime(todayStart, now));
        response.setWeekProducts(getProductsByTime(weekStart, now));
        response.setMonthProducts(getProductsByTime(monthStart, now));
        response.setOnSaleProducts(getOnSaleProducts());

        // 订单统计
        response.setTotalOrders(getTotalOrders());
        response.setTodayOrders(getOrdersByTime(todayStart, now));
        response.setWeekOrders(getOrdersByTime(weekStart, now));
        response.setMonthOrders(getOrdersByTime(monthStart, now));
        
        response.setTotalAmount(getTotalAmount());
        response.setTodayAmount(getAmountByTime(todayStart, now));
        response.setWeekAmount(getAmountByTime(weekStart, now));
        response.setMonthAmount(getAmountByTime(monthStart, now));

        return response;
    }

    // 统计方法实现
    private Long getTotalUsers() {
        return userMapper.countTotal();
    }

    private Long getUsersByTime(LocalDateTime start, LocalDateTime end) {
        return userMapper.countByTime(start, end);
    }

    private Long getTotalProducts() {
        return productMapper.countTotal();
    }

    private Long getProductsByTime(LocalDateTime start, LocalDateTime end) {
        return productMapper.countByTime(start, end);
    }

    private Long getOnSaleProducts() {
        return productMapper.countOnSale();
    }

    private Long getTotalOrders() {
        return orderMapper.countTotal();
    }

    private Long getOrdersByTime(LocalDateTime start, LocalDateTime end) {
        return orderMapper.countByTime(start, end);
    }

    private BigDecimal getTotalAmount() {
        return orderMapper.sumTotalAmount();
    }

    private BigDecimal getAmountByTime(LocalDateTime start, LocalDateTime end) {
        return orderMapper.sumAmountByTime(start, end);
    }
}
