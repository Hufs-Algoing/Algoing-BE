package com.hufs.algoing.submit.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecaptchaRequestDTO {

    private Long userId;
    private Long problemNum;
    private String language;
    private String code;
    private String bojId;
    private String bojPassword;
}
