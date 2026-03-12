package com.datalabel.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Resource extends BaseEntity {
    private String name;
    private String code;
    private ResourceType type;
    private Long parentId;
    private String path;
    private String icon;
    private Integer sort;
    private String apiPath;
    private String apiMethod;

    public enum ResourceType {
        MENU,
        PAGE,
        BUTTON,
        API
    }
}
