package com.hufs.algoing.recommendation.algorithm;

import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.problem.entity.SubmittedProblem;
import com.hufs.algoing.recommendation.dto.WeaknessRecommendDTO;
import com.hufs.algoing.review.entity.Review;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.aisolved.entity.AISolved;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WeaknessRecommendAlgorithm {

    //추천에 관여하는 요소
    //1.가독성(함수명, 변수명, 코드 구조) 점수
    //2.최적화 (시간 복잡도와 공간 복잡도) 점수
    //3.코드 중복성(함수화 또는 리팩토링을 통해 중복을 줄일 수 있는 방법) 점수
    //7.문제유형
    private static final int MAX_WEAKNESS=15; //점수 차이 15점 이내 문제 추천

    public static List<WeaknessRecommendDTO> recommend(User user, List<Review> userReviewProblem, List<AISolved> aisolvedProblem, List<SubmittedProblem> submittedProblems, List<Problem> problems) {

        //유저 id와 일치하는 review 가져오기
        List<Review> userReview = userReviewProblem.stream()
                .filter(review -> review.getUser().getUserId().equals(user.getUserId()))
                .collect(Collectors.toList());

        System.out.println(userReview);

        //리뷰가 7개 이하면 해당 유저 티어 +-2차이의 문제 추천
       if(userReview==null || userReview.size()==0 || userReview.size() <= 7){
           int userLevel = user.getTier(); // 유저 티어
           List<Problem> randomProblem = problems.stream()
                   .filter(p -> Math.abs(p.getLevel() - userLevel) <= 2) // 유저 티어 ±2
                   .filter(p -> !submittedProblems.contains(p.getProblemId())) // 이미 푼 문제 제외
                   .limit(3) // 3개 추천
                   .collect(Collectors.toList());

           return randomProblem.stream()
                   .map(p -> new WeaknessRecommendDTO(
                           p.getProblemId(),
                           p.getTitle(),
                           p.getTag(),
                           p.getLevel(),
                           0.0 // 기본 점수 없음
                   ))
                   .collect(Collectors.toList());
       }

        //약점 분석-각 점수 평균
        //유저 가독성 평균 계산
        double averageUserRead=userReview.stream()
                .mapToLong(Review::getReadbility)
                .average()
                .orElse(0);

        //유저 최적화 평균 계산
        double averageUserOptimize=userReview.stream()
                .mapToLong(Review::getOptimization)
                .average()
                .orElse(0);

        //유저 중복성 평균 계산
        double averageUserduplicate=userReview.stream()
                .mapToLong(Review::getDuplicate)
                .average()
                .orElse(0);

        //가장 낮은(=취약점) 점수 선택
        //key-value 매핑
        Map<String, Double> userScoreMap = new HashMap<>();
        userScoreMap.put("averageUserRead", new BigDecimal(averageUserRead).setScale(2, RoundingMode.HALF_UP).doubleValue());
        userScoreMap.put("averageUserOptimize", new BigDecimal(averageUserOptimize).setScale(2, RoundingMode.HALF_UP).doubleValue());
        userScoreMap.put("averageUserduplicate", new BigDecimal(averageUserduplicate).setScale(2, RoundingMode.HALF_UP).doubleValue());

        String userWeakness=userScoreMap.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("averageUserRead"); //기본값= 중복성 점수

        //유저 약점과 유사한 문제 리스트 필터링
        List<AISolved> aiSolvedList=aisolvedProblem.stream()
                .filter(p-> {
                    switch (userWeakness){
                        case "averageUserRead": //userWeakness=readbility
                            return p.getReadLevel() > averageUserRead &&
                                    p.getReadLevel() <=averageUserRead+MAX_WEAKNESS;//문제 점수가 유저 점수보다 높아야하고, 문제가 문제점수+15보다 작아야 함
                        case "averageUserOptimize":     //&&유저 약점 이외의 항목(!userWeakness) 보다 문제 점수가 높게 설정할 수도 있음
                            return p.getOptLevel()>averageUserOptimize &&
                                    p.getOptLevel()<=averageUserOptimize+MAX_WEAKNESS;
                        case "averageUserduplicate":
                            return p.getDupLevel()>averageUserduplicate &&
                                    p.getDupLevel()<=averageUserduplicate+MAX_WEAKNESS;
                        default:
                            return false;
                    }
                })
                .collect(Collectors.toList());

        //유형 선호도 가중치 계산
        //유저가 푼 유형 갯수(ex: greedy,20)
        Map<String,Long> typeCount= submittedProblems.stream()
                .filter(usp->usp.getUserId().getUserId().equals(user.getUserId()))
                .map(solvedProblem -> solvedProblem.getProblemId().getTag())
                .filter(tagStr -> tagStr != null && !tagStr.isEmpty())
                .flatMap(tagStr -> Arrays.stream(tagStr.split(",")))
                .flatMap(tagStr -> Arrays.stream(tagStr.split("\\s*,\\s*"))) // split 후 양쪽 공백 제거
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        //유형 선호도 정규화
        Map<String,Double> typePreferWeight=typeCount.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue()/(double)problems.size())); //value 전체 문제 수로 정규화

        //최종 점수 계산(점수 기반 가중치 + 유형 선호도 가중치)
        List<WeaknessRecommendDTO> recommendProblems = aiSolvedList.stream()
                .map(r -> {

                    //문제별 점수 벡터
                    double problemRed=r.getReadLevel();
                    double problemOpt=r.getOptLevel();
                    double problemDup=r.getDupLevel();

                    //유클리드 거리 계산
                    //작을수록 유사
                    double distance=Math.sqrt(
                            Math.pow(problemRed-averageUserRead,2)
                            + Math.pow(problemOpt-averageUserOptimize,2)
                            + Math.pow(problemDup-averageUserduplicate,2)
                    );

                    //유사도 점수 계산
                    //유사할수록 높은 점수 받도록 변환
                    double distanceScore= 1/(distance+1);

                    //문제 유형 가중치 합산
                    String allTag=r.getProblem().getTag();
                    double typeWeightSum = 0.0;
                    if(allTag!=null && !allTag.isEmpty()){
                        String[] tags=allTag.split(",");
                        for(String tag : tags){
                            typeWeightSum+=typePreferWeight.getOrDefault(tag.trim(), 0.0);
                        }
                    }

                //유형-점수 합산
                double finalScore = typeWeightSum * 0.4 + distanceScore * 0.6; //유형은 결과에 40% 영향 점수는 60% 영향

                //결과 DTO
                return new WeaknessRecommendDTO(r.getId(),r.getProblem().getTitle(), r.getProblem().getTag(), r.getProblem().getLevel(),finalScore);
                })
                .collect(Collectors.toList());


        // 유저가 푼 문제들의 ID만 추출
        Set<Long> solvedProblemIds = submittedProblems.stream()
                .filter(sp -> sp.getUserId().getUserId().equals(user.getUserId()))
                .map(sp -> sp.getProblemId().getProblemId())
                .collect(Collectors.toSet());

        // 추천 리스트에서 이미 푼 문제 제거
        List<WeaknessRecommendDTO> filteredRecommendProblems = recommendProblems.stream()
                .filter(dto -> !solvedProblemIds.contains(dto.getProblemId()))
                .collect(Collectors.toList());

        filteredRecommendProblems.forEach(problem -> {
            problem.setFinalScore(new BigDecimal(problem.getFinalScore()).setScale(3, RoundingMode.HALF_UP).doubleValue());
        });

        int recommendCount = Math.min(3,filteredRecommendProblems.size());
        return filteredRecommendProblems.subList(0, recommendCount); // 상위 3개만 잘라서 반환

    }
}
