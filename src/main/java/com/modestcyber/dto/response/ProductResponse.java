package com.modestcyber.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private Long userId;
    private String username;
    private String userDepartment;
    private Long categoryId;
    private String categoryName;
    private String name;  // 商品名称（前端使用name）
    private String title;  // 保留title字段
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private List<String> images;
    private Integer status;
    private String statusText;
    private String quality;  // 成色（前端使用quality）
    private String condition;  // 保留condition字段
    private String tradeMethod;  // 交易方式文本（前端使用tradeMethod）
    private Integer transactionType;
    private String transactionTypeText;
    private Integer viewCount;
    private Integer favoriteCount;
    private Boolean isFavorite;
    private String createdAt;  // 前端使用createdAt
    private LocalDateTime createTime;  // 保留createTime字段
    private LocalDateTime updateTime;
}
