package com.modestcyber.controller;

import com.modestcyber.annotation.RequireAdmin;
import com.modestcyber.common.PageResult;
import com.modestcyber.common.Result;
import com.modestcyber.dto.response.OrderResponse;
import com.modestcyber.dto.response.StatisticsOverviewResponse;
import com.modestcyber.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 禁用/启用用户
     */
    @RequireAdmin
    @PutMapping("/user/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminService.updateUserStatus(id, status);
        return Result.success();
    }

    /**
     * 商品审核
     */
    @RequireAdmin
    @PutMapping("/product/{id}/audit")
    public Result<Void> auditProduct(@PathVariable Long id, 
                                      @RequestParam Integer status,
                                      @RequestParam(required = false) String reason) {
        adminService.auditProduct(id, status, reason);
        return Result.success();
    }

    /**
     * 商品下架
     */
    @RequireAdmin
    @PutMapping("/product/{id}/takedown")
    public Result<Void> takedownProduct(@PathVariable Long id) {
        adminService.takedownProduct(id);
        return Result.success();
    }

    /**
     * 删除商品
     */
    @RequireAdmin
    @DeleteMapping("/product/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return Result.success();
    }

    /**
     * 查看所有订单
     */
    @RequireAdmin
    @GetMapping("/order/list")
    public Result<PageResult<OrderResponse>> listAllOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PageResult<OrderResponse> result = adminService.listAllOrders(status, pageNum, pageSize);
        return Result.success(result);
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
}
