package com.datalabel.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseEntity implements Serializable {
    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;
    
    public void preInsert() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.status = 1;
    }
    
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
