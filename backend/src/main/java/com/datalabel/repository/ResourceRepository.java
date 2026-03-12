package com.datalabel.repository;

import com.datalabel.entity.Resource;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ResourceRepository extends BaseRepository<Resource> {
    
    public Resource findByCode(String code) {
        return dataMap.values().stream()
                .filter(r -> r.getCode().equals(code) && r.getStatus() == 1)
                .findFirst()
                .orElse(null);
    }
    
    public List<Resource> findByType(Resource.ResourceType type) {
        return dataMap.values().stream()
                .filter(r -> r.getStatus() == 1 && r.getType() == type)
                .collect(Collectors.toList());
    }
    
    public List<Resource> findByParentId(Long parentId) {
        return dataMap.values().stream()
                .filter(r -> r.getStatus() == 1)
                .filter(r -> (parentId == null && r.getParentId() == null) || 
                        (parentId != null && parentId.equals(r.getParentId())))
                .collect(Collectors.toList());
    }
    
    public List<Resource> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return dataMap.values().stream()
                .filter(r -> r.getStatus() == 1 && ids.contains(r.getId()))
                .collect(Collectors.toList());
    }
    
    public List<Resource> findByKeyword(String keyword) {
        return dataMap.values().stream()
                .filter(r -> r.getStatus() == 1)
                .filter(r -> keyword == null || keyword.isEmpty() || 
                        r.getName().contains(keyword) || 
                        r.getCode().contains(keyword))
                .collect(Collectors.toList());
    }
    
    public List<Resource> findByApiPathAndMethod(String apiPath, String apiMethod) {
        return dataMap.values().stream()
                .filter(r -> r.getStatus() == 1 && r.getType() == Resource.ResourceType.API)
                .filter(r -> apiPath.equals(r.getApiPath()) && apiMethod.equalsIgnoreCase(r.getApiMethod()))
                .collect(Collectors.toList());
    }
    
    public List<Resource> findMenus() {
        return dataMap.values().stream()
                .filter(r -> r.getStatus() == 1)
                .filter(r -> r.getType() == Resource.ResourceType.MENU || r.getType() == Resource.ResourceType.PAGE)
                .sorted((a, b) -> (a.getSort() != null ? a.getSort() : 0) - (b.getSort() != null ? b.getSort() : 0))
                .collect(Collectors.toList());
    }
    
    public boolean existsByCode(String code) {
        return dataMap.values().stream()
                .anyMatch(r -> r.getCode().equals(code) && r.getStatus() == 1);
    }
}
