package com.example.permission.service.impl;

import com.example.permission.entity.Role;
import com.example.permission.service.RoleService;
import com.example.permission.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final Map<Long, Role> roleStore = new ConcurrentHashMap<>();
    private final Map<Long, List<Long>> roleResourceStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {
        Role adminRole = new Role();
        adminRole.setId(idGenerator.getAndIncrement());
        adminRole.setRoleName("超级管理员");
        adminRole.setRoleCode("super_admin");
        adminRole.setDescription("拥有所有权限");
        adminRole.setStatus(Role.STATUS_ENABLED);
        adminRole.setCreateTime(new Date());
        adminRole.setUpdateTime(new Date());
        roleStore.put(adminRole.getId(), adminRole);

        Role userRole = new Role();
        userRole.setId(idGenerator.getAndIncrement());
        userRole.setRoleName("普通用户");
        userRole.setRoleCode("normal_user");
        userRole.setDescription("普通用户权限");
        userRole.setStatus(Role.STATUS_ENABLED);
        userRole.setCreateTime(new Date());
        userRole.setUpdateTime(new Date());
        roleStore.put(userRole.getId(), userRole);

        userService.bindRoles(1L, Arrays.asList(1L));
    }

    @Override
    public Role createRole(Role role) {
        role.setId(idGenerator.getAndIncrement());
        role.setCreateTime(new Date());
        role.setUpdateTime(new Date());
        roleStore.put(role.getId(), role);
        return role;
    }

    @Override
    public Role updateRole(Role role) {
        Role existingRole = roleStore.get(role.getId());
        if (existingRole == null) {
            return null;
        }
        if (role.getRoleName() != null) {
            existingRole.setRoleName(role.getRoleName());
        }
        if (role.getRoleCode() != null) {
            existingRole.setRoleCode(role.getRoleCode());
        }
        if (role.getDescription() != null) {
            existingRole.setDescription(role.getDescription());
        }
        if (role.getStatus() != null) {
            existingRole.setStatus(role.getStatus());
        }
        existingRole.setUpdateTime(new Date());
        return existingRole;
    }

    @Override
    public void deleteRole(Long id) {
        roleStore.remove(id);
        roleResourceStore.remove(id);
    }

    @Override
    public Role getRoleById(Long id) {
        Role role = roleStore.get(id);
        if (role != null) {
            role.setResourceIds(getRoleResourceIds(id));
        }
        return role;
    }

    @Override
    public List<Role> getRoleList() {
        return new ArrayList<>(roleStore.values());
    }

    @Override
    public List<Role> getRolePage(String keyword, int pageNum, int pageSize) {
        return roleStore.values().stream()
                .filter(r -> keyword == null || keyword.isEmpty()
                        || r.getRoleName().contains(keyword)
                        || r.getRoleCode().contains(keyword))
                .skip((long) (pageNum - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public long getRoleCount(String keyword) {
        return roleStore.values().stream()
                .filter(r -> keyword == null || keyword.isEmpty()
                        || r.getRoleName().contains(keyword)
                        || r.getRoleCode().contains(keyword))
                .count();
    }

    @Override
    public void bindResources(Long roleId, List<Long> resourceIds) {
        roleResourceStore.put(roleId, new ArrayList<>(resourceIds));
        Role role = roleStore.get(roleId);
        if (role != null) {
            role.setResourceIds(new ArrayList<>(resourceIds));
        }
    }

    @Override
    public List<Long> getRoleResourceIds(Long roleId) {
        return roleResourceStore.getOrDefault(roleId, new ArrayList<>());
    }

    @Override
    public List<Role> getUserRoles(Long userId) {
        List<Long> roleIds = userService.getUserRoleIds(userId);
        return roleIds.stream()
                .map(roleStore::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
