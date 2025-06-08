package com.hufs.algoing.post.entity;

import com.hufs.algoing.problem.entity.SubmittedProblem;
import com.hufs.algoing.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false, unique = true)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 글쓴이 정보

    @NotNull
    private String title;

    @NotNull
    private String content;

    private String language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_problem_id")
    private SubmittedProblem submittedProblem;

    @Builder
    public Post(User user, String title, String content, String language, SubmittedProblem submittedProblem) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.language = language;
        this.submittedProblem = submittedProblem;
    }
}
