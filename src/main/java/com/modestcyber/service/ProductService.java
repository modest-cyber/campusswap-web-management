package com.modestcyber.service;

import com.modestcyber.common.PageResult;
import com.modestcyber.context.UserContext;
import com.modestcyber.dto.request.PublishProductRequest;
import com.modestcyber.dto.request.UpdateProductRequest;
import com.modestcyber.dto.response.ProductResponse;
import com.modestcyber.exception.BusinessException;
import com.modestcyber.mapper.CategoryMapper;
import com.modestcyber.mapper.FavoriteMapper;
import com.modestcyber.mapper.ProductMapper;
import com.modestcyber.mapper.UserMapper;
import com.modestcyber.pojo.Category;
import com.modestcyber.pojo.Product;
import com.modestcyber.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品服务
 */
@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    private static final Map<Integer, String> TRANSACTION_TYPE_MAP = new HashMap<>();

    static {
        STATUS_MAP.put(0, "待审核");
        STATUS_MAP.put(1, "在售");
        STATUS_MAP.put(2, "已下架");
        STATUS_MAP.put(3, "已售出");
        STATUS_MAP.put(4, "审核拒绝");

        TRANSACTION_TYPE_MAP.put(0, "面交");
        TRANSACTION_TYPE_MAP.put(1, "邮寄");
        TRANSACTION_TYPE_MAP.put(2, "均可");
    }

    /**
     * 发布商品
     */
    @Transactional
    public Long publishProduct(PublishProductRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setUserId(userId);
        product.setImages(convertListToJson(request.getImages()));
        product.setStatus(0);  // 默认待审核状态，需要管理员审核后才能上架
        product.setViewCount(0);
        product.setFavoriteCount(0);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());

        productMapper.insert(product);
        log.info("用户 {} 发布商品: {}，状态: 待审核", userId, product.getTitle());
        return product.getId();
    }

    /**
     * 更新商品
     */
    @Transactional
    public void updateProduct(Long id, UpdateProductRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查权限
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该商品");
        }

        // 检查商品状态
        if (product.getStatus() == 3) {
            throw new BusinessException("商品已售出，无法修改");
        }

        BeanUtils.copyProperties(request, product);
        product.setImages(convertListToJson(request.getImages()));
        product.setUpdateTime(LocalDateTime.now());

        productMapper.update(product);
        log.info("用户 {} 更新商品: {}", userId, id);
    }

    /**
     * 上架/下架商品
     */
    @Transactional
    public void updateProductStatus(Long id, Integer status) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查权限
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该商品");
        }

        productMapper.updateStatus(id, status, LocalDateTime.now());
        log.info("用户 {} 更新商品状态: {} -> {}", userId, id, status);
    }

    /**
     * 删除商品
     */
    @Transactional
    public void deleteProduct(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查权限
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该商品");
        }

        // 检查商品状态
        if (product.getStatus() == 3) {
            throw new BusinessException("商品已售出，无法删除");
        }

        productMapper.deleteById(id);
        log.info("用户 {} 删除商品: {}", userId, id);
    }

    /**
     * 商品详情
     */
    @Transactional
    public ProductResponse getProductDetail(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 增加浏览量
        productMapper.incrementViewCount(id);

        return convertToProductResponse(product);
    }

    /**
     * 商品列表
     */
    public PageResult<ProductResponse> listProducts(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                                     Integer status, String keyword, String sortBy,
                                                     Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        int offset = (pageNum - 1) * pageSize;

        List<Product> products = productMapper.listProducts(categoryId, minPrice, maxPrice, status, keyword, sortBy, offset, pageSize);
        Long total = productMapper.countProducts(categoryId, minPrice, maxPrice, status, keyword);

        List<ProductResponse> responseList = products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());

        return new PageResult<>(responseList, pageNum, pageSize, total);
    }

    /**
     * 我的商品
     */
    public PageResult<ProductResponse> listMyProducts(Integer status, Integer pageNum, Integer pageSize) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        int offset = (pageNum - 1) * pageSize;

        List<Product> products = productMapper.listMyProducts(userId, status, offset, pageSize);
        Long total = productMapper.countMyProducts(userId, status);

        List<ProductResponse> responseList = products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());

        return new PageResult<>(responseList, pageNum, pageSize, total);
    }

    /**
     * 转换为响应DTO
     */
    private ProductResponse convertToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(product, response);
        response.setImages(convertJsonToList(product.getImages()));
        response.setStatusText(STATUS_MAP.get(product.getStatus()));
        response.setTransactionTypeText(TRANSACTION_TYPE_MAP.get(product.getTransactionType()));

        // 查询用户信息
        User user = userMapper.findById(product.getUserId());
        if (user != null) {
            response.setUsername(user.getUsername());
        }

        // 查询分类信息
        if (product.getCategoryId() != null) {
            Category category = categoryMapper.findById(product.getCategoryId());
            if (category != null) {
                response.setCategoryName(category.getName());
            } else {
                response.setCategoryName("未分类");
            }
        } else {
            response.setCategoryName("未分类");
        }

        // 检查是否已收藏
        Long userId = UserContext.getUserId();
        if (userId != null) {
            int count = favoriteMapper.existsByUserIdAndProductId(userId, product.getId());
            response.setIsFavorite(count > 0);
        } else {
            response.setIsFavorite(false);
        }

        return response;
    }

    /**
     * List转JSON字符串
     */
    private String convertListToJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        return "[\"" + String.join("\",\"", list) + "\"]";
    }

    /**
     * JSON字符串转List
     */
    private List<String> convertJsonToList(String json) {
        if (json == null || json.isEmpty() || "[]".equals(json)) {
            return Arrays.asList();
        }
        // 简单解析JSON数组
        json = json.replace("[", "").replace("]", "").replace("\"", "");
        if (json.isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.asList(json.split(","));
    }
}
