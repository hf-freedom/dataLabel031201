package com.auth.service;

import com.auth.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private static final Map<Long, Role> roleMap = new ConcurrentHashMap<>();
    private static long idCounter = 1;

    @Autowired
    private UserService userService;

    static {
        Role adminRole = new Role();
        adminRole.setId(idCounter++);
        adminRole.setName("超级管理员");
        adminRole.setCode("ROLE_ADMIN");
        adminRole.setDescription("拥有所有权限");
        adminRole.setStatus(1);
        adminRole.setCreateTime(new Date());
        adminRole.setUpdateTime(new Date());
        adminRole.setResourceIds(new ArrayList<>());
        adminRole.setUserIds(Arrays.asList(1L));
        roleMap.put(adminRole.getId(), adminRole);

        Role userRole = new Role();
        userRole.setId(idCounter++);
        userRole.setName("普通用户");
        userRole.setCode("ROLE_USER");
        userRole.setDescription("普通用户权限");
        userRole.setStatus(1);
        userRole.setCreateTime(new Date());
        userRole.setUpdateTime(new Date());
        userRole.setResourceIds(new ArrayList<>());
        userRole.setUserIds(Arrays.asList(2L));
        roleMap.put(userRole.getId(), userRole);
    }

    public List<Role> list(String name, String code) {
        return roleMap.values().stream()
                .filter(r -> (name == null || r.getName().contains(name))
                        && (code == null || r.getCode().contains(code)))
                .collect(Collectors.toList());
    }

    public Role getById(Long id) {
        return roleMap.get(id);
    }

    public Role save(Role role) {
        if (role.getId() == null) {
            role.setId(idCounter++);
            role.setCreateTime(new Date());
        }
        role.setUpdateTime(new Date());
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        if (role.getResourceIds() == null) {
            role.setResourceIds(new ArrayList<>());
        }
        if (role.getUserIds() == null) {
            role.setUserIds(new ArrayList<>());
        }
        roleMap.put(role.getId(), role);
        return role;
    }

    public boolean delete(Long id) {
        return roleMap.remove(id) != null;
    }

    public void assignResources(Long roleId, List<Long> resourceIds) {
        Role role = roleMap.get(roleId);
        if (role != null) {
            role.setResourceIds(resourceIds);
            role.setUpdateTime(new Date());
        }
    }

    public void assignUsers(Long roleId, List<Long> userIds) {
        Role role = roleMap.get(roleId);
        if (role != null) {
            role.setUserIds(userIds);
            role.setUpdateTime(new Date());
            userService.getUsersByRoleId(roleId).forEach(user -> {
                List<Long> roleIds = new ArrayList<>(user.getRoleIds());
                roleIds.remove(roleId);
                user.setRoleIds(roleIds);
            });
            if (userIds != null) {
                userIds.forEach(userId -> {
                    com.auth.entity.User user = userService.getById(userId);
                    if (user != null) {
                        List<Long> roleIds = user.getRoleIds() != null ? new ArrayList<>(user.getRoleIds()) : new ArrayList<>();
                        if (!roleIds.contains(roleId)) {
                            roleIds.add(roleId);
                            user.setRoleIds(roleIds);
                        }
                    }
                });
            }
        }
    }

    public List<Role> getRolesByUserId(Long userId) {
        return roleMap.values().stream()
                .filter(r -> r.getUserIds() != null && r.getUserIds().contains(userId))
                .collect(Collectors.toList());
    }
}
