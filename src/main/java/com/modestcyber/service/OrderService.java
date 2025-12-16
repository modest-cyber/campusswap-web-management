package com.modestcyber.service;

import com.modestcyber.common.PageResult;
import com.modestcyber.context.UserContext;
import com.modestcyber.dto.request.CreateOrderRequest;
import com.modestcyber.dto.response.OrderResponse;
import com.modestcyber.exception.BusinessException;
import com.modestcyber.mapper.OrderMapper;
import com.modestcyber.mapper.ProductMapper;
import com.modestcyber.mapper.UserMapper;
import com.modestcyber.pojo.Order;
import com.modestcyber.pojo.Product;
import com.modestcyber.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务
 */
@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    private static final Map<Integer, String> TRANSACTION_TYPE_MAP = new HashMap<>();

    static {
        STATUS_MAP.put(0, "待支付");
        STATUS_MAP.put(1, "待发货");
        STATUS_MAP.put(2, "待收货");
        STATUS_MAP.put(3, "已完成");
        STATUS_MAP.put(4, "已取消");

        TRANSACTION_TYPE_MAP.put(0, "面交");
        TRANSACTION_TYPE_MAP.put(1, "邮寄");
        TRANSACTION_TYPE_MAP.put(2, "均可");
    }

    /**
     * 创建订单
     */
    @Transactional
    public Long createOrder(CreateOrderRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        // 查询商品
        Product product = productMapper.findById(request.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查商品状态
        if (product.getStatus() != 1) {
            throw new BusinessException("商品不在销售中");
        }

        // 检查买卖双方不可同人
        if (product.getUserId().equals(userId)) {
            throw new BusinessException("不能购买自己的商品");
        }

        // 邮寄时地址必填
        if (request.getTransactionType() == 1 && !StringUtils.hasText(request.getAddress())) {
            throw new BusinessException("邮寄方式必须填写收货地址");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setBuyerId(userId);
        order.setSellerId(product.getUserId());
        order.setProductId(product.getId());
        order.setQuantity(1);
        order.setTotalPrice(product.getPrice());
        order.setTransactionType(request.getTransactionType());
        order.setStatus(1);  // 默认待发货状态（跳过待支付）
        order.setRemark(request.getRemark());
        order.setAddress(request.getAddress());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.insert(order);

        // 更新商品状态为已售出
        productMapper.updateStatus(product.getId(), 3, LocalDateTime.now());

        log.info("用户 {} 创建订单: {}", userId, order.getOrderNo());
        return order.getId();
    }

    /**
     * 订单详情
     */
    public OrderResponse getOrderDetail(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查权限
        String role = UserContext.getRole();
        if (!"admin".equals(role) && !order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException("无权查看该订单");
        }

        return convertToOrderResponse(order);
    }

    /**
     * 订单列表
     */
    public PageResult<OrderResponse> listOrders(String type, Integer status, Integer pageNum, Integer pageSize) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        int offset = (pageNum - 1) * pageSize;

        List<Order> orders;
        Long total;

        if ("seller".equals(type)) {
            // 卖家订单
            orders = orderMapper.listSellerOrders(userId, status, offset, pageSize);
            total = orderMapper.countSellerOrders(userId, status);
        } else {
            // 买家订单（默认）
            orders = orderMapper.listBuyerOrders(userId, status, offset, pageSize);
            total = orderMapper.countBuyerOrders(userId, status);
        }

        List<OrderResponse> responseList = orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());

        return new PageResult<>(responseList, pageNum, pageSize, total);
    }

    /**
     * 卖家发货/确认面交
     */
    @Transactional
    public void deliverOrder(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查权限
        if (!order.getSellerId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }

        // 检查订单状态
        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态不正确");
        }

        // 更新状态为待收货
        orderMapper.updateStatus(id, 2, LocalDateTime.now());
        log.info("卖家 {} 发货订单: {}", userId, order.getOrderNo());
    }

    /**
     * 买家确认收货
     */
    @Transactional
    public void confirmOrder(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查权限
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }

        // 检查订单状态
        if (order.getStatus() != 2) {
            throw new BusinessException("订单状态不正确");
        }

        // 更新状态为已完成
        orderMapper.completeOrder(id, 3, LocalDateTime.now(), LocalDateTime.now());
        log.info("买家 {} 确认收货订单: {}", userId, order.getOrderNo());
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查权限
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }

        // 检查订单状态（待发货可取消）
        if (order.getStatus() != 1) {
            throw new BusinessException("当前状态不允许取消订单");
        }

        // 取消订单
        orderMapper.updateStatus(id, 4, LocalDateTime.now());

        // 恢复商品状态
        productMapper.updateStatus(order.getProductId(), 1, LocalDateTime.now());

        log.info("用户 {} 取消订单: {}", userId, order.getOrderNo());
    }

    /**
     * 转换为响应DTO
     */
    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        BeanUtils.copyProperties(order, response);
        response.setStatusText(STATUS_MAP.get(order.getStatus()));
        response.setTransactionTypeText(TRANSACTION_TYPE_MAP.get(order.getTransactionType()));

        // 查询买家信息
        User buyer = userMapper.findById(order.getBuyerId());
        if (buyer != null) {
            response.setBuyerName(buyer.getUsername());
        }

        // 查询卖家信息
        User seller = userMapper.findById(order.getSellerId());
        if (seller != null) {
            response.setSellerName(seller.getUsername());
        }

        // 查询商品信息
        Product product = productMapper.findById(order.getProductId());
        if (product != null) {
            response.setProductTitle(product.getTitle());
            // 获取第一张图片
            List<String> images = convertJsonToList(product.getImages());
            if (!images.isEmpty()) {
                response.setProductImage(images.get(0));
            }
        }

        return response;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * JSON字符串转List
     */
    private List<String> convertJsonToList(String json) {
        if (json == null || json.isEmpty() || "[]".equals(json)) {
            return List.of();
        }
        json = json.replace("[", "").replace("]", "").replace("\"", "");
        if (json.isEmpty()) {
            return List.of();
        }
        return List.of(json.split(","));
    }
}
