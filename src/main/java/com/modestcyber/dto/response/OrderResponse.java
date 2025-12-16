package com.modestcyber.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNo;
    private Long buyerId;
    private String buyerName;
    private Long sellerId;
    private String sellerName;
    private Long productId;
    private String productTitle;
    private String productImage;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Integer transactionType;
    private String transactionTypeText;
    private Integer status;
    private String statusText;
    private String remark;
    private String address;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime completeTime;
}
