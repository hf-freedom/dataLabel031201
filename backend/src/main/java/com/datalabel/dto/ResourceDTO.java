package com.datalabel.dto;

import com.datalabel.entity.Resource;
import lombok.Data;

@Data
public class ResourceDTO {
    private Long id;
    private String name;
    private String code;
    private Resource.ResourceType type;
    private Long parentId;
    private String path;
    private String icon;
    private Integer sort;
    private String apiPath;
    private String apiMethod;
}
