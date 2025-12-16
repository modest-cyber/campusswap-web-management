package com.modestcyber.mapper;

import org.apache.ibatis.jdbc.SQL;

/**
 * 商品SQL构建器
 */
public class ProductSqlProvider {

    public String listProducts(Long categoryId, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
                               Integer status, String keyword, String sortBy, Integer offset, Integer limit) {
        return new SQL() {{
            SELECT("*");
            FROM("product");
            
            if (categoryId != null) {
                WHERE("category_id = #{categoryId}");
            }
            if (minPrice != null) {
                WHERE("price >= #{minPrice}");
            }
            if (maxPrice != null) {
                WHERE("price <= #{maxPrice}");
            }
            if (status != null) {
                WHERE("status = #{status}");
            }
            if (keyword != null && !keyword.isEmpty()) {
                WHERE("(title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))");
            }
            
            // 排序
            if ("price_asc".equals(sortBy)) {
                ORDER_BY("price ASC");
            } else if ("price_desc".equals(sortBy)) {
                ORDER_BY("price DESC");
            } else if ("view_count".equals(sortBy)) {
                ORDER_BY("view_count DESC");
            } else if ("favorite_count".equals(sortBy)) {
                ORDER_BY("favorite_count DESC");
            } else {
                ORDER_BY("create_time DESC");
            }
            
            if (offset != null && limit != null) {
                LIMIT(limit);
                OFFSET(offset);
            }
        }}.toString();
    }

    public String countProducts(Long categoryId, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
                                Integer status, String keyword) {
        return new SQL() {{
            SELECT("COUNT(*)");
            FROM("product");
            
            if (categoryId != null) {
                WHERE("category_id = #{categoryId}");
            }
            if (minPrice != null) {
                WHERE("price >= #{minPrice}");
            }
            if (maxPrice != null) {
                WHERE("price <= #{maxPrice}");
            }
            if (status != null) {
                WHERE("status = #{status}");
            }
            if (keyword != null && !keyword.isEmpty()) {
                WHERE("(title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))");
            }
        }}.toString();
    }
}
