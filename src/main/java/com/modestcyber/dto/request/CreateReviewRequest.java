package com.modestcyber.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 创建评价请求DTO
 */
@Data
public class CreateReviewRequest {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分必须在1-5之间")
    @Max(value = 5, message = "评分必须在1-5之间")
    private Integer rating;
    
    @Size(max = 500, message = "评价内容不能超过500字符")
    private String comment;
}
