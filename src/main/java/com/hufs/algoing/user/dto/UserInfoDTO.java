package com.hufs.algoing.user.dto;

import com.hufs.algoing.user.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserInfoDTO {
    private Long userId;
    private String email;
    private String name;
    private Role role;
    private String handle;
    private String bojId;
    private String bio;
    private String picture;
    private Integer tier;
    private int solvedCount;
    private int userPoint;
    private LocalDateTime createdAt;
}
