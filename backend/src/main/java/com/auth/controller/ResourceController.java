package com.auth.controller;

import com.auth.common.Result;
import com.auth.entity.Resource;
import com.auth.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resource")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    @GetMapping("/tree")
    public Result<List<Resource>> tree() {
        return Result.success(resourceService.tree());
    }

    @GetMapping("/list")
    public Result<List<Resource>> list(@RequestParam(required = false) String name,
                                       @RequestParam(required = false) String type) {
        return Result.success(resourceService.list(name, type));
    }

    @GetMapping("/{id}")
    public Result<Resource> getById(@PathVariable Long id) {
        return Result.success(resourceService.getById(id));
    }

    @PostMapping("/save")
    public Result<Resource> save(@RequestBody Resource resource) {
        return Result.success(resourceService.save(resource));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(resourceService.delete(id));
    }

    @PostMapping("/updateStatus")
    public Result<Boolean> updateStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        return Result.success(resourceService.updateStatus(id, status));
    }

    @GetMapping("/userResources")
    public Result<List<com.auth.entity.Resource>> getUserResources(@RequestHeader("token") String token) {
        Long userId = Long.parseLong(token.substring(6));
        return Result.success(resourceService.getUserResources(userId));
    }
}
