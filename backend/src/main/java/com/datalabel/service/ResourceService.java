package com.datalabel.service;

import cn.hutool.core.bean.BeanUtil;
import com.datalabel.common.PageResult;
import com.datalabel.dto.PageQuery;
import com.datalabel.dto.ResourceDTO;
import com.datalabel.entity.Resource;
import com.datalabel.entity.Role;
import com.datalabel.entity.User;
import com.datalabel.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ResourceService {
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    public Resource create(ResourceDTO dto) {
        if (resourceRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("资源编码已存在");
        }
        Resource resource = new Resource();
        BeanUtil.copyProperties(dto, resource, "id");
        return resourceRepository.save(resource);
    }
    
    public Resource update(ResourceDTO dto) {
        Resource existing = resourceRepository.findById(dto.getId());
        if (existing == null) {
            throw new RuntimeException("资源不存在");
        }
        if (!existing.getCode().equals(dto.getCode()) && resourceRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("资源编码已存在");
        }
        BeanUtil.copyProperties(dto, existing, "id", "createTime", "status");
        return resourceRepository.save(existing);
    }
    
    public void delete(Long id) {
        resourceRepository.deleteById(id);
    }
    
    public void enable(Long id) {
        Resource resource = resourceRepository.findById(id);
        if (resource == null) {
            throw new RuntimeException("资源不存在");
        }
        resource.setStatus(1);
        resourceRepository.save(resource);
    }
    
    public void disable(Long id) {
        Resource resource = resourceRepository.findById(id);
        if (resource == null) {
            throw new RuntimeException("资源不存在");
        }
        resource.setStatus(0);
        resourceRepository.save(resource);
    }
    
    public Resource getById(Long id) {
        return resourceRepository.findById(id);
    }
    
    public List<Resource> getAll() {
        return resourceRepository.findAllActive();
    }
    
    public List<Resource> getByType(Resource.ResourceType type) {
        return resourceRepository.findByType(type);
    }
    
    public List<Resource> getByParentId(Long parentId) {
        return resourceRepository.findByParentId(parentId);
    }
    
    public PageResult<Resource> getPage(PageQuery query) {
        List<Resource> all = resourceRepository.findByKeyword(query.getKeyword());
        int start = (query.getPageNum() - 1) * query.getPageSize();
        int end = Math.min(start + query.getPageSize(), all.size());
        List<Resource> records = all.subList(start, end);
        return PageResult.of(records, (long) all.size(), query.getPageNum(), query.getPageSize());
    }
    
    public List<Resource> getUserMenus(Long userId, UserService userService, RoleService roleService) {
        User user = userService.getById(userId);
        if (user == null || user.getRoleIds() == null || user.getRoleIds().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Role> roles = roleService.getByIds(user.getRoleIds());
        Set<Long> resourceIds = roles.stream()
                .filter(r -> r.getResourceIds() != null)
                .flatMap(r -> r.getResourceIds().stream())
                .collect(Collectors.toSet());
        
        List<Resource> resources = resourceRepository.findByIds(new ArrayList<>(resourceIds));
        return resources.stream()
                .filter(r -> r.getType() == Resource.ResourceType.MENU || r.getType() == Resource.ResourceType.PAGE)
                .collect(Collectors.toList());
    }
    
    public List<Resource> getUserResources(Long userId, UserService userService, RoleService roleService) {
        User user = userService.getById(userId);
        if (user == null || user.getRoleIds() == null || user.getRoleIds().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Role> roles = roleService.getByIds(user.getRoleIds());
        Set<Long> resourceIds = roles.stream()
                .filter(r -> r.getResourceIds() != null)
                .flatMap(r -> r.getResourceIds().stream())
                .collect(Collectors.toSet());
        
        return resourceRepository.findByIds(new ArrayList<>(resourceIds));
    }
    
    public boolean hasApiPermission(Long userId, String apiPath, String apiMethod, UserService userService, RoleService roleService) {
        List<Resource> userResources = getUserResources(userId, userService, roleService);
        return userResources.stream()
                .filter(r -> r.getType() == Resource.ResourceType.API)
                .anyMatch(r -> apiPath.equals(r.getApiPath()) && apiMethod.equalsIgnoreCase(r.getApiMethod()));
    }
}
