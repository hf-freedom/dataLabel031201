package com.datalabel.repository;

import com.datalabel.entity.Role;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RoleRepository extends BaseRepository<Role> {
    
    public Role findByCode(String code) {
        return dataMap.values().stream()
                .filter(r -> r.getCode().equals(code) && r.getStatus() == 1)
                .findFirst()
                .orElse(null);
    }
    
    public List<Role> findByKeyword(String keyword) {
        return dataMap.values().stream()
                .filter(r -> r.getStatus() == 1)
                .filter(r -> keyword == null || keyword.isEmpty() || 
                        r.getName().contains(keyword) || 
                        r.getCode().contains(keyword))
                .collect(Collectors.toList());
    }
    
    public List<Role> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return dataMap.values().stream()
                .filter(r -> r.getStatus() == 1 && ids.contains(r.getId()))
                .collect(Collectors.toList());
    }
    
    public boolean existsByCode(String code) {
        return dataMap.values().stream()
                .anyMatch(r -> r.getCode().equals(code) && r.getStatus() == 1);
    }
}
