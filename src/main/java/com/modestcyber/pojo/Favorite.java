package com.modestcyber.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收藏实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {
    private Long id;
    private Long userId;
    private Long productId;
    private LocalDateTime createTime;
}
