package com.hufs.algoing.hint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "힌트 응답 DTO")
public class HintResponseDTO {

    @Schema(description = "힌트 내용")
    private String content;
    @Schema(description = "요청 힌트 순서")
    private int order;

    public static HintResponseDTO fromEntity(String content, int order) {
        return HintResponseDTO.builder().content(content).order(order).build();
    }
}
