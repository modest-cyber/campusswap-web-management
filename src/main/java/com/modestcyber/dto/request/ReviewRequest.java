package com.modestcyber.dto.request;

import lombok.Data;

/**
 * 商品审核请求DTO
 */
@Data
public class ReviewRequest {
    private Long productId;
    private Integer status;
    private String reason;
}
