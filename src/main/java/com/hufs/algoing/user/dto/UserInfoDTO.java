package com.hufs.algoing.user.dto;

import com.hufs.algoing.user.entity.Role;
import com.hufs.algoing.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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
    private String token;


    public UserInfoDTO(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole();
        this.handle = user.getHandle();
        this.bojId = user.getBojId();
        this.bio = user.getBio();
        this.picture = user.getPicture();
        this.tier = user.getTier();
        this.solvedCount = user.getSolvedCount();
        this.userPoint = user.getUserPoint();
        this.createdAt = user.getCreatedAt();
    }
}
