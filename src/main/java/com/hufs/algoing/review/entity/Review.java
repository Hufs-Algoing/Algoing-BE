package com.hufs.algoing.review.entity;

import com.hufs.algoing.aisolved.entity.AISolved;
import com.hufs.algoing.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="review")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Todo - 제출=리뷰, 그 전 힌트 3번

    // 가독성 점수
    @Column(name="readbility")
    private Long readbility;

    // 최적화 점수
    @Column(name="optimization")
    private Long optimization;

    // 중복성 점수
    @Column(name="duplicate")
    private Long duplicate;

    @Lob
    @Column(name="read_review", length=256)
    private String readReview;

    @Lob
    @Column(name="opt_review", length=256)
    private String optReview;

    @Lob
    @Column(name="dup_review", length=256)
    private String dupReview;

    // 최종 리뷰
    @Lob
    @Column(name="summary", length=256)
    private String summary;

    @Column(name = "problem_num")
    private Long problemNum;

    @CreatedDate
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    @Column(name="code", length=256)
    private String code;

    @Column(name="language")
    private String language;





}
