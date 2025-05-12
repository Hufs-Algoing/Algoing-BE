package com.hufs.algoing.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "리뷰 응답 DTO")
public class ReviewResponseDTO {

    @Schema(description = "최종 리뷰")
    private String summary;
}
