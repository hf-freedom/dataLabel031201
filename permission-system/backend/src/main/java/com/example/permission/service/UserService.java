package com.example.permission.service;

import com.example.permission.entity.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    User getUserById(Long id);

    User getUserByUsername(String username);

    List<User> getUserList();

    List<User> getUserPage(String keyword, int pageNum, int pageSize);

    long getUserCount(String keyword);

    void bindRoles(Long userId, List<Long> roleIds);

    List<Long> getUserRoleIds(Long userId);
}
