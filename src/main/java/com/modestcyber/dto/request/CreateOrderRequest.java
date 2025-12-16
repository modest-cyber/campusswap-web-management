package com.modestcyber.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 创建订单请求DTO
 */
@Data
public class CreateOrderRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    @NotNull(message = "交易方式不能为空")
    @Min(value = 0, message = "交易方式无效")
    @Max(value = 2, message = "交易方式无效")
    private Integer transactionType;
    
    private String remark;
    
    private String address;  // 邮寄时必填
}
