package com.modestcyber.controller;

import com.modestcyber.common.PageResult;
import com.modestcyber.common.Result;
import com.modestcyber.dto.request.CreateOrderRequest;
import com.modestcyber.dto.response.OrderResponse;
import com.modestcyber.service.OrderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<Long> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Long orderId = orderService.createOrder(request);
        return Result.success(orderId);
    }

    /**
     * 订单详情
     */
    @GetMapping("/{id}")
    public Result<OrderResponse> getOrderDetail(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderDetail(id);
        return Result.success(response);
    }

    /**
     * 订单列表
     */
    @GetMapping("/list")
    public Result<PageResult<OrderResponse>> listOrders(
            @RequestParam(required = false, defaultValue = "buyer") String viewType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PageResult<OrderResponse> result = orderService.listOrders(viewType, status, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 卖家发货/确认面交
     */
    @PutMapping("/{id}/deliver")
    public Result<Void> deliverOrder(@PathVariable Long id) {
        orderService.deliverOrder(id);
        return Result.success();
    }

    /**
     * 买家确认收货
     */
    @PutMapping("/{id}/confirm")
    public Result<Void> confirmOrder(@PathVariable Long id) {
        orderService.confirmOrder(id);
        return Result.success();
    }

    /**
     * 取消订单
     */
    @DeleteMapping("/{id}")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.success();
    }
}
