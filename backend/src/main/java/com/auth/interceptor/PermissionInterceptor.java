package com.auth.interceptor;

import com.alibaba.fastjson.JSON;
import com.auth.annotation.RequirePermission;
import com.auth.common.Result;
import com.auth.service.ResourceService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

@Component
public class PermissionInterceptor implements HandlerInterceptor {
    private final ResourceService resourceService;

    public PermissionInterceptor(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("token");
        if (token == null) {
            token = request.getParameter("token");
        }
        if (token == null || !token.startsWith("token_")) {
            writeResponse(response, Result.fail(401, "未登录"));
            return false;
        }
        Long userId;
        try {
            userId = Long.parseLong(token.substring(6));
        } catch (NumberFormatException e) {
            writeResponse(response, Result.fail(401, "无效的token"));
            return false;
        }
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequirePermission methodAnnotation = handlerMethod.getMethodAnnotation(RequirePermission.class);
            RequirePermission classAnnotation = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
            if (methodAnnotation == null && classAnnotation == null) {
                return true;
            }
            Set<String> userPermissions = resourceService.getUserPermissions(userId);
            if (userPermissions.contains("*")) {
                return true;
            }
            String[] requiredPermissions = methodAnnotation != null ? methodAnnotation.value() : classAnnotation.value();
            if (requiredPermissions.length > 0) {
                boolean hasPermission = Arrays.stream(requiredPermissions)
                        .anyMatch(userPermissions::contains);
                if (!hasPermission) {
                    writeResponse(response, Result.fail(403, "无权限访问"));
                    return false;
                }
            }
        }
        return true;
    }

    private void writeResponse(HttpServletResponse response, Result<?> result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(result));
    }
}
