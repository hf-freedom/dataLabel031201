package com.auth.controller;

import com.auth.common.Result;
import com.auth.entity.Role;
import com.auth.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public Result<List<Role>> list(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String code) {
        return Result.success(roleService.list(name, code));
    }

    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @PostMapping("/save")
    public Result<Role> save(@RequestBody Role role) {
        return Result.success(roleService.save(role));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(roleService.delete(id));
    }

    @PostMapping("/assignResources")
    public Result<Void> assignResources(@RequestBody Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        List<Long> resourceIds = (List<Long>) params.get("resourceIds");
        roleService.assignResources(roleId, resourceIds);
        return Result.success();
    }

    @PostMapping("/assignUsers")
    public Result<Void> assignUsers(@RequestBody Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        List<Long> userIds = (List<Long>) params.get("userIds");
        roleService.assignUsers(roleId, userIds);
        return Result.success();
    }
}
