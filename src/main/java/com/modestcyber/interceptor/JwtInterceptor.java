package com.modestcyber.interceptor;

import com.modestcyber.context.UserContext;
import com.modestcyber.util.JwtUtil;
import com.modestcyber.util.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 设置TraceId
        TraceIdUtil.setTraceId();
        
        // 获取Token
        String token = getTokenFromRequest(request);
        
        // 如果没有Token，放行（由具体接口决定是否需要登录）
        if (!StringUtils.hasText(token)) {
            log.debug("请求路径: {}, 未携带Token", request.getRequestURI());
            return true;
        }
        
        // 验证Token
        try {
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);
                
                // 设置用户上下文
                UserContext.setUserId(userId);
                UserContext.setUsername(username);
                UserContext.setRole(role);
                
                log.debug("用户 {} (ID: {}) 访问: {}", username, userId, request.getRequestURI());
                return true;
            } else {
                log.warn("Token无效: {}", token);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"Token无效或已过期\",\"data\":null,\"traceId\":\"" + TraceIdUtil.getTraceId() + "\"}");
                return false;
            }
        } catch (Exception e) {
            log.error("Token解析失败", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token解析失败\",\"data\":null,\"traceId\":\"" + TraceIdUtil.getTraceId() + "\"}");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理上下文
        UserContext.clear();
        TraceIdUtil.clear();
    }

    /**
     * 从请求头中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
