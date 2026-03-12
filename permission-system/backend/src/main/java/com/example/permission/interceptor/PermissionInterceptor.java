package com.example.permission.interceptor;

import com.example.permission.common.Result;
import com.example.permission.entity.User;
import com.example.permission.service.ResourceService;
import com.example.permission.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null || userIdStr.isEmpty()) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "未登录")));
            return false;
        }

        Long userId = Long.parseLong(userIdStr);
        User user = userService.getUserById(userId);
        if (user == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "用户不存在")));
            return false;
        }

        if (User.STATUS_DISABLED.equals(user.getStatus())) {
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(403, "用户已被禁用")));
            return false;
        }

        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        boolean hasPermission = resourceService.hasPermission(userId, requestUri, method);

        if (!hasPermission) {
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(403, "无权限访问")));
            return false;
        }

        request.setAttribute("currentUser", user);
        return true;
    }
}
