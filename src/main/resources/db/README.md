# 数据库脚本说明

## 文件说明

- `schema.sql` - 数据库表结构创建脚本
- `data.sql` - 初始数据脚本（分类数据、默认管理员账号）

## 使用说明

### 1. 执行数据库创建脚本

```bash
# 方式1：使用MySQL命令行
mysql -u root -p < src/main/resources/db/schema.sql

# 方式2：使用MySQL客户端工具（如Navicat、DBeaver等）
# 直接执行 schema.sql 文件
```

### 2. 执行初始数据脚本

```bash
# 方式1：使用MySQL命令行
mysql -u root -p < src/main/resources/db/data.sql

# 方式2：使用MySQL客户端工具
# 直接执行 data.sql 文件
```

### 3. 配置数据库连接

在 `application.properties` 中配置数据库连接信息：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campusswap?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

## 数据库表结构

### 核心表

1. **user** - 用户表
2. **category** - 商品分类表
3. **product** - 商品表
4. **order** - 订单表
5. **favorite** - 收藏表
6. **review** - 评价表

## 注意事项

1. **默认管理员账号**
   - 用户名：`admin`
   - 密码：`admin123`（已BCrypt加密）
   - **首次登录后请立即修改密码！**

2. **字符集**
   - 数据库使用 `utf8mb4` 字符集，支持emoji和特殊字符

3. **外键约束**
   - 表之间设置了外键约束，删除数据时请注意级联关系

4. **索引优化**
   - 已为常用查询字段创建索引，提升查询性能

5. **JSON字段**
   - `product.images` 字段使用JSON类型存储图片数组

## 数据备份

建议定期备份数据库：

```bash
mysqldump -u root -p campusswap > campusswap_backup_$(date +%Y%m%d).sql
```

## 数据恢复

```bash
mysql -u root -p campusswap < campusswap_backup_20240101.sql
```

