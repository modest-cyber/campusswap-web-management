package com.modestcyber.controller;

import com.modestcyber.common.PageResult;
import com.modestcyber.common.Result;
import com.modestcyber.dto.request.CreateReviewRequest;
import com.modestcyber.dto.response.ReviewResponse;
import com.modestcyber.service.ReviewService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评价控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 创建评价
     */
    @PostMapping
    public Result<Void> createReview(@Valid @RequestBody CreateReviewRequest request) {
        reviewService.createReview(request);
        return Result.success();
    }

    /**
     * 查询订单评价
     */
    @GetMapping("/by-order/{orderId}")
    public Result<List<ReviewResponse>> getReviewsByOrderId(@PathVariable Long orderId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByOrderId(orderId);
        return Result.success(reviews);
    }

    /**
     * 查询作为被评价人的评价列表
     */
    @GetMapping("/by-user")
    public Result<PageResult<ReviewResponse>> listReviewsByUser(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PageResult<ReviewResponse> result = reviewService.listReviewsByUser(pageNum, pageSize);
        return Result.success(result);
    }
}
