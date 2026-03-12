package com.datalabel.controller;

import com.datalabel.common.PageResult;
import com.datalabel.common.Result;
import com.datalabel.dto.PageQuery;
import com.datalabel.dto.UserDTO;
import com.datalabel.entity.User;
import com.datalabel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public Result<User> create(@RequestBody UserDTO dto) {
        try {
            return Result.success(userService.create(dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping
    public Result<User> update(@RequestBody UserDTO dto) {
        try {
            return Result.success(userService.update(dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }
    
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }
    
    @GetMapping("/list")
    public Result<List<User>> getAll() {
        return Result.success(userService.getAll());
    }
    
    @GetMapping("/page")
    public Result<PageResult<User>> getPage(PageQuery query) {
        return Result.success(userService.getPage(query));
    }
    
    @PostMapping("/{userId}/roles")
    public Result<Void> bindRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        try {
            userService.bindRoles(userId, roleIds);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
