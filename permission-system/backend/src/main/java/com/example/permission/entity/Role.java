package com.example.permission.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Role {

    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    private List<Long> resourceIds = new ArrayList<>();

    public static final Integer STATUS_ENABLED = 1;
    public static final Integer STATUS_DISABLED = 0;
}
