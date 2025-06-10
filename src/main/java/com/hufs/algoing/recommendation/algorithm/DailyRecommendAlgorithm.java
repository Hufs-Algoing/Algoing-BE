package com.hufs.algoing.recommendation.algorithm;

import com.hufs.algoing.problem.entity.SubmittedProblem;
import com.hufs.algoing.recommendation.dto.DailyRecommendDTO;
import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.user.entity.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DailyRecommendAlgorithm {

    // 가중치 관리
    private static final double W_UserTier = 0.3;
    private static final double W_ProblemTier = 0.3;
    private static final double W_ProblemType = 0.2;
    private static final double W_UserSolvedType = 0.2;

    private static final int MAX_TIER_DIFF = 1; // 티어 차이 최대 +1까지

    // 유저 정보(user테이블) + 유저가 푼 문제(user solved problem테이블) + 문제(problem테이블) 리스트 받아서 추천리스트 만듦.
    public static List<DailyRecommendDTO> recommend(User user, List<Problem> problems, List<SubmittedProblem> solvedProblems) {

        // 유저가 푼 문제 번호 -> USER_SOLVED_PROBLEM 테이블 Problem_num값 가져오기
        List<Long> solvedProblemIds = solvedProblems.stream()
                .filter(solvedProblem -> {
                    boolean isSolved = "SOLVED".equalsIgnoreCase(String.valueOf(solvedProblem.getStatus()));
                    boolean isMatchingUser = solvedProblem.getUserId().getUserId().equals(user.getUserId()); //solvedProblem이 user객체 참조 -> 다시 userId조회
                    return isMatchingUser&& isSolved;
                }) // 해당 유저가 푼 문제만 필터링
                .map(solvedProblem -> {
                    return solvedProblem.getProblemId().getProblemId(); // 푼 문제의 문제번호만 추출
                })
                .collect(Collectors.toList());  // 리스트로 변환

        //리뷰가 7개 이하면 해당 유저 티어 +-2차이의 문제 추천
        if(solvedProblemIds==null || solvedProblemIds.size()==0 ||solvedProblemIds.size() <= 7){
            int userLevel = user.getTier(); // 유저 티어

            Set<Long> userSolvedproblem = solvedProblems.stream()
                    .filter(p -> "solved".equalsIgnoreCase(String.valueOf(p.getStatus())))
                    .map(p -> p.getProblemId().getProblemId())
                    .collect(Collectors.toSet());

            List<Problem> randomProblem = problems.stream()
                    .filter(p -> Math.abs(p.getLevel() - userLevel) <= 2)
                    .filter(p -> !userSolvedproblem.contains(p.getProblemId()))
                    .limit(3)
                    .collect(Collectors.toList());

            return randomProblem.stream()
                    .map(p -> new DailyRecommendDTO(
                            p.getProblemId(),
                            p.getTitle(),
                            Math.toIntExact(p.getLevel()),
                            p.getTagNames(),
                            0.0 // 기본 점수 없음
                    ))
                    .collect(Collectors.toList());
        }

        // 유저가 푼 문제 유형(tag)
        List<String> userProblemTags = calculateUserProblemTags(user, solvedProblems, problems);

        // 문제 추천 점수 계산
        List<DailyRecommendDTO> recommendations = problems.stream()
                .filter(problem -> !solvedProblemIds.contains(problem.getProblemId())) // 유저가 푼 문제 제외
                .map(problem -> {
                    // 각 문제 태그 추출
                    String problemTag = problem.getTagNames();
                    double typeSimilarity = calculateCosineSimilarity(userProblemTags, problemTag); // 유사도 계산

                    // 점수 계산
                    double score = calculateScore(user, problem, userProblemTags, typeSimilarity);

                    // dailyRecommend 객체 생성(문제 번호와 점수)
                    return new DailyRecommendDTO(
                            problem.getProblemId(),
                            problem.getTitle(),
                            Math.toIntExact(problem.getLevel()),
                            problem.getTagNames(),
                            score
                    );
                })
                .collect(Collectors.toList());

        // 점수 기준 정렬
        recommendations.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        // 상위 3개 문제만 추천
        int recommendCount = Math.min(3, recommendations.size());
        return recommendations.subList(0, recommendCount); // 상위 3개만 잘라서 반환
    }

    // 유저가 푼 문제 유형 리스트로 반환
    private static List<String> calculateUserProblemTags(User user, List<SubmittedProblem> solvedProblems, List<Problem> problems) {
        return solvedProblems.stream()
                .filter(solvedProblem -> solvedProblem.getUserId().equals(user))
                .map(solvedProblem -> solvedProblem.getProblemId().getTagNames())
                .filter(tagStr -> tagStr != null && !tagStr.isEmpty())
                .flatMap(tagStr -> List.of(tagStr.split(",")).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    // 유저가 푼 태그와 문제의 태그의 코사인 유사도 계산
    private static double calculateCosineSimilarity(List<String> userTags, String problemTag) {
        if (problemTag == null || problemTag.isEmpty()) {
            return 0.0;
        }

        // 문제 태그를 ',' 기준으로 분리
        String[] problemTags = problemTag.split(",");

        // 유저 태그와 문제 태그의 합집합을 만들어서, 각 태그가 포함되었는지 체크
        List<String> allTags = userTags.stream()
                .distinct() // 중복 제거
                .collect(Collectors.toList());
        for (String tag : problemTags) {
            if (!allTags.contains(tag)) {
                allTags.add(tag);
            }
        }

        // 유저 태그와 문제 태그의 벡터 만들기
        int[] userVector = new int[allTags.size()];
        int[] problemVector = new int[allTags.size()];

        for (int i = 0; i < allTags.size(); i++) {
            if (userTags.contains(allTags.get(i))) {
                userVector[i] = 1;
            }
            if (List.of(problemTags).contains(allTags.get(i))) {
                problemVector[i] = 1;
            }
        }

        // 코사인 유사도 계산
        //공통 태그 수 기반
        int dotProduct = 0;
        double userNorm = 0;
        double problemNorm = 0;

        for (int j = 0; j < allTags.size(); j++) {
            dotProduct += userVector[j] * problemVector[j];
            userNorm += userVector[j] * userVector[j];
            problemNorm += problemVector[j] * problemVector[j];
        }

        if (userNorm == 0 || problemNorm == 0) {
            return 0.0; // 벡터 크기가 0이면 유사도 0
        }

        return dotProduct / (Math.sqrt(userNorm) * Math.sqrt(problemNorm));
    }

    private static double calculateScore(User user, Problem problem, List<String> userProblemTags, double typeSimilarity) {
        // 유저와 문제의 티어 차이
        double tierDiff = Math.abs(user.getTier() - problem.getLevel());

        // 유저와 문제의 티어 차이를 고려한 점수 부여
        double tierScore = (tierDiff <= MAX_TIER_DIFF) ? (1.0 - (tierDiff * 0.5)) : 0.0;

        // 문제 유형 유사도 기반 점수
        double typeScore = typeSimilarity * W_ProblemType;

        // 유저 문제 유형과 문제의 태그 유사도를 기반으로 점수 계산
        double solvedTypeScore = typeSimilarity * W_UserSolvedType;

        // 최종 점수 계산
        double totalScore = (W_UserTier * user.getTier() +
                W_ProblemTier * problem.getLevel() +
                typeScore +
                solvedTypeScore +
                tierScore);

        return  Math.round(totalScore * 100.0) / 100.0; //소수점 둘째 자리 까지만

    }
}