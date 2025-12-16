package com.modestcyber.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 统一响应结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private String traceId;

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data, generateTraceId());
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null, generateTraceId());
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    private static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
