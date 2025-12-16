package com.modestcyber.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 更新商品请求DTO
 */
@Data
public class UpdateProductRequest {
    @NotBlank(message = "商品标题不能为空")
    @Size(max = 200, message = "商品标题不能超过200字符")
    private String title;
    
    @NotBlank(message = "商品描述不能为空")
    private String description;
    
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    @NotEmpty(message = "商品图片不能为空")
    private List<String> images;
    
    private String condition;
    
    @NotNull(message = "交易方式不能为空")
    @Min(value = 0, message = "交易方式无效")
    @Max(value = 2, message = "交易方式无效")
    private Integer transactionType;
}
