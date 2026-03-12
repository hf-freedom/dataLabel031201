package com.example.permission.service.impl;

import com.example.permission.entity.Resource;
import com.example.permission.entity.Role;
import com.example.permission.service.ResourceService;
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
public class ResourceServiceImpl implements ResourceService {

    private final Map<Long, Resource> resourceStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @PostConstruct
    public void init() {
        Long systemMenuId = createMenu("系统管理", "system", null, 1, "Setting");
        Long userMenuId = createMenu("用户管理", "user", systemMenuId, 1, "User");
        Long roleMenuId = createMenu("角色管理", "role", systemMenuId, 2, "UserFilled");
        Long resourceMenuId = createMenu("资源管理", "resource", systemMenuId, 3, "Menu");

        createApi("用户列表", "/api/user/list", "GET", null);
        createApi("用户详情", "/api/user/{id}", "GET", null);
        createApi("创建用户", "/api/user", "POST", null);
        createApi("更新用户", "/api/user", "PUT", null);
        createApi("删除用户", "/api/user/{id}", "DELETE", null);
        createApi("绑定角色", "/api/user/{id}/roles", "POST", null);

        createApi("角色列表", "/api/role/list", "GET", null);
        createApi("角色详情", "/api/role/{id}", "GET", null);
        createApi("创建角色", "/api/role", "POST", null);
        createApi("更新角色", "/api/role", "PUT", null);
        createApi("删除角色", "/api/role/{id}", "DELETE", null);
        createApi("绑定资源", "/api/role/{id}/resources", "POST", null);

        createApi("资源列表", "/api/resource/list", "GET", null);
        createApi("资源树", "/api/resource/tree", "GET", null);
        createApi("资源详情", "/api/resource/{id}", "GET", null);
        createApi("创建资源", "/api/resource", "POST", null);
        createApi("更新资源", "/api/resource", "PUT", null);
        createApi("删除资源", "/api/resource/{id}", "DELETE", null);
        createApi("启用资源", "/api/resource/{id}/enable", "PUT", null);
        createApi("停用资源", "/api/resource/{id}/disable", "PUT", null);
        createApi("用户菜单", "/api/resource/user/menus", "GET", null);
        createApi("用户权限", "/api/resource/user/permissions", "GET", null);

        roleService.bindResources(1L, new ArrayList<>(resourceStore.keySet()));
    }

    private Long createMenu(String name, String code, Long parentId, int sort, String icon) {
        Resource resource = new Resource();
        resource.setId(idGenerator.getAndIncrement());
        resource.setResourceName(name);
        resource.setResourceCode(code);
        resource.setType(Resource.TYPE_MENU);
        resource.setParentId(parentId);
        resource.setSort(sort);
        resource.setIcon(icon);
        resource.setStatus(Resource.STATUS_ENABLED);
        resource.setCreateTime(new Date());
        resource.setUpdateTime(new Date());
        resourceStore.put(resource.getId(), resource);
        return resource.getId();
    }

    private void createApi(String name, String url, String method, Long parentId) {
        Resource resource = new Resource();
        resource.setId(idGenerator.getAndIncrement());
        resource.setResourceName(name);
        resource.setResourceCode(url.replace("/", ".").substring(1));
        resource.setType(Resource.TYPE_API);
        resource.setUrl(url);
        resource.setMethod(method);
        resource.setParentId(parentId);
        resource.setStatus(Resource.STATUS_ENABLED);
        resource.setCreateTime(new Date());
        resource.setUpdateTime(new Date());
        resourceStore.put(resource.getId(), resource);
    }

    @Override
    public Resource createResource(Resource resource) {
        resource.setId(idGenerator.getAndIncrement());
        resource.setCreateTime(new Date());
        resource.setUpdateTime(new Date());
        resourceStore.put(resource.getId(), resource);
        return resource;
    }

    @Override
    public Resource updateResource(Resource resource) {
        Resource existingResource = resourceStore.get(resource.getId());
        if (existingResource == null) {
            return null;
        }
        if (resource.getResourceName() != null) {
            existingResource.setResourceName(resource.getResourceName());
        }
        if (resource.getResourceCode() != null) {
            existingResource.setResourceCode(resource.getResourceCode());
        }
        if (resource.getType() != null) {
            existingResource.setType(resource.getType());
        }
        if (resource.getUrl() != null) {
            existingResource.setUrl(resource.getUrl());
        }
        if (resource.getMethod() != null) {
            existingResource.setMethod(resource.getMethod());
        }
        if (resource.getParentId() != null) {
            existingResource.setParentId(resource.getParentId());
        }
        if (resource.getIcon() != null) {
            existingResource.setIcon(resource.getIcon());
        }
        if (resource.getSort() != null) {
            existingResource.setSort(resource.getSort());
        }
        existingResource.setUpdateTime(new Date());
        return existingResource;
    }

