package com.hufs.algoing.post.dto;

import lombok.Data;

@Data
public class PostRequestDTO {
    private String title;
    private String content;
//    private Long submittedProblemId; // 문제 정보
}
