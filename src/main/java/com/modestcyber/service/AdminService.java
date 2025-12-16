package com.modestcyber.service;

import com.modestcyber.common.PageResult;
import com.modestcyber.dto.request.CategoryRequest;
import com.modestcyber.dto.request.ReviewRequest;
import com.modestcyber.dto.response.DashboardStatsResponse;
import com.modestcyber.dto.response.OrderResponse;
import com.modestcyber.dto.response.ProductResponse;
import com.modestcyber.dto.response.StatisticsOverviewResponse;
import com.modestcyber.dto.response.UserInfoResponse;
import com.modestcyber.exception.BusinessException;
import com.modestcyber.mapper.OrderMapper;
import com.modestcyber.mapper.ProductMapper;
import com.modestcyber.mapper.CategoryMapper;
import com.modestcyber.mapper.UserMapper;
import com.modestcyber.pojo.Order;
import com.modestcyber.pojo.Product;
import com.modestcyber.pojo.Category;
import com.modestcyber.pojo.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员服务
 */
@Slf4j
@Service
public class AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CategoryMapper categoryMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取用户列表
     */
    public PageResult<User> getUserList(String keyword, String department, Integer status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        int offset = (pageNum - 1) * pageSize;
        List<User> users = userMapper.listUsers(keyword, department, status, offset, pageSize);
        Long total = userMapper.countUsers(keyword, department, status);

        return new PageResult<>(users, pageNum, pageSize, total);
    }

    /**
     * 获取用户详情
     */
    public User getUserDetail(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    /**
     * 禁用/启用用户
     */
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 不能禁用管理员
        if ("admin".equals(user.getRole())) {
            throw new BusinessException("不能禁用管理员账号");
        }

        // 更新状态 (0-正常，1-禁用)
        userMapper.updateById(userId, status, LocalDateTime.now());
        log.info("管理员更新用户 {} 状态为: {}", userId, status);
    }

    /**
     * 商品审核
     */
    @Transactional
    public void auditProduct(Long productId, Integer status, String reason) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        productMapper.updateStatus(productId, status, LocalDateTime.now());
        log.info("管理员审核商品 {}: 状态={}, 原因={}", productId, status, reason);
    }

    /**
     * 获取待审核商品列表
     */
    public PageResult<ProductResponse> getPendingReviewProducts(String keyword, Long categoryId, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        int offset = (pageNum - 1) * pageSize;
        List<Product> products = productMapper.listProducts(categoryId, null, null, 0, keyword, "create_time", offset, pageSize);
        Long total = productMapper.countProducts(categoryId, null, null, 0, keyword);

        List<ProductResponse> responseList = products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());

        return new PageResult<>(responseList, pageNum, pageSize, total);
    }

    /**
     * 获取所有商品列表（管理员）
     */
    public PageResult<ProductResponse> getProductList(String keyword, Long categoryId, Integer status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        int offset = (pageNum - 1) * pageSize;
        List<Product> products = productMapper.listProducts(categoryId, null, null, status, keyword, "create_time", offset, pageSize);
        Long total = productMapper.countProducts(categoryId, null, null, status, keyword);

        List<ProductResponse> responseList = products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());

        return new PageResult<>(responseList, pageNum, pageSize, total);
    }

    /**
     * 批量审核商品
     */
    @Transactional
    public void batchReviewProducts(List<ReviewRequest> requests) {
        for (ReviewRequest request : requests) {
            auditProduct(request.getProductId(), request.getStatus(), request.getReason());
        }
    }

    /**
     * 转换Product到ProductResponse
     */
    private ProductResponse convertToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        
        // 基本字段复制
        response.setId(product.getId());
        response.setUserId(product.getUserId());
        response.setCategoryId(product.getCategoryId());
        response.setTitle(product.getTitle());
        response.setName(product.getTitle());  // 前端使用name
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setOriginalPrice(product.getOriginalPrice());
        response.setStatus(product.getStatus());
        response.setCondition(product.getCondition());
        response.setQuality(product.getCondition());  // 前端使用quality
        response.setTransactionType(product.getTransactionType());
        response.setViewCount(product.getViewCount());
        response.setFavoriteCount(product.getFavoriteCount());
        response.setCreateTime(product.getCreateTime());
        response.setUpdateTime(product.getUpdateTime());
        
        // 格式化时间为前端需要的格式
        if (product.getCreateTime() != null) {
            response.setCreatedAt(product.getCreateTime().format(DATE_TIME_FORMATTER));
        }
        
        // 解析图片JSON
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            try {
                List<String> imageList = objectMapper.readValue(product.getImages(), new TypeReference<List<String>>() {});
                response.setImages(imageList);
            } catch (Exception e) {
                log.error("解析商品图片JSON失败: {}", product.getImages(), e);
                response.setImages(new ArrayList<>());
            }
        } else {
            response.setImages(new ArrayList<>());
        }
        
        // 转换交易方式
        response.setTradeMethod(getTradeMethodText(product.getTransactionType()));
        
        // 获取用户信息
        User user = userMapper.findById(product.getUserId());
        if (user != null) {
            response.setUsername(user.getUsername());
            response.setUserDepartment(user.getDepartment());
        }
        
        return response;
    }
    
    private String getTradeMethodText(Integer transactionType) {
        if (transactionType == null) {
            return "";
        }
        switch (transactionType) {
            case 0: return "面交";
            case 1: return "邮寄";
            case 2: return "均可";
            default: return "";
        }
    }

    /**
     * 商品下架
     */
    @Transactional
    public void takedownProduct(Long productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        productMapper.updateStatus(productId, 2, LocalDateTime.now());
        log.info("管理员下架商品: {}", productId);
    }

    /**
     * 删除商品
     */
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        productMapper.deleteById(productId);
        log.info("管理员删除商品: {}", productId);
    }

    /**
     * 查看所有订单
     */
    public PageResult<OrderResponse> listAllOrders(Integer status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        int offset = (pageNum - 1) * pageSize;

        // 这里需要添加查询所有订单的方法
        List<Order> orders = orderMapper.listBuyerOrders(null, status, offset, pageSize);
        Long total = orderMapper.countBuyerOrders(null, status);

        List<OrderResponse> responseList = orders.stream()
                .map(order -> {
                    OrderResponse response = new OrderResponse();
                    BeanUtils.copyProperties(order, response);
                    return response;
                })
                .collect(Collectors.toList());

        return new PageResult<>(responseList, pageNum, pageSize, total);
    }

    /**
     * 获取订单详情
     */
    public OrderResponse getOrderDetail(Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        OrderResponse response = new OrderResponse();
        BeanUtils.copyProperties(order, response);
        return response;
    }

    /**
     * 获取仪表盘统计数据
     */
    public DashboardStatsResponse getDashboardStats() {
        DashboardStatsResponse response = new DashboardStatsResponse();
        
        response.setUserCount(getTotalUsers());
        response.setProductCount(getTotalProducts());
        response.setOrderCount(getTotalOrders());
        response.setTotalAmount(getTotalAmount());
        response.setPendingReviewCount(getPendingReviewCount());
        
        return response;
    }

    /**
     * 获取统计概览
     */
    public StatisticsOverviewResponse getStatisticsOverview() {
        StatisticsOverviewResponse response = new StatisticsOverviewResponse();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusDays(7);
        LocalDateTime monthStart = now.minusDays(30);

        // 用户统计
        response.setTotalUsers(getTotalUsers());
        response.setTodayUsers(getUsersByTime(todayStart, now));
        response.setWeekUsers(getUsersByTime(weekStart, now));
        response.setMonthUsers(getUsersByTime(monthStart, now));

        // 商品统计
        response.setTotalProducts(getTotalProducts());
        response.setTodayProducts(getProductsByTime(todayStart, now));
        response.setWeekProducts(getProductsByTime(weekStart, now));
        response.setMonthProducts(getProductsByTime(monthStart, now));
        response.setOnSaleProducts(getOnSaleProducts());

        // 订单统计
        response.setTotalOrders(getTotalOrders());
        response.setTodayOrders(getOrdersByTime(todayStart, now));
        response.setWeekOrders(getOrdersByTime(weekStart, now));
        response.setMonthOrders(getOrdersByTime(monthStart, now));
        
        response.setTotalAmount(getTotalAmount());
        response.setTodayAmount(getAmountByTime(todayStart, now));
        response.setWeekAmount(getAmountByTime(weekStart, now));
        response.setMonthAmount(getAmountByTime(monthStart, now));

        return response;
    }

    // 统计方法实现
    private Long getTotalUsers() {
        return userMapper.countTotal();
    }

    private Long getUsersByTime(LocalDateTime start, LocalDateTime end) {
        return userMapper.countByTime(start, end);
    }

    private Long getTotalProducts() {
        return productMapper.countTotal();
    }

    private Long getProductsByTime(LocalDateTime start, LocalDateTime end) {
        return productMapper.countByTime(start, end);
    }

    private Long getOnSaleProducts() {
        return productMapper.countOnSale();
    }

    private Long getTotalOrders() {
        return orderMapper.countTotal();
    }

    private Long getOrdersByTime(LocalDateTime start, LocalDateTime end) {
        return orderMapper.countByTime(start, end);
    }

    private BigDecimal getTotalAmount() {
        return orderMapper.sumTotalAmount();
    }

    private BigDecimal getAmountByTime(LocalDateTime start, LocalDateTime end) {
        return orderMapper.sumAmountByTime(start, end);
    }

    private Long getPendingReviewCount() {
        return productMapper.countPendingReview();
    }

    // ========== 分类管理 ==========

    public List<Category> getCategoryTree() {
        List<Category> allCategories = categoryMapper.findAll();
        return buildCategoryTree(allCategories, 0L);
    }

    public List<Category> getCategoryList() {
        return categoryMapper.findAll();
    }

    @Transactional
    public void createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        category.setSortOrder(request.getSort() != null ? request.getSort() : 0);
        category.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        category.setCreateTime(LocalDateTime.now());
        categoryMapper.insert(category);
    }

    @Transactional
    public void updateCategory(Long id, CategoryRequest request) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        category.setName(request.getName());
        category.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        category.setSortOrder(request.getSort() != null ? request.getSort() : 0);
        category.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        categoryMapper.update(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        // 检查是否有子分类
        List<Category> children = categoryMapper.findByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException("存在子分类，无法删除");
        }
        // 检查是否有商品使用该分类
        Long count = categoryMapper.countProductsByCategoryId(id);
        if (count > 0) {
            throw new BusinessException("该分类下存在商品，无法删除");
        }
        categoryMapper.deleteById(id);
    }

    @Transactional
    public void updateCategoryStatus(Long id, Integer status) {
        categoryMapper.updateStatus(id, status);
    }

    private List<Category> buildCategoryTree(List<Category> allCategories, Long parentId) {
        return allCategories.stream()
                .filter(cat -> cat.getParentId().equals(parentId))
                .peek(cat -> cat.setChildren(buildCategoryTree(allCategories, cat.getId())))
                .collect(Collectors.toList());
    }

    // ========== 统计报表 ==========

    public List<Map<String, Object>> getUserStats(String startDate, String endDate) {
        // 查询用户注册趋势（按日期统计）
        List<Map<String, Object>> stats = new ArrayList<>();
        // 如果没有时间范围，返回过去7天的数据
        LocalDate endD = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : LocalDate.now();
        LocalDate startD = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : endD.minusDays(7);
        
        for (LocalDate date = startD; !date.isAfter(endD); date = date.plusDays(1)) {
            Map<String, Object> dayStats = new HashMap<>();
            dayStats.put("date", date.toString());
            dayStats.put("registerCount", 0); // 实际应查询数据库
            dayStats.put("activeCount", 0);   // 实际应查询数据库
            stats.add(dayStats);
        }
        return stats;
    }

    public List<Map<String, Object>> getDepartmentStats() {
        // 查询院系用户分布
        List<Map<String, Object>> stats = new ArrayList<>();
        List<User> users = userMapper.findAll();
        
        Map<String, Long> deptCount = new HashMap<>();
        for (User user : users) {
            String dept = user.getDepartment() != null ? user.getDepartment() : "未知";
            deptCount.put(dept, deptCount.getOrDefault(dept, 0L) + 1);
        }
        
        for (Map.Entry<String, Long> entry : deptCount.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("department", entry.getKey());
            map.put("count", entry.getValue());
            stats.add(map);
        }
        return stats;
    }

    public List<Map<String, Object>> getProductStats(String startDate, String endDate) {
        List<Map<String, Object>> stats = new ArrayList<>();
        // 如果没有时间范围，返回过去7天的数据
        LocalDate endD = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : LocalDate.now();
        LocalDate startD = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : endD.minusDays(7);
        
        for (LocalDate date = startD; !date.isAfter(endD); date = date.plusDays(1)) {
            Map<String, Object> dayStats = new HashMap<>();
            dayStats.put("date", date.toString());
            dayStats.put("publishCount", 0); // 实际应查询数据库
            stats.add(dayStats);
        }
        return stats;
    }

    public List<Map<String, Object>> getCategoryDistribution() {
        List<Map<String, Object>> stats = new ArrayList<>();
        var distribution = categoryMapper.getCategoryDistribution();
        for (var item : distribution) {
            Map<String, Object> map = new HashMap<>();
            map.put("categoryName", item.categoryName);
            map.put("count", item.count);
            stats.add(map);
        }
        return stats;
    }

    public List<Map<String, Object>> getTradeStats(String startDate, String endDate) {
        List<Map<String, Object>> stats = new ArrayList<>();
        // 如果没有时间范围，返回过去7天的数据
        LocalDate endD = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : LocalDate.now();
        LocalDate startD = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : endD.minusDays(7);
        
        for (LocalDate date = startD; !date.isAfter(endD); date = date.plusDays(1)) {
            Map<String, Object> dayStats = new HashMap<>();
            dayStats.put("date", date.toString());
            dayStats.put("orderCount", 0);
            dayStats.put("successCount", 0);
            dayStats.put("totalAmount", 0);
            stats.add(dayStats);
        }
        return stats;
    }

    public List<Map<String, Object>> getTradeMethodStats(String startDate, String endDate) {
        List<Map<String, Object>> stats = new ArrayList<>();
        // 面交、邮寄、均可
        List<String> methods = Arrays.asList("面交", "邮寄", "均可");
        for (String method : methods) {
            Map<String, Object> map = new HashMap<>();
            map.put("method", method);
            map.put("count", 0);
            map.put("percentage", 0.0);
            stats.add(map);
        }
        return stats;
    }

    public List<Map<String, Object>> getBuyerRank(String startDate, String endDate) {
        List<Map<String, Object>> rank = new ArrayList<>();
        // 实际应查询数据库，这里返回空列表
        return rank;
    }

    public List<Map<String, Object>> getSellerRank(String startDate, String endDate) {
        List<Map<String, Object>> rank = new ArrayList<>();
        // 实际应查询数据库，这里返回空列表
        return rank;
    }

    public List<Map<String, Object>> getHotProducts(String startDate, String endDate) {
        List<Map<String, Object>> products = new ArrayList<>();
        // 查询热门商品（按浏览量排序）
        List<Product> allProducts = productMapper.listProducts(null, null, null, 1, null, "view_count", 0, 10);
        for (Product product : allProducts) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", product.getTitle());
            map.put("viewCount", product.getViewCount());
            map.put("favoriteCount", product.getFavoriteCount());
            products.add(map);
        }
        return products;
    }
}
