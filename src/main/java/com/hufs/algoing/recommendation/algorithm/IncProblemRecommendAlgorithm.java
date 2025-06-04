package com.hufs.algoing.recommendation.algorithm;

import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.problem.entity.SubmittedProblem;
import com.hufs.algoing.problem.repository.ProblemRepository;
import com.hufs.algoing.recommendation.dto.IncProblemRecommendDTO;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.problem.repository.SubmittedProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IncProblemRecommendAlgorithm {

    private final SubmittedProblemRepository submittedProblemRepository;
    private final ProblemRepository problemRepository;

    public List<IncProblemRecommendDTO> recommend(User user) {
        //유저가 제출한 문제 가져오기
        List<SubmittedProblem> userSubmitted = submittedProblemRepository.findByUserId(user);

        //제출한 문제가 7개 이하면 해당 유저 티어 +-2차이의 문제 추천
        if(userSubmitted==null || userSubmitted.size()==0 || userSubmitted.size() <= 7){
            int userLevel = user.getTier(); // 유저 티어
            List<Problem> allProblems = problemRepository.findAll();

            List<Problem> randomProblem = allProblems.stream()
                    .filter(p -> Math.abs(p.getLevel() - userLevel) <= 2) // 티어 ±2
                    .filter(p -> !userSubmitted.contains(p.getProblemId())) // 이미 푼 문제 제외
                    .limit(3)
                    .collect(Collectors.toList());


            return randomProblem.stream()
                    .map(p -> new IncProblemRecommendDTO(
                            p.getProblemId(),
                            p.getTitle(),
                            p.getTag(),
                            p.getLevel()
                    ))
                    .collect(Collectors.toList());
        }

        //유저가 푼 문제 태그 추출
        List<String> userSubmittedTags = calculateUserSubmitTags(user, userSubmitted);

        //유형별 정답률 계산
        Map<String,Double> correctRates=calculateTagCorrectRate(userSubmitted);

        //정답률이 낮은 상위 3개 태그 선정
        //점수가 같을시 태그 이름순 정렬
        //1에 가까울수록 오답률 높음
        List<String> weakTags = correctRates.entrySet().stream()
                .sorted((e1, e2) -> {
                    int cmp = Double.compare(e2.getValue(), e1.getValue());
                    if (cmp == 0) return e1.getKey().compareTo(e2.getKey());
                    return cmp;
                })
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        System.out.println(weakTags);
        
        //유저가 아직 풀지 않은 문제들 중에서 약한 태그 포함된 문제 찾기
        List<Problem> allProblems = problemRepository.findAll();
        Set<Long> solvedProblemIds = userSubmitted.stream()
                .map(s -> s.getProblemId().getProblemId())
                .collect(Collectors.toSet());

        //유사도 높은 3개 추천
        List<Problem> recommendProblems = allProblems.stream()
                .filter(p -> !solvedProblemIds.contains(p.getProblemId())) // 이미 푼 문제 제외
                .filter(p -> similarWeakTags(p, weakTags)) // 약한 태그와 유사한 문제만 포함
                .sorted((p1, p2) -> {
                    double sim1 = calculateSimilarity(p1, weakTags);
                    double sim2 = calculateSimilarity(p2,weakTags);
                    return Double.compare(sim2, sim1); // 유사도 높은 순
                })
                .limit(3)
                .collect(Collectors.toList());

        return recommendProblems.stream()
                .map(p -> new IncProblemRecommendDTO(p.getProblemId(), p.getTitle(), p.getTag(),p.getLevel()))
                .collect(Collectors.toList());
    }

    // 유저가 푼 문제 유형 리스트로 반환
    private static List<String> calculateUserSubmitTags(User user, List<SubmittedProblem> submittedProblems) {
        return submittedProblems.stream()
                .filter(s -> s.getUserId().equals(user.getUserId()))
                .map(s -> s.getProblemId().getTag())
                .filter(tagStr -> tagStr != null && !tagStr.isEmpty())
                .flatMap(tagStr -> List.of(tagStr.split(",")).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    //백준 정답률=(맞은 문제 수) / (맞은 문제 수 + 첫 번째 맞기 전 까지 틀린 횟수)
    //유형별 정답률 구하기
    private static Map<String,Double> calculateTagCorrectRate(List<SubmittedProblem> submittedProblems){
        //제출 기록 묶기
        Map<Problem, List<SubmittedProblem>> groupedProblems = submittedProblems.stream()
                .collect(Collectors.groupingBy(SubmittedProblem::getProblemId));

        //태그별 정답수:오답수 저장할 Map
        Map<String, int[]> tagStats=new HashMap<>();

        for(Map.Entry<Problem, List<SubmittedProblem>> entry : groupedProblems.entrySet()){
            Problem problem = entry.getKey(); //현재 문제
            List<SubmittedProblem> attempt = entry.getValue(); //현재 문제에 대한 유저의 제출 목록

            // 제출 순서대로 정렬
            attempt.sort(Comparator.comparing(SubmittedProblem::getSubmittedAt));

            //정답을 맞추기까지 틀린 횟수
            boolean isSolved = false;
            int wrongCount=0;

            // 첫 번째 정답 전까지 틀린 횟수 계산
            for (SubmittedProblem sub : attempt) {
                if (sub.isCorrect()) {
                    isSolved = true;
                    break;
                } else {
                    wrongCount++; //faild or error
                }
            }

            if (!isSolved) continue; //문제를 아예 맞추지 못한 경우 계산에서 제외

            String tagStr = problem.getTag();
            if (tagStr == null || tagStr.isEmpty()) continue;

            for (String tag : tagStr.split(",")) {
                tag = tag.trim();
                tagStats.putIfAbsent(tag, new int[2]); // [정답 수, 오답 수]
                tagStats.get(tag)[0] += 1;       // 정답 수 증가
                tagStats.get(tag)[1] += wrongCount; // 틀린 횟수 누적
            }
        }
        // 태그별 오답률 계산
        Map<String, Double> correctRates = new HashMap<>();
        for (Map.Entry<String, int[]> entry : tagStats.entrySet()) {
            int correct = entry.getValue()[0];
            int wrong = entry.getValue()[1];
            double correctRate = (double) wrong / (correct + wrong);
            correctRates.put(entry.getKey(), correctRate);
        }
        return correctRates;

    }

    //문제가 많이 틀린 유형 태그 리스트와 유사한지 확인
    private boolean similarWeakTags(Problem problem, List<String> weakTags){
        if (problem.getTag() == null || problem.getTag().isEmpty()) return false;

        Set<String> problemTags = Arrays.stream(problem.getTag().split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        //유사도 계산을 위해 집합 만듦
        for (String weakTag : weakTags) {
            Set<String> weakTagSet = new HashSet<>();
            weakTagSet.add(weakTag.trim());

            double jaccard = calculateJaccard(problemTags,weakTagSet);
            if (jaccard > 0.1) { // 이상시 유사
                return true;
            }
        }
        return false;
    }

    // 문제와 자카드 유사도 총합 계산
    private double calculateSimilarity(Problem problem, List<String> weakTags) {
        if (problem.getTag() == null || problem.getTag().isEmpty()) return 0.0;

        Set<String> problemTags = Arrays.stream(problem.getTag().split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        double totalSim = 0.0;
        for (String weakTag : weakTags) {
            Set<String> weakTagSet = new HashSet<>();
            weakTagSet.add(weakTag.trim());
            totalSim += calculateJaccard(problemTags, weakTagSet);
        }
        return totalSim;
    }

    //자카드 유사도 계산: 교집합 크기 / 합집합 크기
    private double calculateJaccard(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2); // 교집합

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2); // 합집합

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }
}
