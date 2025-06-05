package com.hufs.algoing.recommendation.log;

import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Entity
@Table(name = "recommendation_log")
public class RecommendationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recommendationSessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 추천을 받은 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem; // 추천된 문제

    @Column(name="recommendation_type", nullable = false)
    private String recommendationType; // Daily, Weakness, IncProblem

    @Column(name = "recommendation_score")
    private Double recommendationScore; // 추천 당시 점수

    @Column(name="recommended_at", nullable = false)
    private LocalDateTime recommendedAt; // 추천 생성 시각

    @Column(name="clicked_at")
    private LocalDateTime clickedAt; // 사용자가 이 추천을 클릭한 시각

    @Column(name="is_clicked", nullable = false)
    private boolean isClicked; // 클릭 여부

    @Builder
    public RecommendationLog(String recommendationSessionId, User user, Problem problem,
                             String recommendationType, Double recommendationScore) {
        this.recommendationSessionId = recommendationSessionId;
        this.user = user;
        this.problem = problem;
        this.recommendationType = recommendationType;
        this.recommendationScore = recommendationScore;
        this.recommendedAt = LocalDateTime.now();
        this.isClicked = false;
    }

    // 추천 클릭 시 호출
    public void markAsClicked() {
        this.isClicked = true;
        this.clickedAt = LocalDateTime.now();
    }

}
