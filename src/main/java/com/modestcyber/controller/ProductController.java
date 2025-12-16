package com.modestcyber.controller;

import com.modestcyber.common.PageResult;
import com.modestcyber.common.Result;
import com.modestcyber.dto.request.PublishProductRequest;
import com.modestcyber.dto.request.UpdateProductRequest;
import com.modestcyber.dto.response.ProductResponse;
import com.modestcyber.service.FavoriteService;
import com.modestcyber.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 商品控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 发布商品
     */
    @PostMapping
    public Result<Long> publishProduct(@Valid @RequestBody PublishProductRequest request) {
        Long productId = productService.publishProduct(request);
        return Result.success(productId);
    }

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public Result<Void> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        productService.updateProduct(id, request);
        return Result.success();
    }

    /**
     * 上架/下架商品
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateProductStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateProductStatus(id, status);
        return Result.success();
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    /**
     * 商品详情
     */
    @GetMapping("/{id}")
    public Result<ProductResponse> getProductDetail(@PathVariable Long id) {
        ProductResponse response = productService.getProductDetail(id);
        return Result.success(response);
    }

    /**
     * 商品列表
     */
    @GetMapping("/list")
    public Result<PageResult<ProductResponse>> listProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "create_time") String sortBy,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PageResult<ProductResponse> result = productService.listProducts(
                categoryId, minPrice, maxPrice, status, keyword, sortBy, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 我的商品
     */
    @GetMapping("/my")
    public Result<PageResult<ProductResponse>> listMyProducts(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PageResult<ProductResponse> result = productService.listMyProducts(pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 收藏商品
     */
    @PostMapping("/{id}/favorite")
    public Result<Void> addFavorite(@PathVariable Long id) {
        favoriteService.addFavorite(id);
        return Result.success();
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{id}/favorite")
    public Result<Void> removeFavorite(@PathVariable Long id) {
        favoriteService.removeFavorite(id);
        return Result.success();
    }

    /**
     * 收藏列表
     */
    @GetMapping("/favorite")
    public Result<PageResult<ProductResponse>> listFavorites(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PageResult<ProductResponse> result = favoriteService.listFavorites(pageNum, pageSize);
        return Result.success(result);
    }
}
