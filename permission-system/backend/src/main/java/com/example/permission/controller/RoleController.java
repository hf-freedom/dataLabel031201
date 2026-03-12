package com.example.permission.controller;

import com.example.permission.common.PageResult;
import com.example.permission.common.Result;
import com.example.permission.entity.Role;
import com.example.permission.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public Result<PageResult<Role>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<Role> list = roleService.getRolePage(keyword, pageNum, pageSize);
        long total = roleService.getRoleCount(keyword);
        return Result.success(PageResult.of(total, list));
    }

    @GetMapping("/all")
    public Result<List<Role>> all() {
        return Result.success(roleService.getRoleList());
    }

    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    @PostMapping
    public Result<Role> create(@RequestBody Role role) {
        return Result.success(roleService.createRole(role));
    }

    @PutMapping
    public Result<Role> update(@RequestBody Role role) {
        Role updatedRole = roleService.updateRole(role);
        if (updatedRole == null) {
            return Result.error("角色不存在");
        }
        return Result.success(updatedRole);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    @PostMapping("/{id}/resources")
    public Result<Void> bindResources(@PathVariable Long id, @RequestBody List<Long> resourceIds) {
        Role role = roleService.getRoleById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        roleService.bindResources(id, resourceIds);
        return Result.success();
    }

    @GetMapping("/{id}/resources")
    public Result<List<Long>> getRoleResources(@PathVariable Long id) {
        return Result.success(roleService.getRoleResourceIds(id));
    }
}
