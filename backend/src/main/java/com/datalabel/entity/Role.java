package com.datalabel.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {
    private String name;
    private String code;
    private String description;
    private List<Long> resourceIds;
}
