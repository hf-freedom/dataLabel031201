package com.datalabel.controller;

import com.datalabel.common.PageResult;
import com.datalabel.common.Result;
import com.datalabel.dto.PageQuery;
import com.datalabel.dto.RoleDTO;
import com.datalabel.entity.Role;
import com.datalabel.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@CrossOrigin
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    @PostMapping
    public Result<Role> create(@RequestBody RoleDTO dto) {
        try {
            return Result.success(roleService.create(dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping
    public Result<Role> update(@RequestBody RoleDTO dto) {
        try {
            return Result.success(roleService.update(dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }
    
    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }
    
    @GetMapping("/list")
    public Result<List<Role>> getAll() {
        return Result.success(roleService.getAll());
    }
    
    @GetMapping("/page")
    public Result<PageResult<Role>> getPage(PageQuery query) {
        return Result.success(roleService.getPage(query));
    }
    
    @PostMapping("/{roleId}/resources")
    public Result<Void> bindResources(@PathVariable Long roleId, @RequestBody List<Long> resourceIds) {
        try {
            roleService.bindResources(roleId, resourceIds);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
