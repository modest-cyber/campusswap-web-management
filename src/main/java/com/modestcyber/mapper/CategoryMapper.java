package com.modestcyber.mapper;

import com.modestcyber.pojo.Category;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类Mapper
 */
@Mapper
public interface CategoryMapper {

    @Select("SELECT * FROM category WHERE id = #{id}")
    Category findById(Long id);

    @Select("SELECT * FROM category ORDER BY parent_id, sort_order")
    List<Category> findAll();

    @Select("SELECT * FROM category WHERE parent_id = #{parentId} ORDER BY sort_order")
    List<Category> findByParentId(Long parentId);

    @Insert("INSERT INTO category (name, parent_id, sort_order, status, create_time) " +
            "VALUES (#{name}, #{parentId}, #{sortOrder}, #{status}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    @Update("UPDATE category SET name = #{name}, parent_id = #{parentId}, sort_order = #{sortOrder}, " +
            "status = #{status} WHERE id = #{id}")
    int update(Category category);

    @Update("UPDATE category SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM category WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM product WHERE category_id = #{categoryId}")
    Long countProductsByCategoryId(Long categoryId);

    @Select("SELECT c.name, COUNT(p.id) as count FROM category c " +
            "LEFT JOIN product p ON c.id = p.category_id " +
            "WHERE c.parent_id = 0 " +
            "GROUP BY c.id, c.name ORDER BY count DESC")
    @Results({
        @Result(column = "name", property = "categoryName"),
        @Result(column = "count", property = "count")
    })
    List<CategoryDistribution> getCategoryDistribution();
    
    class CategoryDistribution {
        public String categoryName;
        public Long count;
    }
}
