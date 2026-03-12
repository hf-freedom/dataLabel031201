package com.example.permission.service;

import com.example.permission.entity.Resource;

import java.util.List;

public interface ResourceService {

    Resource createResource(Resource resource);

    Resource updateResource(Resource resource);

    void deleteResource(Long id);

    Resource getResourceById(Long id);

    List<Resource> getResourceList();

    List<Resource> getResourceByType(Integer type);

    List<Resource> getResourceTree();

    List<Resource> getResourcePage(String keyword, Integer type, int pageNum, int pageSize);

    long getResourceCount(String keyword, Integer type);

    void enableResource(Long id);

    void disableResource(Long id);

    List<Resource> getUserMenus(Long userId);

    List<String> getUserPermissions(Long userId);

    boolean hasPermission(Long userId, String url, String method);
}
