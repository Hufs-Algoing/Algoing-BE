package com.hufs.algoing.problem.service;

import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemCrawlService problemCrawlService;

    public Problem getOrCrawlProblem(Long problemId){

        if(problemRepository.existsByProblemId(problemId)){
            return (Problem) problemRepository.findByProblemId(problemId).orElse(null);
        }
        Problem newProblem = problemCrawlService.crawlProblem(problemId);

        return problemRepository.save(newProblem);
    }

    public void crawlProblemsByTagWithTier(String tag) {
        // 각 티어별 개수
        int easyCount = 2;
        int normalCount = 4;
        int midCount = 3;
        int hardCount = 1;

        List<Problem> problems = new ArrayList<>();

        // 쉬움 - 브론즈2(8) ~ 실버4(12)
        problems.addAll(problemCrawlService.crawlProblemsByTierAndTag(tag, 8, 12, easyCount));

        // 보통 - 실버3(13) ~ 실버1(15)
        problems.addAll(problemCrawlService.crawlProblemsByTierAndTag(tag, 13, 15, normalCount));

        // 중간 - 골드5(16) ~ 골드3(18)
        problems.addAll(problemCrawlService.crawlProblemsByTierAndTag(tag, 16, 18, midCount));

        // 어려움 - 골드2(19) ~ 골드1(20), 플레5(21)
        problems.addAll(problemCrawlService.crawlProblemsByTierAndTag(tag, 19, 21, hardCount));

        problemRepository.saveAll(problems);
    }

    public List<Problem> searchByTitleOrTagNamesOrDescriptionOrId(String keyword) {
        List<Problem> results = new ArrayList<>();

        // ID 검색 (숫자)
        if (keyword != null && keyword.matches("^\\d+$")) {
            try {
                Long id = Long.parseLong(keyword);
                problemRepository.findById(id).ifPresent(results::add);
            } catch (NumberFormatException ignored) {
                // 숫자가 아닌 경우 무시
            }
        }

        // 텍스트 검색
        if (keyword != null && !keyword.trim().isEmpty()) {
            results.addAll(problemRepository.searchByKeyword(keyword));
        }

        return results.stream().distinct().toList();

    }




}
