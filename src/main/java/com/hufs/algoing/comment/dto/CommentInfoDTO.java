package com.hufs.algoing.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "로그인 사용자 정보 DTO")
public class CommentInfoDTO {
    @Schema(description = "로그인 사용자 ID, 채택 시에는 게시글 작성자의 ID / 댓글 삭제 시에는 댓글 작성자 ID", example = "3")
    private Long userId;
}
