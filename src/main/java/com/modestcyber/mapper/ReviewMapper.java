package com.modestcyber.mapper;

import com.modestcyber.pojo.Review;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 评价Mapper
 */
@Mapper
public interface ReviewMapper {

    /**
     * 插入评价
     */
    @Insert("INSERT INTO review (order_id, reviewer_id, reviewed_id, rating, comment, create_time) " +
            "VALUES (#{orderId}, #{reviewerId}, #{reviewedId}, #{rating}, #{comment}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Review review);

    /**
     * 根据订单ID和评价者ID查询评价
     */
    @Select("SELECT * FROM review WHERE order_id = #{orderId} AND reviewer_id = #{reviewerId}")
    Review findByOrderIdAndReviewerId(@Param("orderId") Long orderId, @Param("reviewerId") Long reviewerId);

    /**
     * 根据订单ID查询评价列表
     */
    @Select("SELECT * FROM review WHERE order_id = #{orderId}")
    List<Review> findByOrderId(Long orderId);

    /**
     * 查询作为被评价人的评价列表
     */
    @Select("SELECT * FROM review WHERE reviewed_id = #{reviewedId} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Review> listReviewsByReviewedId(@Param("reviewedId") Long reviewedId, 
                                         @Param("offset") Integer offset, 
                                         @Param("limit") Integer limit);

    /**
     * 查询作为被评价人的评价总数
     */
    @Select("SELECT COUNT(*) FROM review WHERE reviewed_id = #{reviewedId}")
    Long countReviewsByReviewedId(Long reviewedId);
}
