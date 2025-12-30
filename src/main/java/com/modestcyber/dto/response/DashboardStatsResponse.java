package com.modestcyber.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 仪表盘统计响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private Long userCount;           // 用户总数
    private Long productCount;        // 商品总数
    private Long orderCount;          // 订单总数
    
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal totalAmount;   // 总交易额
    
    private Long pendingReviewCount;  // 待审核商品数
}
