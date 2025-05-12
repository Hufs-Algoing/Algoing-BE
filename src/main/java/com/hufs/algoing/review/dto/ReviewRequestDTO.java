package com.hufs.algoing.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "리뷰 요청 DTO")
public class ReviewRequestDTO {

    @Schema(description = "리뷰 요청 유저 ID")
    private Long userId;

    @Schema(description = "문제 번호")
    private Long problemNum;

    @Schema(description = "작성 언어")
    private String language;

    @Schema(description = "코드")
    private String code;
}
