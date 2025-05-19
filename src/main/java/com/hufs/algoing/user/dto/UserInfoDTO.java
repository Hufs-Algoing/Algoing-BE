package com.hufs.algoing.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserInfoDTO {
    private Long userId;
    private String handle;
    private String email;
    private String nickname;
    private String bio;
    private String profileImageUrl;
    private Integer tier;
    private int solvedCount;
    private int userPoint;
    private LocalDateTime createdAt;
}
