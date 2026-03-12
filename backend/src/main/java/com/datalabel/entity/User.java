package com.datalabel.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private List<Long> roleIds;
}
