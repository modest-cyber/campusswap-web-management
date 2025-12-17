package com.modestcyber.mapper;

import com.modestcyber.pojo.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 订单Mapper
 */
@Mapper
public interface OrderMapper {

    /**
     * 插入订单
     */
    @Insert("INSERT INTO `order` (order_no, buyer_id, seller_id, product_id, quantity, total_price, " +
            "transaction_type, status, remark, address, create_time, update_time) " +
            "VALUES (#{orderNo}, #{buyerId}, #{sellerId}, #{productId}, #{quantity}, #{totalPrice}, " +
            "#{transactionType}, #{status}, #{remark}, #{address}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    /**
     * 根据ID查询订单
     */
    @Select("SELECT * FROM `order` WHERE id = #{id}")
    Order findById(Long id);

    /**
     * 根据订单号查询订单
     */
    @Select("SELECT * FROM `order` WHERE order_no = #{orderNo}")
    Order findByOrderNo(String orderNo);

    /**
     * 更新订单状态
     */
    @Update("UPDATE `order` SET status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, 
                     @Param("updateTime") java.time.LocalDateTime updateTime);

    /**
     * 完成订单
     */
    @Update("UPDATE `order` SET status = #{status}, complete_time = #{completeTime}, update_time = #{updateTime} WHERE id = #{id}")
    int completeOrder(@Param("id") Long id, @Param("status") Integer status,
                      @Param("completeTime") java.time.LocalDateTime completeTime,
                      @Param("updateTime") java.time.LocalDateTime updateTime);

    /**
     * 删除订单
     */
    @Delete("DELETE FROM `order` WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 查询买家订单列表
     */
    @SelectProvider(type = OrderSqlProvider.class, method = "listBuyerOrders")
    List<Order> listBuyerOrders(@Param("buyerId") Long buyerId,
                                @Param("status") Integer status,
                                @Param("offset") Integer offset,
                                @Param("limit") Integer limit);

    /**
     * 查询买家订单总数
     */
    @SelectProvider(type = OrderSqlProvider.class, method = "countBuyerOrders")
    Long countBuyerOrders(@Param("buyerId") Long buyerId, @Param("status") Integer status);

    /**
     * 查询卖家订单列表
     */
    @SelectProvider(type = OrderSqlProvider.class, method = "listSellerOrders")
    List<Order> listSellerOrders(@Param("sellerId") Long sellerId,
                                 @Param("status") Integer status,
                                 @Param("offset") Integer offset,
                                 @Param("limit") Integer limit);

    /**
     * 查询卖家订单总数
     */
    @SelectProvider(type = OrderSqlProvider.class, method = "countSellerOrders")
    Long countSellerOrders(@Param("sellerId") Long sellerId, @Param("status") Integer status);

    /**
     * 查询用户未完成订单数
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE (buyer_id = #{userId} OR seller_id = #{userId}) AND status NOT IN (3, 4)")
    int countUnfinishedOrders(Long userId);

    /**
     * 统计总订单数
     */
    @Select("SELECT COUNT(*) FROM `order`")
    Long countTotal();

    /**
     * 统计时间范围内的订单数
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE create_time BETWEEN #{start} AND #{end}")
    Long countByTime(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);

    /**
     * 统计总交易额
     */
    @Select("SELECT COALESCE(SUM(total_price), 0) FROM `order` WHERE status = 3")
    java.math.BigDecimal sumTotalAmount();

    /**
     * 统计时间范围内的交易额
     */
    @Select("SELECT COALESCE(SUM(total_price), 0) FROM `order` WHERE status = 3 AND create_time BETWEEN #{start} AND #{end}")
    java.math.BigDecimal sumAmountByTime(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);

    /**
     * 统计时间范围内成功的订单数
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE status = 3 AND create_time BETWEEN #{start} AND #{end}")
    Long countSuccessByTime(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);
}
