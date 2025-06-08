package com.hufs.algoing.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDTO {
    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 500, message = "댓글 내용은 500자를 초과할 수 없습니다.")
    @Schema(description = "댓글 내용", example = "이렇게 코드를 고쳐보세요.", required = true)
    private String content;

    @Schema(description = "코드가 들어갈 수 있습니다.")
    private String code;

    @Schema(description = "댓글을 작성 사용자 ID", example = "1", required = true)
    private Long userId;
}
