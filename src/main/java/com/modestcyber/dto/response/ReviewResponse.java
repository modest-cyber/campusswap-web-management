package com.modestcyber.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评价响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long orderId;
    private Long reviewerId;
    private String reviewerName;
    private Long reviewedId;
    private String reviewedName;
    private Integer rating;
    private String comment;
    private LocalDateTime createTime;
}
