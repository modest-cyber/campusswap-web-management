# 西安石油大学二手交易网

## 项目简介
西安石油大学二手交易网是一个专为西安石油大学师生设计的在线二手交易平台，提供便捷的二手商品发布、浏览、购买和管理功能，旨在促进校园内闲置资源的循环利用，打造绿色校园。

## 技术栈

### 后端
- Spring Boot 4.0.0
- MyBatis 4.0.0
- MySQL 8.0+
- Redis
- JWT 0.12.3
- Java 21

### 前端
- Vue 3
- TypeScript
- Vite
- SCSS
- Vue Router
- Pinia

## 项目结构

```
西安石油大学二手交易网/
├── campusswap-web-management/  # 后端服务
│   ├── src/                   # 后端源码
│   ├── pom.xml                # Maven配置
│   └── ...
├── frontend/                  # 前端应用
│   ├── public/                # 静态资源
│   ├── src/                   # 前端源码
│   │   ├── api/               # API请求
│   │   ├── components/        # 组件
│   │   ├── router/            # 路由
│   │   ├── store/             # 状态管理
│   │   ├── styles/            # 样式
│   │   └── views/             # 页面
│   ├── package.json           # 依赖配置
│   └── vite.config.ts         # Vite配置
└── README.md                  # 项目说明
```

## 功能模块

### 用户模块
- 用户注册/登录
- 个人信息管理
- 密码修改

### 商品模块
- 商品发布与编辑
- 商品分类管理
- 商品搜索与筛选
- 商品详情查看
- 商品收藏
- 商品审核（管理员）

### 订单模块
- 订单创建与支付
- 订单状态管理
- 订单详情查看
- 订单列表
- 订单评价

### 管理模块
- 商品管理
- 订单管理
- 用户管理
- 分类管理
- 数据统计

## 快速开始

### 后端启动
1. 配置数据库连接：修改 `application.properties` 文件中的数据库配置
2. 配置Redis连接：修改 `application.properties` 文件中的Redis配置
3. 运行项目：
   ```bash
   cd campusswap-web-management
   ./mvnw spring-boot:run
   ```

### 前端启动
1. 安装依赖：
   ```bash
   cd frontend
   npm install
   ```
2. 启动开发服务器：
   ```bash
   npm run dev
   ```
3. 访问应用：打开浏览器访问 `http://localhost:5173`

## 开发指南

### 后端开发
- 代码规范：遵循Spring Boot最佳实践
- 数据库设计：使用MyBatis注解或XML配置
- API设计：RESTful风格

### 前端开发
- 组件开发：遵循Vue 3 Composition API
- 状态管理：使用Pinia
- 样式设计：使用SCSS预处理器

## 部署说明

1. 后端打包：
   ```bash
   cd campusswap-web-management
   ./mvnw clean package -DskipTests
   ```

2. 前端构建：
   ```bash
   cd frontend
   npm run build
   ```

3. 部署到服务器：
   - 将后端jar包部署到服务器
   - 将前端dist目录部署到Nginx
   - 配置Nginx反向代理

## 注意事项

1. 确保JDK版本为21或以上
2. 确保MySQL版本为8.0或以上
3. 开发环境需要安装Redis
4. 前端开发需要Node.js 16或以上

