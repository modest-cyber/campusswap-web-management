package com.modestcyber.dto.request;

import lombok.Data;

/**
 * 分类请求DTO
 */
@Data
public class CategoryRequest {
    private String name;
    private Long parentId;
    private Integer sort;
    private Integer status;
}
