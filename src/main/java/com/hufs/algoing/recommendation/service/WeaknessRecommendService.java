package com.hufs.algoing.recommendation.service;

import com.hufs.algoing.aisolved.entity.AISolved;
import com.hufs.algoing.aisolved.repository.AISolvedRepository;
import com.hufs.algoing.global.code.ErrorStatus;
import com.hufs.algoing.global.exception.custom.UserNotFoundException;
import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.problem.entity.SubmittedProblem;
import com.hufs.algoing.problem.repository.ProblemRepository;
import com.hufs.algoing.problem.repository.SubmittedProblemRepository;
import com.hufs.algoing.recommendation.algorithm.WeaknessRecommendAlgorithm;
import com.hufs.algoing.recommendation.dto.WeaknessRecommendDTO;
import com.hufs.algoing.review.entity.Review;
import com.hufs.algoing.review.repository.ReviewRepository;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WeaknessRecommendService {

    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final SubmittedProblemRepository submittedProblemRepository;
    private final ReviewRepository reviewRepository;
    private final AISolvedRepository aisolvedRepository;


    public List<WeaknessRecommendDTO> getWeaknessRecommendations(Long userId) {

        //유저 정보 가져오기
        User user=userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException(ErrorStatus.USER_NOT_FOUND));
        System.out.println(user);

        //전체 문제 가져오기
        List<Problem> allProblems = problemRepository.findAll();

        //푼 문제 목록 가져오기
        List<SubmittedProblem> allSolvedProblems = submittedProblemRepository.findAll();

        //전체 리뷰 가져오기
        List<Review> allReviews = reviewRepository.findAll();

        //AI가 푼 문제 가져오기
        List<AISolved> allAiSolved =aisolvedRepository.findAll();

        // 추천 알고리즘 호출
        List<WeaknessRecommendDTO> recommendations = WeaknessRecommendAlgorithm.recommend(user, allReviews, allAiSolved, allSolvedProblems, allProblems);
        for (WeaknessRecommendDTO dto : recommendations) {
        }
        return recommendations;


    }
}