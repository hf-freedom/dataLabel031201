package com.datalabel.service;

import cn.hutool.core.bean.BeanUtil;
import com.datalabel.common.PageResult;
import com.datalabel.dto.PageQuery;
import com.datalabel.dto.RoleDTO;
import com.datalabel.entity.Role;
import com.datalabel.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
    public Role create(RoleDTO dto) {
        if (roleRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("角色编码已存在");
        }
        Role role = new Role();
        BeanUtil.copyProperties(dto, role, "id");
        return roleRepository.save(role);
    }
    
    public Role update(RoleDTO dto) {
        Role existing = roleRepository.findById(dto.getId());
        if (existing == null) {
            throw new RuntimeException("角色不存在");
        }
        if (!existing.getCode().equals(dto.getCode()) && roleRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("角色编码已存在");
        }
        BeanUtil.copyProperties(dto, existing, "id", "createTime", "status");
        return roleRepository.save(existing);
    }
    
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }
    
    public Role getById(Long id) {
        return roleRepository.findById(id);
    }
    
    public List<Role> getAll() {
        return roleRepository.findAllActive();
    }
    
    public PageResult<Role> getPage(PageQuery query) {
        List<Role> all = roleRepository.findByKeyword(query.getKeyword());
        int start = (query.getPageNum() - 1) * query.getPageSize();
        int end = Math.min(start + query.getPageSize(), all.size());
        List<Role> records = all.subList(start, end);
        return PageResult.of(records, (long) all.size(), query.getPageNum(), query.getPageSize());
    }
    
    public void bindResources(Long roleId, List<Long> resourceIds) {
        Role role = roleRepository.findById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        role.setResourceIds(resourceIds);
        roleRepository.save(role);
    }
    
    public List<Role> getByIds(List<Long> ids) {
        return roleRepository.findByIds(ids);
    }
}
