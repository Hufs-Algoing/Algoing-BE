package com.hufs.algoing.user.entity;

import com.hufs.algoing.post.entity.Post;
import com.hufs.algoing.problem.entity.UserSolvedProblem;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "handle", unique = true, nullable = false)
    private String handle;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private String bio;

    private String profileImageUrl;

    private Integer tier;

    @Column(name = "solved_count")
    private int solvedCount;

    @Column(name = "user_point")
    private int userPoint;

    @OneToMany
    private List<Post> post;

    @OneToMany
    private List<UserSolvedProblem> userSolvedProblem;


    @Builder
    public User(String handle, String password, String email, LocalDateTime createdAt, String bio,
                String profileImageUrl, Integer tier, int solvedCount, int userPoint) {
        this.handle = handle;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.tier = tier;
        this.solvedCount = solvedCount;
        this.userPoint = userPoint;
    }

}
