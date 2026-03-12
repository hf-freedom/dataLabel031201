package com.auth.controller;

import com.auth.common.Result;
import com.auth.entity.User;
import com.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<User>> list(@RequestParam(required = false) String username,
                                   @RequestParam(required = false) String nickname) {
        return Result.success(userService.list(username, nickname));
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @PostMapping("/save")
    public Result<User> save(@RequestBody User user) {
        return Result.success(userService.save(user));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(userService.delete(id));
    }
}
