package com.hufs.algoing.recommendation.service;

import com.hufs.algoing.aisolved.entity.AISolved;
import com.hufs.algoing.aisolved.repository.AISolvedRepository;
import com.hufs.algoing.global.code.ErrorStatus;
import com.hufs.algoing.global.exception.custom.ProblemNotFoundException;
import com.hufs.algoing.global.exception.custom.RecommendationLogNotFoundException;
import com.hufs.algoing.global.exception.custom.UserNotFoundException;
import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.problem.entity.SubmittedProblem;
import com.hufs.algoing.problem.repository.ProblemRepository;
import com.hufs.algoing.problem.repository.SubmittedProblemRepository;
import com.hufs.algoing.recommendation.algorithm.DailyRecommendAlgorithm;
import com.hufs.algoing.recommendation.algorithm.IncProblemRecommendAlgorithm;
import com.hufs.algoing.recommendation.algorithm.WeaknessRecommendAlgorithm;
import com.hufs.algoing.recommendation.dto.CombinedRecommendationsDTO;
import com.hufs.algoing.recommendation.dto.DailyRecommendDTO;
import com.hufs.algoing.recommendation.dto.IncProblemRecommendDTO;
import com.hufs.algoing.recommendation.dto.WeaknessRecommendDTO;
import com.hufs.algoing.recommendation.log.RecommendationLog;
import com.hufs.algoing.recommendation.log.RecommendationLogRepository;
import com.hufs.algoing.review.entity.Review;
import com.hufs.algoing.review.repository.ReviewRepository;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {

    private final ProblemRepository problemRepository;
    private final SubmittedProblemRepository submittedProblemRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final AISolvedRepository aisolvedRepository;
    private final IncProblemRecommendAlgorithm incProblemRecommendAlgorithm;
    private final RecommendationLogRepository recommendationLogRepository;

    public List<DailyRecommendDTO> getDailyRecommendations(Long userId, String recommendationSessionId) {

        // 유저 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorStatus.USER_NOT_FOUND));

        // 유저가 푼 문제 가져오기
        List<SubmittedProblem> solvedProblems = submittedProblemRepository.findByUserId(user);

        // 전체 문제 가져오기
        List<Problem> allProblems = problemRepository.findAll();

        // 추천 알고리즘 호출
        List<DailyRecommendDTO> recommendations = DailyRecommendAlgorithm.recommend(user, allProblems, solvedProblems);

        for (DailyRecommendDTO dto : recommendations) {
        }

        // 로깅
        List<RecommendationLog> logs = recommendations.stream()
                .map(dto -> RecommendationLog.builder()
                        .recommendationSessionId(recommendationSessionId)
                        .user(user)
                        .problem(problemRepository.findById(dto.getProblemId())
                                .orElseThrow(() -> new ProblemNotFoundException(ErrorStatus.PROBLEM_NOT_FOUND)))
                        .recommendationType("Daily")
                        .recommendationScore(dto.getScore()) // 추천 점수
                        .build())
                .collect(Collectors.toList());
        recommendationLogRepository.saveAll(logs);

        return recommendations;
    }

    public List<WeaknessRecommendDTO> getWeaknessRecommendations(Long userId, String recommendationSessionId) {

        //유저 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorStatus.USER_NOT_FOUND));
        System.out.println(user);

        //전체 문제 가져오기
        List<Problem> allProblems = problemRepository.findAll();

        //푼 문제 목록 가져오기
        List<SubmittedProblem> allSolvedProblems = submittedProblemRepository.findAll();

        //전체 리뷰 가져오기
        List<Review> allReviews = reviewRepository.findAll();

        //AI가 푼 문제 가져오기
        List<AISolved> allAiSolved = aisolvedRepository.findAll();

        // 추천 알고리즘 호출
        List<WeaknessRecommendDTO> recommendations = WeaknessRecommendAlgorithm.recommend(user, allReviews, allAiSolved, allSolvedProblems, allProblems);
        for (WeaknessRecommendDTO dto : recommendations) {
        }

        // 로깅
        List<RecommendationLog> logs = recommendations.stream()
                .map(dto -> RecommendationLog.builder()
                        .recommendationSessionId(recommendationSessionId)
                        .user(user)
                        .problem(problemRepository.findById(dto.getProblemId())
                                .orElseThrow(() -> new ProblemNotFoundException(ErrorStatus.PROBLEM_NOT_FOUND)))
                        .recommendationType("Weakness")
                        .recommendationScore(dto.getFinalScore())
                        .build())
                .collect(Collectors.toList());
        recommendationLogRepository.saveAll(logs);
        return recommendations;


    }

    public List<IncProblemRecommendDTO> getIncProblemRecommendation(Long userId, String recommendationSessionId) {

        //유저 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorStatus.USER_NOT_FOUND));
        System.out.println(user);

        List<IncProblemRecommendDTO> recommendations = incProblemRecommendAlgorithm.recommend(user);

        // 로깅
        List<RecommendationLog> logs = recommendations.stream()
                .map(dto -> RecommendationLog.builder()
                        .recommendationSessionId(recommendationSessionId)
                        .user(user)
                        .problem(problemRepository.findById(dto.getProblemId())
                                .orElseThrow(() -> new ProblemNotFoundException(ErrorStatus.PROBLEM_NOT_FOUND)))
                        .recommendationType("IncProblem")
                        .recommendationScore(null)
                        .build())
                .collect(Collectors.toList());
        recommendationLogRepository.saveAll(logs);

        return recommendations;

    }

    @Transactional
    public CombinedRecommendationsDTO getAllRecommendations(Long userId) {

        String recommendationSessionId = UUID.randomUUID().toString();

        List<DailyRecommendDTO> dailyRecs = getDailyRecommendations(userId, recommendationSessionId);
        List<WeaknessRecommendDTO> weaknessRecs = getWeaknessRecommendations(userId, recommendationSessionId);
        List<IncProblemRecommendDTO> incProblemRecs = getIncProblemRecommendation(userId, recommendationSessionId);

        return new CombinedRecommendationsDTO(recommendationSessionId, dailyRecs, weaknessRecs, incProblemRecs);
    }


    @Transactional
    public void recordRecommendationClick(Long userId, Long problemId, String recommendationSessionId) {

        RecommendationLog log = recommendationLogRepository.findTopByUserUserIdAndProblemProblemIdAndRecommendationSessionIdOrderByRecommendedAtDesc(
                        userId, problemId, recommendationSessionId)
                .orElseThrow(() -> new RecommendationLogNotFoundException(ErrorStatus.RECOMMENDATION_LOG_NOT_FOUND));

        if (!log.isClicked()) {
            log.markAsClicked();
            recommendationLogRepository.save(log);
        }
    }

}
