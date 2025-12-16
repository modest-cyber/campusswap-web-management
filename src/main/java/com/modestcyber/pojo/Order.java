package com.modestcyber.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private String orderNo;
    private Long buyerId;
    private Long sellerId;
    private Long productId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Integer transactionType;  // 0-面交，1-邮寄，2-均可
    private Integer status;  // 0-待支付，1-待发货，2-待收货，3-已完成，4-已取消
    private String remark;
    private String address;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime completeTime;
}
