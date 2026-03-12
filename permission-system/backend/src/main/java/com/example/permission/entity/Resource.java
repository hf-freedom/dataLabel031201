package com.example.permission.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Resource {

    private Long id;
    private String resourceName;
    private String resourceCode;
    private Integer type;
    private String url;
    private String method;
    private Long parentId;
    private String icon;
    private Integer sort;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    private List<Resource> children = new ArrayList<>();

    public static final Integer TYPE_MENU = 1;
    public static final Integer TYPE_PAGE = 2;
    public static final Integer TYPE_BUTTON = 3;
    public static final Integer TYPE_API = 4;

    public static final Integer STATUS_ENABLED = 1;
    public static final Integer STATUS_DISABLED = 0;
}
