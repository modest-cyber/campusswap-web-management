package com.modestcyber.mapper;

import com.modestcyber.pojo.Favorite;
import com.modestcyber.pojo.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 收藏Mapper
 */
@Mapper
public interface FavoriteMapper {

    /**
     * 添加收藏
     */
    @Insert("INSERT INTO favorite (user_id, product_id, create_time) VALUES (#{userId}, #{productId}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Favorite favorite);

    /**
     * 删除收藏
     */
    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND product_id = #{productId}")
    int delete(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 查询是否已收藏
     */
    @Select("SELECT COUNT(*) FROM favorite WHERE user_id = #{userId} AND product_id = #{productId}")
    int existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 查询收藏列表
     */
    @Select("SELECT p.* FROM product p INNER JOIN favorite f ON p.id = f.product_id " +
            "WHERE f.user_id = #{userId} ORDER BY f.create_time DESC LIMIT #{offset}, #{limit}")
    List<Product> listFavoriteProducts(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 查询收藏总数
     */
    @Select("SELECT COUNT(*) FROM favorite WHERE user_id = #{userId}")
    Long countFavorites(Long userId);
}
