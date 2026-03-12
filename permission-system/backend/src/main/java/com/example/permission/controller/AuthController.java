package com.example.permission.controller;

import com.example.permission.common.Result;
import com.example.permission.entity.User;
import com.example.permission.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        User user = userService.getUserByUsername(request.getUsername());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        if (!user.getPassword().equals(request.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        if (User.STATUS_DISABLED.equals(user.getStatus())) {
            return Result.error("用户已被禁用");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());

        return Result.success(result);
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
