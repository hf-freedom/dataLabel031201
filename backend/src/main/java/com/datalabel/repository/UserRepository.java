package com.datalabel.repository;

import com.datalabel.entity.User;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserRepository extends BaseRepository<User> {
    
    public User findByUsername(String username) {
        return dataMap.values().stream()
                .filter(u -> u.getUsername().equals(username) && u.getStatus() == 1)
                .findFirst()
                .orElse(null);
    }
    
    public List<User> findByKeyword(String keyword) {
        return dataMap.values().stream()
                .filter(u -> u.getStatus() == 1)
                .filter(u -> keyword == null || keyword.isEmpty() || 
                        u.getUsername().contains(keyword) || 
                        u.getNickname().contains(keyword))
                .collect(Collectors.toList());
    }
    
    public List<User> findByRoleId(Long roleId) {
        return dataMap.values().stream()
                .filter(u -> u.getStatus() == 1 && u.getRoleIds() != null && u.getRoleIds().contains(roleId))
                .collect(Collectors.toList());
    }
    
    public boolean existsByUsername(String username) {
        return dataMap.values().stream()
                .anyMatch(u -> u.getUsername().equals(username) && u.getStatus() == 1);
    }
}
