package com.hufs.algoing.comment.controller;

import com.hufs.algoing.comment.dto.CommentInfoDTO;
import com.hufs.algoing.comment.dto.CommentRequestDTO;
import com.hufs.algoing.comment.dto.CommentResponseDTO;
import com.hufs.algoing.comment.service.CommentService;
import com.hufs.algoing.global.code.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Comment API", description = "댓글 API")
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "게시글 댓글 목록 조회", description = "특정 게시글(postId)에 달린 모든 댓글 목록을 조회합니다.")
    @GetMapping("/posts/{postId}/comments")
    @Parameter(description = "댓글 조회하는 게시글 ID", example = "3")
    public ApiResponse<List<CommentResponseDTO>> getCommentsByPost(@PathVariable Long postId) {
        List<CommentResponseDTO> comments = commentService.getCommentsByPostId(postId);
        return ApiResponse.onSuccess(comments);
    }

    @Operation(summary = "댓글 작성", description = "특정 게시글에 댓글을 작성합니다")
    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @Parameter(description = "댓글 작성하는 게시글 ID", example = "3")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "댓글 작성자 ID, 댓글 내용, 필요하다면 코드도 들어갑니다.",
            required = true,
            content = @Content(schema = @Schema(implementation = CommentRequestDTO.class))
    )
    public ApiResponse<CommentResponseDTO> createComment(@PathVariable Long postId, @RequestBody @Valid CommentRequestDTO commentRequestDTO) {

        CommentResponseDTO comment = commentService.createComment(postId, commentRequestDTO);
        return ApiResponse.onSuccess(comment);
    }

    @Operation(summary = "댓글 채택", description = "게시글 작성자가 자신의 게시글에 달린 댓글을 채택합니다, Response isAdopted true시 채택, 채택 받은 유저 포인트 10 증가" )
    @PatchMapping("/comments/{commentId}/adopt")
    @Parameter(description = "채택할 댓글의 ID", example = "6")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "게시글 작성자 정보를 담은 요청 객체 (로그인 사용자)",
            required = true,
            content = @Content(schema = @Schema(implementation = CommentInfoDTO.class))
    )
    public ApiResponse<CommentResponseDTO> adoptComment(@PathVariable Long commentId, @RequestBody CommentInfoDTO commentInfoDTO) {
        CommentResponseDTO adoptedComment = commentService.adoptComment(commentId, commentInfoDTO.getUserId());
        return ApiResponse.onSuccess(adoptedComment);
    }

    @Operation(summary = "댓글 삭제", description = "특정 ID의 댓글을 삭제합니다. 작성자만 삭제할 수 있습니다.")
    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Parameter(name = "commentId", description = "삭제할 댓글 ID", example = "7")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "댓글 작성자 정보를 담은 요청 객체 (로그인 사용자)",
            required = true,
            content = @Content(schema = @Schema(implementation = CommentInfoDTO.class))
    )
    public ApiResponse<Void> deleteComment(@PathVariable Long commentId, @RequestBody CommentInfoDTO commentInfoDTO) {
        commentService.deleteComment(commentId, commentInfoDTO.getUserId());
        return ApiResponse.onSuccess(null);
    }




}
