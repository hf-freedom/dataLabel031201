package com.auth.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private List<Long> roleIds;
}
