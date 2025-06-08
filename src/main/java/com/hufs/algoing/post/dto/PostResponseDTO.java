package com.hufs.algoing.post.dto;

import com.hufs.algoing.post.entity.Post;
import com.hufs.algoing.problem.dto.SubmittedProblemDTO;
import com.hufs.algoing.user.dto.UserInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class PostResponseDTO {

    private Long postId;
    private Long userId;
    private String title;
    private String content;
    private String language;
    private UserInfoDTO userInfo; // 글쓴이 정보
    private SubmittedProblemDTO submittedProblem; // 제출된 문제 정보


    // Post 엔티티를 받아서 DTO로 변환하는 생성자
    public PostResponseDTO(Post post) {
        this.postId = post.getPostId();
        this.userId = post.getUser().getUserId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.language = post.getLanguage();
        this.userInfo = new UserInfoDTO(post.getUser());
        this.submittedProblem = new SubmittedProblemDTO(post.getSubmittedProblem());
    }
}
