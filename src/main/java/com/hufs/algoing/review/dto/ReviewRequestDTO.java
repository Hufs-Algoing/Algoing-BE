package com.hufs.algoing.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewRequestDTO {
    private Long userId;
    private Long problemNum;
    private String language;
    private String code;
}
