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
    private Long categoryId;
    private String categoryName;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private List<String> images;
    private Integer status;
    private String statusText;
    private String condition;
    private Integer transactionType;
    private String transactionTypeText;
    private Integer viewCount;
    private Integer favoriteCount;
    private Boolean isFavorite;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
