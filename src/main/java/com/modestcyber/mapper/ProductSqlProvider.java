package com.modestcyber.mapper;

import org.apache.ibatis.jdbc.SQL;
import java.util.List;

/**
 * 商品SQL构建器
 */
public class ProductSqlProvider {

    public String listProducts(Long categoryId, List<Long> categoryIds, List<String> quality,
                               java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
                               Integer status, String keyword, String sortBy, String sortOrder,
                               Integer offset, Integer limit) {
        return new SQL() {{
            SELECT("*");
            FROM("product");
            
            if (categoryId != null) {
                WHERE("category_id = #{categoryId}");
            }
            if (categoryIds != null && !categoryIds.isEmpty()) {
                WHERE("category_id IN (" + buildInClause(categoryIds.size(), "categoryIds") + ")");
            }
            if (quality != null && !quality.isEmpty()) {
                WHERE("`condition` IN (" + buildInClause(quality.size(), "quality") + ")");
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
            if (sortBy != null && !sortBy.isEmpty()) {
                String order = (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) ? "DESC" : "ASC";
                ORDER_BY(sortBy + " " + order);
            } else {
                ORDER_BY("create_time DESC");
            }
            
            if (offset != null && limit != null) {
                LIMIT(limit);
                OFFSET(offset);
            }
        }}.toString();
    }

    public String countProducts(Long categoryId, List<Long> categoryIds, List<String> quality,
                                java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
                                Integer status, String keyword) {
        return new SQL() {{
            SELECT("COUNT(*)");
            FROM("product");
            
            if (categoryId != null) {
                WHERE("category_id = #{categoryId}");
            }
            if (categoryIds != null && !categoryIds.isEmpty()) {
                WHERE("category_id IN (" + buildInClause(categoryIds.size(), "categoryIds") + ")");
            }
            if (quality != null && !quality.isEmpty()) {
                WHERE("`condition` IN (" + buildInClause(quality.size(), "quality") + ")");
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
    
    private String buildInClause(int size, String paramName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(",");
            sb.append("#{" + paramName + "[" + i + "]}");
        }
        return sb.toString();
    }

    public String listMyProducts(Long userId, Integer status, Integer offset, Integer limit) {
        return new SQL() {{
            SELECT("*");
            FROM("product");
            WHERE("user_id = #{userId}");
            
            if (status != null) {
                WHERE("status = #{status}");
            }
            
            ORDER_BY("create_time DESC");
            
            if (offset != null && limit != null) {
                LIMIT(limit);
                OFFSET(offset);
            }
        }}.toString();
    }

    public String countMyProducts(Long userId, Integer status) {
        return new SQL() {{
            SELECT("COUNT(*)");
            FROM("product");
            WHERE("user_id = #{userId}");
            
            if (status != null) {
                WHERE("status = #{status}");
            }
        }}.toString();
    }
}
