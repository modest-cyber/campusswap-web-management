package com.modestcyber.mapper;

import com.modestcyber.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(String email);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User findByPhone(String phone);

    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    /**
     * 插入用户
     */
    @Insert("INSERT INTO user (username, password, real_name, email, phone, student_id, department, role, status, create_time, update_time) " +
            "VALUES (#{username}, #{password}, #{realName}, #{email}, #{phone}, #{studentId}, #{department}, #{role}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 更新用户信息
     */
    @Update("UPDATE user SET real_name = #{realName}, email = #{email}, phone = #{phone}, " +
            "department = #{department}, update_time = #{updateTime} WHERE id = #{id}")
    int updateInfo(User user);

    /**
     * 更新密码
     */
    @Update("UPDATE user SET password = #{password}, update_time = #{updateTime} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password, @Param("updateTime") java.time.LocalDateTime updateTime);

    /**
     * 更新头像
     */
    @Update("UPDATE user SET avatar = #{avatar}, update_time = #{updateTime} WHERE id = #{id}")
    int updateAvatar(@Param("id") Long id, @Param("avatar") String avatar, @Param("updateTime") java.time.LocalDateTime updateTime);

    /**
     * 删除用户
     */
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 更新用户状态
     */
    @Update("UPDATE user SET status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(@Param("id") Long id, @Param("status") Integer status, @Param("updateTime") java.time.LocalDateTime updateTime);

    /**
     * 统计总用户数
     */
    @Select("SELECT COUNT(*) FROM user")
    Long countTotal();

    /**
     * 统计时间范围内的用户数
     */
    @Select("SELECT COUNT(*) FROM user WHERE create_time BETWEEN #{start} AND #{end}")
    Long countByTime(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);

    /**
     * 查询用户列表（带筛选）
     */
    @Select("<script>" +
            "SELECT * FROM user WHERE 1=1" +
            "<if test='keyword != null and keyword != \"\"'>" +
            " AND (username LIKE CONCAT('%', #{keyword}, '%') OR email LIKE CONCAT('%', #{keyword}, '%') OR phone LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='department != null and department != \"\"'>" +
            " AND department = #{department}" +
            "</if>" +
            "<if test='status != null'>" +
            " AND status = #{status}" +
            "</if>" +
            " ORDER BY create_time DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<User> listUsers(@Param("keyword") String keyword,
                         @Param("department") String department,
                         @Param("status") Integer status,
                         @Param("offset") Integer offset,
                         @Param("limit") Integer limit);

    /**
     * 统计用户数量（带筛选）
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM user WHERE 1=1" +
            "<if test='keyword != null and keyword != \"\"'>" +
            " AND (username LIKE CONCAT('%', #{keyword}, '%') OR email LIKE CONCAT('%', #{keyword}, '%') OR phone LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='department != null and department != \"\"'>" +
            " AND department = #{department}" +
            "</if>" +
            "<if test='status != null'>" +
            " AND status = #{status}" +
            "</if>" +
            "</script>")
    Long countUsers(@Param("keyword") String keyword,
                    @Param("department") String department,
                    @Param("status") Integer status);

    @Select("SELECT * FROM user")
    List<User> findAll();
}
