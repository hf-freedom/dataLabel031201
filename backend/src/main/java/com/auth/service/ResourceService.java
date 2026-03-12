package com.auth.service;

import com.auth.entity.Resource;
import com.auth.entity.Role;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ResourceService {
    private static final Map<Long, Resource> resourceMap = new ConcurrentHashMap<>();
    private static long idCounter = 1;

    static {
        initResources();
    }

    private static void initResources() {
        Resource system = new Resource();
        system.setId(idCounter++);
        system.setName("系统管理");
        system.setCode("SYSTEM_MANAGE");
        system.setType("menu");
        system.setPath("/system");
        system.setComponent("Layout");
        system.setIcon("el-icon-setting");
        system.setSort(1);
        system.setParentId(0L);
        system.setStatus(1);
        system.setCreateTime(new Date());
        system.setUpdateTime(new Date());
        resourceMap.put(system.getId(), system);

        Resource userManage = new Resource();
        userManage.setId(idCounter++);
        userManage.setName("用户管理");
        userManage.setCode("USER_MANAGE");
        userManage.setType("menu");
        userManage.setPath("/system/user");
        userManage.setComponent("system/user");
        userManage.setIcon("el-icon-user");
        userManage.setSort(1);
        userManage.setParentId(system.getId());
        userManage.setStatus(1);
        userManage.setPermission("system:user:list");
        userManage.setCreateTime(new Date());
        userManage.setUpdateTime(new Date());
        resourceMap.put(userManage.getId(), userManage);

        Resource roleManage = new Resource();
        roleManage.setId(idCounter++);
        roleManage.setName("角色管理");
        roleManage.setCode("ROLE_MANAGE");
        roleManage.setType("menu");
        roleManage.setPath("/system/role");
        roleManage.setComponent("system/role");
        roleManage.setIcon("el-icon-s-custom");
        roleManage.setSort(2);
        roleManage.setParentId(system.getId());
        roleManage.setStatus(1);
        roleManage.setPermission("system:role:list");
        roleManage.setCreateTime(new Date());
        roleManage.setUpdateTime(new Date());
        resourceMap.put(roleManage.getId(), roleManage);

        Resource resourceManage = new Resource();
        resourceManage.setId(idCounter++);
        resourceManage.setName("资源管理");
        resourceManage.setCode("RESOURCE_MANAGE");
        resourceManage.setType("menu");
        resourceManage.setPath("/system/resource");
        resourceManage.setComponent("system/resource");
        resourceManage.setIcon("el-icon-menu");
        resourceManage.setSort(3);
        resourceManage.setParentId(system.getId());
        resourceManage.setStatus(1);
        resourceManage.setPermission("system:resource:list");
        resourceManage.setCreateTime(new Date());
        resourceManage.setUpdateTime(new Date());
        resourceMap.put(resourceManage.getId(), resourceManage);

        Resource userAdd = new Resource();
        userAdd.setId(idCounter++);
        userAdd.setName("新增用户");
        userAdd.setCode("USER_ADD");
        userAdd.setType("button");
        userAdd.setStatus(1);
        userAdd.setParentId(userManage.getId());
        userAdd.setPermission("system:user:add");
        userAdd.setCreateTime(new Date());
        userAdd.setUpdateTime(new Date());
        resourceMap.put(userAdd.getId(), userAdd);

        Resource userEdit = new Resource();
        userEdit.setId(idCounter++);
        userEdit.setName("编辑用户");
        userEdit.setCode("USER_EDIT");
        userEdit.setType("button");
        userEdit.setStatus(1);
        userEdit.setParentId(userManage.getId());
        userEdit.setPermission("system:user:edit");
        userEdit.setCreateTime(new Date());
        userEdit.setUpdateTime(new Date());
        resourceMap.put(userEdit.getId(), userEdit);

        Resource userDelete = new Resource();
        userDelete.setId(idCounter++);
        userDelete.setName("删除用户");
        userDelete.setCode("USER_DELETE");
        userDelete.setType("button");
        userDelete.setStatus(1);
        userDelete.setParentId(userManage.getId());
        userDelete.setPermission("system:user:delete");
        userDelete.setCreateTime(new Date());
        userDelete.setUpdateTime(new Date());
        resourceMap.put(userDelete.getId(), userDelete);
    }

    public List<Resource> tree() {
        List<Resource> all = resourceMap.values().stream()
                .filter(r -> r.getStatus() == 1)
                .collect(Collectors.toList());
        return buildTree(all, 0L);
    }

    public List<Resource> list(String name, String type) {
        return resourceMap.values().stream()
                .filter(r -> (name == null || r.getName().contains(name))
                        && (type == null || r.getType().equals(type)))
                .collect(Collectors.toList());
    }

    private List<Resource> buildTree(List<Resource> all, Long parentId) {
        List<Resource> children = all.stream()
                .filter(r -> parentId.equals(r.getParentId()))
                .sorted(Comparator.comparing(Resource::getSort))
                .collect(Collectors.toList());
        children.forEach(r -> r.setChildren(buildTree(all, r.getId())));
        return children;
    }

    public Resource getById(Long id) {
        return resourceMap.get(id);
    }

    public Resource save(Resource resource) {
        if (resource.getId() == null) {
            resource.setId(idCounter++);
            resource.setCreateTime(new Date());
        }
        resource.setUpdateTime(new Date());
        if (resource.getStatus() == null) {
            resource.setStatus(1);
        }
        if (resource.getParentId() == null) {
            resource.setParentId(0L);
        }
        resourceMap.put(resource.getId(), resource);
        return resource;
    }

    public boolean delete(Long id) {
        Resource resource = resourceMap.get(id);
        if (resource == null) return false;
        resourceMap.remove(id);
        resourceMap.values().stream()
                .filter(r -> id.equals(r.getParentId()))
                .forEach(r -> delete(r.getId()));
        return true;
    }

    public boolean updateStatus(Long id, Integer status) {
        Resource resource = resourceMap.get(id);
        if (resource == null) return false;
        resource.setStatus(status);
        resource.setUpdateTime(new Date());
        return true;
    }

    public List<Resource> getResourcesByRoleId(Long roleId) {
        Role role = null;
        try {
            role = com.auth.common.SpringContextUtil.getBean(RoleService.class).getById(roleId);
        } catch (Exception e) {}
        if (role == null || role.getResourceIds() == null) {
            return new ArrayList<>();
        }
        return role.getResourceIds().stream()
                .map(resourceMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Set<String> getUserPermissions(Long userId) {
        Set<String> permissions = new HashSet<>();
        try {
            RoleService roleService = com.auth.common.SpringContextUtil.getBean(RoleService.class);
            List<Role> roles = roleService.getRolesByUserId(userId);
            for (Role role : roles) {
                if ("ROLE_ADMIN".equals(role.getCode())) {
                    permissions.add("*");
                    return permissions;
                }
                if (role.getResourceIds() == null) continue;
                for (Long resourceId : role.getResourceIds()) {
                    Resource resource = resourceMap.get(resourceId);
                    if (resource != null && resource.getPermission() != null) {
                        permissions.add(resource.getPermission());
                    }
                }
            }
        } catch (Exception e) {}
        return permissions;
    }

    public List<Resource> getUserResources(Long userId) {
        List<Resource> all = new ArrayList<>();
        try {
            RoleService roleService = com.auth.common.SpringContextUtil.getBean(RoleService.class);
            List<Role> roles = roleService.getRolesByUserId(userId);
            boolean isAdmin = roles.stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getCode()));
            if (isAdmin) {
                all.addAll(resourceMap.values().stream().filter(r -> r.getStatus() == 1).collect(Collectors.toList()));
            } else {
                Set<Long> resourceIds = new HashSet<>();
                for (Role role : roles) {
                    if (role.getResourceIds() != null) {
                        resourceIds.addAll(role.getResourceIds());
                    }
                }
                for (Long resourceId : resourceIds) {
                    Resource resource = resourceMap.get(resourceId);
                    if (resource != null && resource.getStatus() == 1) {
                        all.add(resource);
                    }
                }
            }
        } catch (Exception e) {}
        return buildTree(all, 0L);
    }
}
