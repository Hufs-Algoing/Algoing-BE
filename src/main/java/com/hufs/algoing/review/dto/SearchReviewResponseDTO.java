package com.hufs.algoing.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class SearchReviewResponseDTO {

    private Long id;
    private Long userId;
    private Long problemNum;
    private String summary;
    private LocalDateTime createdAt;
    private String code;
    private String language;

}