    @Override
    public void deleteResource(Long id) {
        resourceStore.remove(id);
        List<Long> childrenIds = resourceStore.values().stream()
                .filter(r -> id.equals(r.getParentId()))
                .map(Resource::getId)
                .collect(Collectors.toList());
        childrenIds.forEach(this::deleteResource);
    }

    @Override
    public Resource getResourceById(Long id) {
        return resourceStore.get(id);
    }

    @Override
    public List<Resource> getResourceList() {
        return new ArrayList<>(resourceStore.values());
    }

    @Override
    public List<Resource> getResourceByType(Integer type) {
        return resourceStore.values().stream()
                .filter(r -> r.getType().equals(type))
                .collect(Collectors.toList());
    }

    @Override
    public List<Resource> getResourceTree() {
        List<Resource> allResources = new ArrayList<>(resourceStore.values());
        return buildTree(allResources, null);
    }

    private List<Resource> buildTree(List<Resource> resources, Long parentId) {
        List<Resource> tree = new ArrayList<>();
        for (Resource resource : resources) {
            if (Objects.equals(resource.getParentId(), parentId)) {
                resource.setChildren(buildTree(resources, resource.getId()));
                tree.add(resource);
            }
        }
        tree.sort(Comparator.comparing(Resource::getSort, Comparator.nullsLast(Integer::compareTo)));
        return tree;
    }

    @Override
    public List<Resource> getResourcePage(String keyword, Integer type, int pageNum, int pageSize) {
        return resourceStore.values().stream()
                .filter(r -> (keyword == null || keyword.isEmpty()
                        || r.getResourceName().contains(keyword)
                        || r.getResourceCode().contains(keyword))
                        && (type == null || r.getType().equals(type)))
                .skip((long) (pageNum - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public long getResourceCount(String keyword, Integer type) {
        return resourceStore.values().stream()
                .filter(r -> (keyword == null || keyword.isEmpty()
                        || r.getResourceName().contains(keyword)
                        || r.getResourceCode().contains(keyword))
                        && (type == null || r.getType().equals(type)))
                .count();
    }

    @Override
    public void enableResource(Long id) {
        Resource resource = resourceStore.get(id);
        if (resource != null) {
            resource.setStatus(Resource.STATUS_ENABLED);
            resource.setUpdateTime(new Date());
        }
    }

    @Override
    public void disableResource(Long id) {
        Resource resource = resourceStore.get(id);
        if (resource != null) {
            resource.setStatus(Resource.STATUS_DISABLED);
            resource.setUpdateTime(new Date());
        }
    }

    @Override
    public List<Resource> getUserMenus(Long userId) {
        List<Long> roleIds = userService.getUserRoleIds(userId);
        Set<Long> resourceIds = new HashSet<>();
        for (Long roleId : roleIds) {
            resourceIds.addAll(roleService.getRoleResourceIds(roleId));
        }

        List<Resource> menus = resourceStore.values().stream()
                .filter(r -> resourceIds.contains(r.getId())
                        && (r.getType().equals(Resource.TYPE_MENU) || r.getType().equals(Resource.TYPE_PAGE))
                        && r.getStatus().equals(Resource.STATUS_ENABLED))
                .collect(Collectors.toList());

        return buildTree(menus, null);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        List<Long> roleIds = userService.getUserRoleIds(userId);
        Set<Long> resourceIds = new HashSet<>();
        for (Long roleId : roleIds) {
            resourceIds.addAll(roleService.getRoleResourceIds(roleId));
        }

        return resourceStore.values().stream()
                .filter(r -> resourceIds.contains(r.getId())
                        && r.getStatus().equals(Resource.STATUS_ENABLED))
                .map(Resource::getResourceCode)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasPermission(Long userId, String url, String method) {
        List<Long> roleIds = userService.getUserRoleIds(userId);
        Set<Long> resourceIds = new HashSet<>();
        for (Long roleId : roleIds) {
            resourceIds.addAll(roleService.getRoleResourceIds(roleId));
        }

        return resourceStore.values().stream()
                .anyMatch(r -> resourceIds.contains(r.getId())
                        && r.getType().equals(Resource.TYPE_API)
                        && r.getStatus().equals(Resource.STATUS_ENABLED)
                        && matchUrl(r.getUrl(), url)
                        && r.getMethod().equalsIgnoreCase(method));
    }

    private boolean matchUrl(String pattern, String url) {
        if (pattern == null || url == null) {
            return false;
        }
        String regex = pattern.replace("**", ".*").replace("*", "[^/]*");
        return url.matches(regex);
    }
}
