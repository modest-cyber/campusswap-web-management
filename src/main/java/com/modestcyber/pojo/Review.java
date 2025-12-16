package com.modestcyber.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评价实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long id;
    private Long orderId;
    private Long reviewerId;
    private Long reviewedId;
    private Integer rating;
    private String comment;
    private LocalDateTime createTime;
}
