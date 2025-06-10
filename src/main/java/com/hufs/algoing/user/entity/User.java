package com.hufs.algoing.user.entity;

import com.hufs.algoing.problem.entity.SubmittedProblem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email;

    //Google OAuth2.0에서 제공하는 이름
    //provider + providerId
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    //백준 아이디 비밀번호
    //비밀번호는 암호화되어 저장, 사용할때 복호화 해야함
    @Column(unique = true)
    private String bojId;
    private String bojPassword;

    private String nickname;

    @CreatedDate
    @NotNull
    private LocalDateTime createdAt;

    private String bio;
    private String picture;

    private Integer tier;

    private int solvedCount;
    private int userPoint;

    @OneToMany(mappedBy = "userId")
    private List<SubmittedProblem> submittedProblem;

    private String provider;
    private String providerId;

    @Getter
    @Setter
    private String token;

    @Builder
    public User
            (String name, String email, String picture, Role role,
             String provider, String providerId, String nickname) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.createdAt = LocalDateTime.now();
        this.nickname = nickname;
    }

    public User update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return this;
    }

    public String getRoleKey() {
        return this.role.getValue();
    }

    public void updatePoint(int point) {
        this.userPoint = point;
    }


}
