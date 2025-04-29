package com.hufs.algoing.recommendation.service;

import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.problem.repository.ProblemRepository;
import com.hufs.algoing.problem.entity.UserSolvedProblem;
import com.hufs.algoing.problem.repository.UserSolvedProblemRepository;
import com.hufs.algoing.recommendation.dto.DailyRecommendDTO;
import com.hufs.algoing.recommendation.algorithm.DailyRecommenderAlgorithm;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyRecommendService {

    private final ProblemRepository problemRepository;
    private final UserSolvedProblemRepository userSolvedProblemRepository;
    private final UserRepository userRepository;

    public List<DailyRecommendDTO> getDailyRecommendations(Long userId) {

        // 유저 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저정보를 찾을 수 없습니다: " + userId));

        // 유저가 푼 문제 가져오기
        List<UserSolvedProblem> solvedProblems = userSolvedProblemRepository.findByUserId(user);

        // 전체 문제 가져오기
        List<Problem> allProblems = problemRepository.findAll();

        // 추천 알고리즘 호출
        List<DailyRecommendDTO> recommendations = DailyRecommenderAlgorithm.recommend(user, allProblems, solvedProblems);
        
        for (DailyRecommendDTO dto : recommendations) {
        }
        return recommendations;
    }
}
