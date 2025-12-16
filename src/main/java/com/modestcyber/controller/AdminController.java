package com.modestcyber.controller;

import com.modestcyber.annotation.RequireAdmin;
import com.modestcyber.common.PageResult;
import com.modestcyber.common.Result;
import com.modestcyber.dto.request.CategoryRequest;
import com.modestcyber.dto.request.ReviewRequest;
import com.modestcyber.dto.response.DashboardStatsResponse;
import com.modestcyber.dto.response.OrderResponse;
import com.modestcyber.dto.response.ProductResponse;
import com.modestcyber.dto.response.StatisticsOverviewResponse;
import com.modestcyber.pojo.Category;
import com.modestcyber.pojo.User;
import com.modestcyber.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 获取用户列表
     */
    @RequireAdmin
    @GetMapping("/users")
    public Result<PageResult<User>> getUserList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        PageResult<User> result = adminService.getUserList(keyword, department, status, page, size);
        return Result.success(result);
    }

    /**
     * 获取用户详情
     */
    @RequireAdmin
    @GetMapping("/users/{id}")
    public Result<User> getUserDetail(@PathVariable Long id) {
        User user = adminService.getUserDetail(id);
        return Result.success(user);
    }

    /**
     * 禁用/启用用户
     */
    @RequireAdmin
    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        adminService.updateUserStatus(id, params.get("status"));
        return Result.success();
    }

    /**
     * 获取待审核商品列表
     */
    @RequireAdmin
    @GetMapping("/products/pending")
    public Result<PageResult<ProductResponse>> getPendingReviewProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        PageResult<ProductResponse> result = adminService.getPendingReviewProducts(keyword, categoryId, page, size);
        return Result.success(result);
    }

    /**
     * 获取所有商品列表（管理员）
     */
    @RequireAdmin
    @GetMapping("/products")
    public Result<PageResult<ProductResponse>> getProductList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        PageResult<ProductResponse> result = adminService.getProductList(keyword, categoryId, status, page, size);
        return Result.success(result);
    }

    /**
     * 商品审核
     */
    @RequireAdmin
    @PostMapping("/products/review")
    public Result<Void> reviewProduct(@RequestBody ReviewRequest request) {
        adminService.auditProduct(request.getProductId(), request.getStatus(), request.getReason());
        return Result.success();
    }

    /**
     * 批量审核商品
     */
    @RequireAdmin
    @PostMapping("/products/review/batch")
    public Result<Void> batchReviewProducts(@RequestBody List<ReviewRequest> requests) {
        adminService.batchReviewProducts(requests);
        return Result.success();
    }

    /**
     * 删除商品
     */
    @RequireAdmin
    @DeleteMapping("/products/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return Result.success();
    }

    /**
     * 获取所有订单列表
     */
    @RequireAdmin
    @GetMapping("/orders")
    public Result<PageResult<OrderResponse>> getOrderList(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        PageResult<OrderResponse> result = adminService.listAllOrders(status, page, size);
        return Result.success(result);
    }

    /**
     * 获取订单详情
     */
    @RequireAdmin
    @GetMapping("/orders/{id}")
    public Result<OrderResponse> getOrderDetail(@PathVariable Long id) {
        OrderResponse order = adminService.getOrderDetail(id);
        return Result.success(order);
    }

    /**
     * 获取仪表盘统计数据
     */
    @RequireAdmin
    @GetMapping("/stats/dashboard")
    public Result<DashboardStatsResponse> getDashboardStats() {
        DashboardStatsResponse response = adminService.getDashboardStats();
        return Result.success(response);
    }

    /**
     * 统计概览
     */
    @RequireAdmin
    @GetMapping("/statistics/overview")
    public Result<StatisticsOverviewResponse> getStatisticsOverview() {
        StatisticsOverviewResponse response = adminService.getStatisticsOverview();
        return Result.success(response);
    }

    // ========== 分类管理 ==========

    @RequireAdmin
    @GetMapping("/categories/tree")
    public Result<List<Category>> getCategoryTree() {
        List<Category> tree = adminService.getCategoryTree();
        return Result.success(tree);
    }

    @RequireAdmin
    @GetMapping("/categories")
    public Result<List<Category>> getCategoryList() {
        List<Category> list = adminService.getCategoryList();
        return Result.success(list);
    }

    @RequireAdmin
    @PostMapping("/categories")
    public Result<Void> createCategory(@RequestBody CategoryRequest request) {
        adminService.createCategory(request);
        return Result.success();
    }

    @RequireAdmin
    @PutMapping("/categories/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest request) {
        adminService.updateCategory(id, request);
        return Result.success();
    }

    @RequireAdmin
    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return Result.success();
    }

    @RequireAdmin
    @PutMapping("/categories/{id}/status")
    public Result<Void> updateCategoryStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        adminService.updateCategoryStatus(id, params.get("status"));
        return Result.success();
    }

    // ========== 统计报表 ==========

    @RequireAdmin
    @GetMapping("/stats/users")
    public Result<List<Map<String, Object>>> getUserStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<Map<String, Object>> stats = adminService.getUserStats(startDate, endDate);
        return Result.success(stats);
    }

    @RequireAdmin
    @GetMapping("/stats/departments")
    public Result<List<Map<String, Object>>> getDepartmentStats() {
        List<Map<String, Object>> stats = adminService.getDepartmentStats();
        return Result.success(stats);
    }

    @RequireAdmin
    @GetMapping("/stats/products")
    public Result<List<Map<String, Object>>> getProductStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<Map<String, Object>> stats = adminService.getProductStats(startDate, endDate);
        return Result.success(stats);
    }

    @RequireAdmin
    @GetMapping("/stats/categories")
    public Result<List<Map<String, Object>>> getCategoryDistribution() {
        List<Map<String, Object>> stats = adminService.getCategoryDistribution();
        return Result.success(stats);
    }

    @RequireAdmin
    @GetMapping("/stats/trades")
    public Result<List<Map<String, Object>>> getTradeStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<Map<String, Object>> stats = adminService.getTradeStats(startDate, endDate);
        return Result.success(stats);
    }

    @RequireAdmin
    @GetMapping("/stats/trade-methods")
    public Result<List<Map<String, Object>>> getTradeMethodStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<Map<String, Object>> stats = adminService.getTradeMethodStats(startDate, endDate);
        return Result.success(stats);
    }

    @RequireAdmin
    @GetMapping("/stats/rank/buyers")
    public Result<List<Map<String, Object>>> getBuyerRank(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<Map<String, Object>> rank = adminService.getBuyerRank(startDate, endDate);
        return Result.success(rank);
    }

    @RequireAdmin
    @GetMapping("/stats/rank/sellers")
    public Result<List<Map<String, Object>>> getSellerRank(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<Map<String, Object>> rank = adminService.getSellerRank(startDate, endDate);
        return Result.success(rank);
    }

    @RequireAdmin
    @GetMapping("/stats/hot-products")
    public Result<List<Map<String, Object>>> getHotProducts(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<Map<String, Object>> products = adminService.getHotProducts(startDate, endDate);
        return Result.success(products);
    }
}
