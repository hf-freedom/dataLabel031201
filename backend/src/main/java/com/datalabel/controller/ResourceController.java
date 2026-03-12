package com.datalabel.controller;

import com.datalabel.common.PageResult;
import com.datalabel.common.Result;
import com.datalabel.dto.PageQuery;
import com.datalabel.dto.ResourceDTO;
import com.datalabel.entity.Resource;
import com.datalabel.service.ResourceService;
import com.datalabel.service.RoleService;
import com.datalabel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resource")
@CrossOrigin
public class ResourceController {
    
    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @PostMapping
    public Result<Resource> create(@RequestBody ResourceDTO dto) {
        try {
            return Result.success(resourceService.create(dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping
    public Result<Resource> update(@RequestBody ResourceDTO dto) {
        try {
            return Result.success(resourceService.update(dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return Result.success();
    }
    
    @PutMapping("/{id}/enable")
    public Result<Void> enable(@PathVariable Long id) {
        try {
            resourceService.enable(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/disable")
    public Result<Void> disable(@PathVariable Long id) {
        try {
            resourceService.disable(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<Resource> getById(@PathVariable Long id) {
        return Result.success(resourceService.getById(id));
    }
    
    @GetMapping("/list")
    public Result<List<Resource>> getAll() {
        return Result.success(resourceService.getAll());
    }
    
    @GetMapping("/type/{type}")
    public Result<List<Resource>> getByType(@PathVariable Resource.ResourceType type) {
        return Result.success(resourceService.getByType(type));
    }
    
    @GetMapping("/parent/{parentId}")
    public Result<List<Resource>> getByParentId(@PathVariable Long parentId) {
        return Result.success(resourceService.getByParentId(parentId));
    }
    
    @GetMapping("/page")
    public Result<PageResult<Resource>> getPage(PageQuery query) {
        return Result.success(resourceService.getPage(query));
    }
    
    @GetMapping("/user/{userId}/menus")
    public Result<List<Resource>> getUserMenus(@PathVariable Long userId) {
        return Result.success(resourceService.getUserMenus(userId, userService, roleService));
    }
    
    @GetMapping("/user/{userId}/resources")
    public Result<List<Resource>> getUserResources(@PathVariable Long userId) {
        return Result.success(resourceService.getUserResources(userId, userService, roleService));
    }
}
