package com.hufs.algoing.recommendation.log;

import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface RecommendationLogRepository extends JpaRepository<RecommendationLog, Long> {

    // 클릭 이벤트 처리를 위해 특정 로그를 찾는 메서드
    // 가장 최근 추천된 특정 문제의 로그를 찾는 경우
    Optional<RecommendationLog> findTopByUserUserIdAndProblemProblemIdAndRecommendationSessionIdOrderByRecommendedAtDesc(
            Long userId, Long problemId, String recommendationSessionId);


}