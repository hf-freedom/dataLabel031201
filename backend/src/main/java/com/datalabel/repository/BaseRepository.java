package com.datalabel.repository;

import com.datalabel.entity.BaseEntity;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public abstract class BaseRepository<T extends BaseEntity> {
    protected final Map<Long, T> dataMap = new ConcurrentHashMap<>();
    protected final AtomicLong idGenerator = new AtomicLong(1);
    
    public T save(T entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
            entity.preInsert();
        } else {
            entity.preUpdate();
        }
        dataMap.put(entity.getId(), entity);
        return entity;
    }
    
    public T findById(Long id) {
        return dataMap.get(id);
    }
    
    public List<T> findAll() {
        return new ArrayList<>(dataMap.values());
    }
    
    public List<T> findAllActive() {
        return dataMap.values().stream()
                .filter(e -> e.getStatus() != null && e.getStatus() == 1)
                .collect(Collectors.toList());
    }
    
    public void deleteById(Long id) {
        T entity = dataMap.get(id);
        if (entity != null) {
            entity.setStatus(0);
            entity.preUpdate();
        }
    }
    
    public void hardDeleteById(Long id) {
        dataMap.remove(id);
    }
    
    public boolean existsById(Long id) {
        return dataMap.containsKey(id);
    }
    
    public long count() {
        return dataMap.size();
    }
}
