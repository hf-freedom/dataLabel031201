package com.datalabel.interceptor;

import com.datalabel.entity.Resource;
import com.datalabel.service.ResourceService;
import com.datalabel.service.RoleService;
import com.datalabel.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PermissionInterceptor implements HandlerInterceptor {
    
    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    private static final String HEADER_USER_ID = "X-User-Id";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        String userIdStr = request.getHeader(HEADER_USER_ID);
        if (userIdStr == null || userIdStr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或缺少用户标识\"}");
            return false;
        }
        
        Long userId = Long.parseLong(userIdStr);
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        if (resourceService.hasApiPermission(userId, requestURI, method, userService, roleService)) {
            return true;
        }
        
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":403,\"message\":\"无权限访问该接口\"}");
        return false;
    }
}
