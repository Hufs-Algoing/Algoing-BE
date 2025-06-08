package com.hufs.algoing.post.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.global.code.ErrorStatus;
import com.hufs.algoing.global.exception.custom.UserNotFoundException;
import com.hufs.algoing.global.oauth.PrincipalDetails;
import com.hufs.algoing.post.dto.PostRequestDTO;
import com.hufs.algoing.post.dto.PostResponseDTO;
import com.hufs.algoing.post.entity.Post;
import com.hufs.algoing.post.repository.PostRepository;
import com.hufs.algoing.post.service.PostService;
import com.hufs.algoing.problem.dto.SubmittedProblemDTO;
import com.hufs.algoing.user.dto.UserInfoDTO;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Post API", description = "게시글 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final UserService userService;
    private final PostRepository postRepository;
    private final PostService postService;

    @Operation(summary = "게시글 조회", description = "게시글 ID를 기반으로 게시글을 조회합니다.")
    @GetMapping("/{postId}")
    public ApiResponse<PostResponseDTO> getPost(@PathVariable Long postId) {
        Post post = postRepository.findByPostId(postId);
        User user = post.getUser();
        UserInfoDTO userInfoDTO = userService.toUserInfoDTO(user);
        SubmittedProblemDTO submittedProblemDTO = post.getSubmittedProblem() != null
                ? post.getSubmittedProblem().toSubmittedProblemDTO()
                : null;

        PostResponseDTO postResponseDTO = new PostResponseDTO(
                post.getPostId(),
                post.getUser().getUserId(),
                post.getTitle(),
                post.getContent(),
                post.getLanguage(),
                userInfoDTO,
                submittedProblemDTO
        );
        return ApiResponse.onSuccess(postResponseDTO);
    }

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @PostMapping("/{submittedProblemId}/write")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostResponseDTO> createPost(@PathVariable Long submittedProblemId, @RequestBody PostRequestDTO postRequestDTO, @AuthenticationPrincipal PrincipalDetails p) {
        if(p == null) {
            throw new UserNotFoundException(ErrorStatus.USER_NOT_AUTHORIZED);
        }
        return ApiResponse.onSuccess(
                postService.createPost(postRequestDTO, p.getUser(), submittedProblemId)
        );
    }

}
