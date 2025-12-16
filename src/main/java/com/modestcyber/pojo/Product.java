package com.modestcyber.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String images;  // JSON字符串
    private Integer status;  // 0-待审核，1-在售，2-已下架，3-已售出，4-审核拒绝
    private String condition;
    private Integer transactionType;  // 0-面交，1-邮寄，2-均可
    private Integer viewCount;
    private Integer favoriteCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 辅助字段
    private List<String> imageList;  // 用于前端展示
}
