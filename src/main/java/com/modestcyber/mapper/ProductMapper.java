package com.modestcyber.mapper;

import com.modestcyber.pojo.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 商品Mapper
 */
@Mapper
public interface ProductMapper {

    /**
     * 插入商品
     */
    @Insert("INSERT INTO product (user_id, category_id, title, description, price, original_price, images, " +
            "status, `condition`, transaction_type, view_count, favorite_count, create_time, update_time) " +
            "VALUES (#{userId}, #{categoryId}, #{title}, #{description}, #{price}, #{originalPrice}, #{images}, " +
            "#{status}, #{condition}, #{transactionType}, #{viewCount}, #{favoriteCount}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    /**
     * 根据ID查询商品
     */
    @Select("SELECT * FROM product WHERE id = #{id}")
    Product findById(Long id);

    /**
     * 更新商品
     */
    @Update("UPDATE product SET category_id = #{categoryId}, title = #{title}, description = #{description}, " +
            "price = #{price}, original_price = #{originalPrice}, images = #{images}, `condition` = #{condition}, " +
            "transaction_type = #{transactionType}, update_time = #{updateTime} WHERE id = #{id}")
    int update(Product product);

    /**
     * 更新商品状态
     */
    @Update("UPDATE product SET status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, 
                     @Param("updateTime") java.time.LocalDateTime updateTime);

    /**
     * 删除商品
     */
    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 增加浏览量
     */
    @Update("UPDATE product SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);

    /**
     * 增加收藏量
     */
    @Update("UPDATE product SET favorite_count = favorite_count + 1 WHERE id = #{id}")
    int incrementFavoriteCount(Long id);

    /**
     * 减少收藏量
     */
    @Update("UPDATE product SET favorite_count = favorite_count - 1 WHERE id = #{id} AND favorite_count > 0")
    int decrementFavoriteCount(Long id);

    /**
     * 查询商品列表（带分页和筛选）
     */
    @SelectProvider(type = ProductSqlProvider.class, method = "listProducts")
    List<Product> listProducts(@Param("categoryId") Long categoryId,
                               @Param("minPrice") java.math.BigDecimal minPrice,
                               @Param("maxPrice") java.math.BigDecimal maxPrice,
                               @Param("status") Integer status,
                               @Param("keyword") String keyword,
                               @Param("sortBy") String sortBy,
                               @Param("offset") Integer offset,
                               @Param("limit") Integer limit);

    /**
     * 查询商品总数
     */
    @SelectProvider(type = ProductSqlProvider.class, method = "countProducts")
    Long countProducts(@Param("categoryId") Long categoryId,
                       @Param("minPrice") java.math.BigDecimal minPrice,
                       @Param("maxPrice") java.math.BigDecimal maxPrice,
                       @Param("status") Integer status,
                       @Param("keyword") String keyword);

    /**
     * 查询我的商品
     */
    @Select("SELECT * FROM product WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Product> listMyProducts(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 查询我的商品总数
     */
    @Select("SELECT COUNT(*) FROM product WHERE user_id = #{userId}")
    Long countMyProducts(Long userId);
}
