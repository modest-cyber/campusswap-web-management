package com.modestcyber.interceptor;

import com.modestcyber.annotation.RequireAdmin;
import com.modestcyber.context.UserContext;
import com.modestcyber.util.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理员权限拦截器
 */
@Slf4j
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireAdmin requireAdmin = handlerMethod.getMethodAnnotation(RequireAdmin.class);

        if (requireAdmin != null) {
            String role = UserContext.getRole();
            if (!"admin".equals(role)) {
                log.warn("非管理员用户尝试访问管理员接口: {}", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"message\":\"无权限访问\",\"data\":null,\"traceId\":\"" + TraceIdUtil.getTraceId() + "\"}");
                return false;
            }
        }

        return true;
    }
}
