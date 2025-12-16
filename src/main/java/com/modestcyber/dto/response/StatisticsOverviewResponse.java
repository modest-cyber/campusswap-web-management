package com.modestcyber.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 统计概览响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsOverviewResponse {
    // 用户统计
    private Long totalUsers;
    private Long todayUsers;
    private Long weekUsers;
    private Long monthUsers;
    
    // 商品统计
    private Long totalProducts;
    private Long todayProducts;
    private Long weekProducts;
    private Long monthProducts;
    private Long onSaleProducts;
    
    // 订单统计
    private Long totalOrders;
    private Long todayOrders;
    private Long weekOrders;
    private Long monthOrders;
    private BigDecimal totalAmount;
    private BigDecimal todayAmount;
    private BigDecimal weekAmount;
    private BigDecimal monthAmount;
}
