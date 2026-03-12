package com.example.permission.controller;

import com.example.permission.common.PageResult;
import com.example.permission.common.Result;
import com.example.permission.entity.Resource;
import com.example.permission.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @GetMapping("/list")
    public Result<PageResult<Resource>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<Resource> list = resourceService.getResourcePage(keyword, type, pageNum, pageSize);
        long total = resourceService.getResourceCount(keyword, type);
        return Result.success(PageResult.of(total, list));
    }

    @GetMapping("/tree")
    public Result<List<Resource>> tree() {
        return Result.success(resourceService.getResourceTree());
    }

    @GetMapping("/all")
    public Result<List<Resource>> all() {
        return Result.success(resourceService.getResourceList());
    }

    @GetMapping("/{id}")
    public Result<Resource> getById(@PathVariable Long id) {
        Resource resource = resourceService.getResourceById(id);
        if (resource == null) {
            return Result.error("资源不存在");
        }
        return Result.success(resource);
    }

    @PostMapping
    public Result<Resource> create(@RequestBody Resource resource) {
        return Result.success(resourceService.createResource(resource));
    }

    @PutMapping
    public Result<Resource> update(@RequestBody Resource resource) {
        Resource updatedResource = resourceService.updateResource(resource);
        if (updatedResource == null) {
            return Result.error("资源不存在");
        }
        return Result.success(updatedResource);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return Result.success();
    }

    @PutMapping("/{id}/enable")
    public Result<Void> enable(@PathVariable Long id) {
        resourceService.enableResource(id);
        return Result.success();
    }

    @PutMapping("/{id}/disable")
    public Result<Void> disable(@PathVariable Long id) {
        resourceService.disableResource(id);
        return Result.success();
    }

    @GetMapping("/user/menus")
    public Result<List<Resource>> getUserMenus(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        return Result.success(resourceService.getUserMenus(userId));
    }

    @GetMapping("/user/permissions")
    public Result<List<String>> getUserPermissions(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        return Result.success(resourceService.getUserPermissions(userId));
    }
}
