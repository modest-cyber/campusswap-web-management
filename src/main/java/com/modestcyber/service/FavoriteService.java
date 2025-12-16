package com.modestcyber.service;

import com.modestcyber.common.PageResult;
import com.modestcyber.context.UserContext;
import com.modestcyber.dto.response.ProductResponse;
import com.modestcyber.exception.BusinessException;
import com.modestcyber.mapper.FavoriteMapper;
import com.modestcyber.mapper.ProductMapper;
import com.modestcyber.pojo.Favorite;
import com.modestcyber.pojo.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        int offset = (pageNum - 1) * pageSize;

        List<Product> products = favoriteMapper.listFavoriteProducts(userId, offset, pageSize);
        Long total = favoriteMapper.countFavorites(userId);

        List<ProductResponse> responseList = products.stream()
                .map(product -> {
                    // 使用ProductService的转换方法需要设置用户上下文
                    ProductResponse response = new ProductResponse();
                    org.springframework.beans.BeanUtils.copyProperties(product, response);
                    response.setIsFavorite(true);
                    return response;
                })
                .collect(Collectors.toList());

        return new PageResult<>(responseList, pageNum, pageSize, total);
    }
}
