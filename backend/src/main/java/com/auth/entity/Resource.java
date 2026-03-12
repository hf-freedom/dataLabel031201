package com.auth.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Resource {
    private Long id;
    private String name;
    private String code;
    private String type;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private Long parentId;
    private Integer status;
    private String permission;
    private Date createTime;
    private Date updateTime;
    private List<Resource> children;
}
