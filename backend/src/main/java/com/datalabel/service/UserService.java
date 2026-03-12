package com.datalabel.service;

import cn.hutool.core.bean.BeanUtil;
import com.datalabel.common.PageResult;
import com.datalabel.dto.PageQuery;
import com.datalabel.dto.UserDTO;
import com.datalabel.entity.User;
import com.datalabel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User create(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        BeanUtil.copyProperties(dto, user, "id");
        return userRepository.save(user);
    }
    
    public User update(UserDTO dto) {
        User existing = userRepository.findById(dto.getId());
        if (existing == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!existing.getUsername().equals(dto.getUsername()) && userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        BeanUtil.copyProperties(dto, existing, "id", "createTime", "status");
        return userRepository.save(existing);
    }
    
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
    
    public User getById(Long id) {
        return userRepository.findById(id);
    }
    
    public User getByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public List<User> getAll() {
        return userRepository.findAllActive();
    }
    
    public PageResult<User> getPage(PageQuery query) {
        List<User> all = userRepository.findByKeyword(query.getKeyword());
        int start = (query.getPageNum() - 1) * query.getPageSize();
        int end = Math.min(start + query.getPageSize(), all.size());
        List<User> records = all.subList(start, end);
        return PageResult.of(records, (long) all.size(), query.getPageNum(), query.getPageSize());
    }
    
    public void bindRoles(Long userId, List<Long> roleIds) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setRoleIds(roleIds);
        userRepository.save(user);
    }
}
