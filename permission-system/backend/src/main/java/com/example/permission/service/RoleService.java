package com.example.permission.service;

import com.example.permission.entity.Role;

import java.util.List;

public interface RoleService {

    Role createRole(Role role);

    Role updateRole(Role role);

    void deleteRole(Long id);

    Role getRoleById(Long id);

    List<Role> getRoleList();

    List<Role> getRolePage(String keyword, int pageNum, int pageSize);

    long getRoleCount(String keyword);

    void bindResources(Long roleId, List<Long> resourceIds);

    List<Long> getRoleResourceIds(Long roleId);

    List<Role> getUserRoles(Long userId);
}
