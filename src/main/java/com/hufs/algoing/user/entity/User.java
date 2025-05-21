package com.hufs.algoing.user.entity;

import com.hufs.algoing.problem.entity.SubmittedProblem;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    // Solved.ac에서 사용하는 handle입니다.
    @Column(name = "handle", unique = true)
    private String handle;

    // 알고잉에서 사용하는 닉네임입니다.
    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    //여기까지 회원가입 시 생성됨

    private String bio;

    private String profileImageUrl;

    private Integer tier;

    @Column(name = "solved_count")
    private int solvedCount;

    @Column(name = "user_point")
    private int userPoint;

    @OneToMany(mappedBy = "userId")
    private List<SubmittedProblem> submittedProblem;


    @Builder
    public User(String handle, String password, String email, String nickname) {
        this.handle = handle;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.createdAt = LocalDateTime.now();
    }

    //UserDetails 등에서 Username은 이메일입니다!!!!!
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }


    public void updatePoint(int point) {
        this.userPoint = point;
    }


}
