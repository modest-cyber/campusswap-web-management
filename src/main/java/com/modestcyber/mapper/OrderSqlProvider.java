package com.modestcyber.mapper;

import org.apache.ibatis.jdbc.SQL;

/**
 * 订单SQL构建器
 */
public class OrderSqlProvider {

    public String listBuyerOrders(Long buyerId, Integer status, Integer offset, Integer limit) {
        return new SQL() {{
            SELECT("*");
            FROM("`order`");
            WHERE("buyer_id = #{buyerId}");
            
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

    public String countBuyerOrders(Long buyerId, Integer status) {
        return new SQL() {{
            SELECT("COUNT(*)");
            FROM("`order`");
            WHERE("buyer_id = #{buyerId}");
            
            if (status != null) {
                WHERE("status = #{status}");
            }
        }}.toString();
    }

    public String listSellerOrders(Long sellerId, Integer status, Integer offset, Integer limit) {
        return new SQL() {{
            SELECT("*");
            FROM("`order`");
            WHERE("seller_id = #{sellerId}");
            
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

    public String countSellerOrders(Long sellerId, Integer status) {
        return new SQL() {{
            SELECT("COUNT(*)");
            FROM("`order`");
            WHERE("seller_id = #{sellerId}");
            
            if (status != null) {
                WHERE("status = #{status}");
            }
        }}.toString();
    }
}
