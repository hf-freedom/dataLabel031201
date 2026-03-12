package com.auth.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Role {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private List<Long> resourceIds;
    private List<Long> userIds;
}
