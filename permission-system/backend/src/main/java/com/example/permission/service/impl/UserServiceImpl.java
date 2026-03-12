package com.example.permission.service.impl;

import com.example.permission.entity.User;
import com.example.permission.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final Map<Long, User> userStore = new ConcurrentHashMap<>();
    private final Map<Long, List<Long>> userRoleStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @PostConstruct
    public void init() {
        User admin = new User();
        admin.setId(idGenerator.getAndIncrement());
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setNickname("管理员");
        admin.setEmail("admin@example.com");
        admin.setPhone("13800138000");
        admin.setStatus(User.STATUS_ENABLED);
        admin.setCreateTime(new Date());
        admin.setUpdateTime(new Date());
        userStore.put(admin.getId(), admin);
    }

    @Override
    public User createUser(User user) {
        user.setId(idGenerator.getAndIncrement());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userStore.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userStore.get(user.getId());
        if (existingUser == null) {
            return null;
        }
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getNickname() != null) {
            existingUser.setNickname(user.getNickname());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }
        if (user.getStatus() != null) {
            existingUser.setStatus(user.getStatus());
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        existingUser.setUpdateTime(new Date());
        return existingUser;
    }

    @Override
    public void deleteUser(Long id) {
        userStore.remove(id);
        userRoleStore.remove(id);
    }

    @Override
    public User getUserById(Long id) {
        User user = userStore.get(id);
        if (user != null) {
            user.setRoleIds(getUserRoleIds(id));
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        return userStore.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> getUserList() {
        return new ArrayList<>(userStore.values());
    }

    @Override
    public List<User> getUserPage(String keyword, int pageNum, int pageSize) {
        return userStore.values().stream()
                .filter(u -> keyword == null || keyword.isEmpty()
                        || u.getUsername().contains(keyword)
                        || u.getNickname().contains(keyword))
                .skip((long) (pageNum - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public long getUserCount(String keyword) {
        return userStore.values().stream()
                .filter(u -> keyword == null || keyword.isEmpty()
                        || u.getUsername().contains(keyword)
                        || u.getNickname().contains(keyword))
                .count();
    }

    @Override
    public void bindRoles(Long userId, List<Long> roleIds) {
        userRoleStore.put(userId, new ArrayList<>(roleIds));
        User user = userStore.get(userId);
        if (user != null) {
            user.setRoleIds(new ArrayList<>(roleIds));
        }
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleStore.getOrDefault(userId, new ArrayList<>());
    }
}
