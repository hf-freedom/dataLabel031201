package com.auth.controller;

import com.auth.common.Result;
import com.auth.entity.User;
import com.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        User user = userService.getByUsername(username);
        if (user == null || !password.equals(user.getPassword())) {
            return Result.fail("用户名或密码错误");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("token", "token_" + user.getId());
        result.put("user", user);
        return Result.success(result);
    }

    @GetMapping("/info")
    public Result<User> getInfo(@RequestParam String token) {
        if (token != null && token.startsWith("token_")) {
            String userIdStr = token.substring(6);
            try {
                Long userId = Long.parseLong(userIdStr);
                User user = userService.getById(userId);
                return Result.success(user);
            } catch (NumberFormatException e) {
                return Result.fail("无效的token");
            }
        }
        return Result.fail("无效的token");
    }
}
