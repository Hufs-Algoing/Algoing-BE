package com.hufs.algoing.comment.dto;

import com.hufs.algoing.comment.entity.Comment;
import com.hufs.algoing.hint.dto.HintResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponseDTO {
    private Long commentId;
    private String content;
    private String code;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Schema(description = "채택여부")
    private Boolean isAdopted;

    public static CommentResponseDTO fromEntity(Comment comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getId())
                .code(comment.getCode())
                .content(comment.getContent())
                .userId(comment.getUser().getBojId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .isAdopted(comment.getIsAdopted())
                .build();

    }
}
