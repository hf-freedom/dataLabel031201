package com.auth.service;

import com.auth.entity.User;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Map<Long, User> userMap = new ConcurrentHashMap<>();
    private static long idCounter = 1;

    static {
        User admin = new User();
        admin.setId(idCounter++);
        admin.setUsername("admin");
        admin.setPassword("123456");
        admin.setNickname("管理员");
        admin.setEmail("admin@example.com");
        admin.setPhone("13800138000");
        admin.setStatus(1);
        admin.setCreateTime(new Date());
        admin.setUpdateTime(new Date());
        admin.setRoleIds(Arrays.asList(1L));
        userMap.put(admin.getId(), admin);

        User test = new User();
        test.setId(idCounter++);
        test.setUsername("test");
        test.setPassword("123456");
        test.setNickname("测试用户");
        test.setEmail("test@example.com");
        test.setPhone("13800138001");
        test.setStatus(1);
        test.setCreateTime(new Date());
        test.setUpdateTime(new Date());
        test.setRoleIds(Arrays.asList(2L));
        userMap.put(test.getId(), test);
    }

    public List<User> list(String username, String nickname) {
        return userMap.values().stream()
                .filter(u -> (username == null || u.getUsername().contains(username))
                        && (nickname == null || u.getNickname().contains(nickname)))
                .collect(Collectors.toList());
    }

    public User getById(Long id) {
        return userMap.get(id);
    }

    public User getByUsername(String username) {
        return userMap.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idCounter++);
            user.setCreateTime(new Date());
        }
        user.setUpdateTime(new Date());
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        userMap.put(user.getId(), user);
        return user;
    }

    public boolean delete(Long id) {
        return userMap.remove(id) != null;
    }

    public List<User> getUsersByRoleId(Long roleId) {
        return userMap.values().stream()
                .filter(u -> u.getRoleIds() != null && u.getRoleIds().contains(roleId))
                .collect(Collectors.toList());
    }
}
