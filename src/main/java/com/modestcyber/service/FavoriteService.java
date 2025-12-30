package com.modestcyber.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modestcyber.common.PageResult;
import com.modestcyber.context.UserContext;
import com.modestcyber.dto.response.ProductResponse;
import com.modestcyber.exception.BusinessException;
import com.modestcyber.mapper.CategoryMapper;
import com.modestcyber.mapper.FavoriteMapper;
import com.modestcyber.mapper.ProductMapper;
import com.modestcyber.mapper.UserMapper;
import com.modestcyber.pojo.Category;
import com.modestcyber.pojo.Favorite;
import com.modestcyber.pojo.Product;
import com.modestcyber.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏服务
 */
@Slf4j
@Service
public class FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 添加收藏
     */
    @Transactional
    public void addFavorite(Long productId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        // 检查商品是否存在
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查是否已收藏
        int count = favoriteMapper.existsByUserIdAndProductId(userId, productId);
        if (count > 0) {
            throw new BusinessException("已收藏该商品");
        }

        // 添加收藏
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(favorite);

        // 增加商品收藏量
        productMapper.incrementFavoriteCount(productId);

        log.info("用户 {} 收藏商品: {}", userId, productId);
    }

    /**
     * 取消收藏
     */
    @Transactional
    public void removeFavorite(Long productId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        // 检查是否已收藏
        int count = favoriteMapper.existsByUserIdAndProductId(userId, productId);
        if (count == 0) {
            throw new BusinessException("未收藏该商品");
        }

        // 删除收藏
        favoriteMapper.delete(userId, productId);

        // 减少商品收藏量
        productMapper.decrementFavoriteCount(productId);

        log.info("用户 {} 取消收藏商品: {}", userId, productId);
    }

    /**
     * 收藏列表
     */
    public PageResult<ProductResponse> listFavorites(Integer pageNum, Integer pageSize) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        int offset = (pageNum - 1) * pageSize;

        List<Product> products = favoriteMapper.listFavoriteProducts(userId, offset, pageSize);
        Long total = favoriteMapper.countFavorites(userId);

        List<ProductResponse> responseList = products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());

        return new PageResult<>(responseList, pageNum, pageSize, total);
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
        response.setName(product.getTitle());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setOriginalPrice(product.getOriginalPrice());
        response.setStatus(product.getStatus());
        response.setCondition(product.getCondition());
        response.setQuality(product.getCondition());
        response.setTransactionType(product.getTransactionType());
        response.setViewCount(product.getViewCount());
        response.setFavoriteCount(product.getFavoriteCount());
        response.setCreateTime(product.getCreateTime());
        response.setUpdateTime(product.getUpdateTime());
        response.setIsFavorite(true);
        
        // 格式化时间
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
        
        // 获取用户信息
        User user = userMapper.findById(product.getUserId());
        if (user != null) {
            response.setUsername(user.getUsername());
            response.setUserDepartment(user.getDepartment());
        }
        
        // 获取分类信息
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
        
        return response;
    }
}
