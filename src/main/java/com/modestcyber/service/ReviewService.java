package com.modestcyber.service;

import com.modestcyber.common.PageResult;
import com.modestcyber.context.UserContext;
import com.modestcyber.dto.request.CreateReviewRequest;
import com.modestcyber.dto.response.ReviewResponse;
import com.modestcyber.exception.BusinessException;
import com.modestcyber.mapper.OrderMapper;
import com.modestcyber.mapper.ReviewMapper;
import com.modestcyber.mapper.UserMapper;
import com.modestcyber.pojo.Order;
import com.modestcyber.pojo.Review;
import com.modestcyber.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评价服务
 */
@Slf4j
@Service
public class ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 创建评价
     */
    @Transactional
    public void createReview(CreateReviewRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        // 查询订单
        Order order = orderMapper.findById(request.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态（必须已完成）
        if (order.getStatus() != 3) {
            throw new BusinessException("只能对已完成的订单进行评价");
        }

        // 检查是否是订单相关人
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException("无权评价该订单");
        }

        // 检查是否已评价
        Review existReview = reviewMapper.findByOrderIdAndReviewerId(request.getOrderId(), userId);
        if (existReview != null) {
            throw new BusinessException("已经评价过该订单");
        }

        // 确定被评价人
        Long reviewedId = order.getBuyerId().equals(userId) ? order.getSellerId() : order.getBuyerId();

        // 创建评价
        Review review = new Review();
        review.setOrderId(request.getOrderId());
        review.setReviewerId(userId);
        review.setReviewedId(reviewedId);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreateTime(LocalDateTime.now());

        reviewMapper.insert(review);
        log.info("用户 {} 评价订单: {}", userId, order.getOrderNo());
    }

    /**
     * 查询订单评价
     */
    public List<ReviewResponse> getReviewsByOrderId(Long orderId) {
        List<Review> reviews = reviewMapper.findByOrderId(orderId);
        return reviews.stream()
                .map(this::convertToReviewResponse)
                .collect(Collectors.toList());
    }

    /**
     * 查询作为被评价人的评价列表
     */
    public PageResult<ReviewResponse> listReviewsByUser(Integer pageNum, Integer pageSize) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        int offset = (pageNum - 1) * pageSize;

        List<Review> reviews = reviewMapper.listReviewsByReviewedId(userId, offset, pageSize);
        Long total = reviewMapper.countReviewsByReviewedId(userId);

        List<ReviewResponse> responseList = reviews.stream()
                .map(this::convertToReviewResponse)
                .collect(Collectors.toList());

        return new PageResult<>(responseList, pageNum, pageSize, total);
    }

    /**
     * 转换为响应DTO
     */
    private ReviewResponse convertToReviewResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        BeanUtils.copyProperties(review, response);

        // 查询评价者信息
        User reviewer = userMapper.findById(review.getReviewerId());
        if (reviewer != null) {
            response.setReviewerName(reviewer.getUsername());
        }

        // 查询被评价者信息
        User reviewed = userMapper.findById(review.getReviewedId());
        if (reviewed != null) {
            response.setReviewedName(reviewed.getUsername());
        }

        return response;
    }
}
