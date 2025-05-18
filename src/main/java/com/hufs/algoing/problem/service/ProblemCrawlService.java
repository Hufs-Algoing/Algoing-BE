package com.hufs.algoing.problem.service;

import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.solvedac.dto.SolvedAcProblemDTO;
import com.hufs.algoing.solvedac.dto.SolvedAcTagDTO;
import com.hufs.algoing.solvedac.entity.SolvedAcTags;
import com.hufs.algoing.solvedac.repository.TagRepository;
import com.hufs.algoing.solvedac.service.SolvedAcService;
import com.hufs.algoing.solvedac.service.SolvedAcTagService;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProblemCrawlService{
    private static final String BASE_URL = "https://www.acmicpc.net/problem/";
    private final SolvedAcService solvedAcService;
    private final SolvedAcTagService solvedAcTagService;
    private final TagRepository tagRepository;

    public ProblemCrawlService(SolvedAcService solvedAcService, SolvedAcTagService solvedAcTagService, TagRepository tagRepository) {
        this.solvedAcService = solvedAcService;
        this.solvedAcTagService = solvedAcTagService;
        this.tagRepository = tagRepository;
    }

    public String getProblemUrl(Long problemId) {
        return BASE_URL + problemId;
    }

//    public String getProblemUrlForSolvedAc(Long problemId) {
//        return "https://solved.ac/api/v3/problem/show" + problemId;
//    }

    public Problem crawlProblem(Long problemId) {

        List<SolvedAcTagDTO> tags = solvedAcService.getSolvedAcProblemInfo(problemId).getTags();
        solvedAcTagService.saveNewTags(tags);

        try{
            String url = getProblemUrl(problemId);
            Document doc = Jsoup.connect(url).get();

            SolvedAcProblemDTO solPro = solvedAcService.getSolvedAcProblemInfo(problemId);

            List<String> tagKeys = solPro.getTags().stream().map(SolvedAcTagDTO::getKey).collect(Collectors.toList());
            List<String> tagNames = solPro.getTags().stream()
                    .map(tag -> {
                        SolvedAcTags solvedAcTag = tagRepository.findNameByKey(tag.getKey());
                        return solvedAcTag != null ? solvedAcTag.getName() : null;
                    })
                    .collect(Collectors.toList());


            return Problem.builder()
                    .problemId(problemId)
                    .title(doc.select("#problem_title").text())
                    .description(doc.select("#problem_description").html())
                    .limit(doc.select("#problem_limit").html())
                    .input(doc.select("#problem_input p").text())
                    .output(doc.select("#problem_output p").text())
                    .sampleInput1(doc.select("#sample-input-1").html())
                    .sampleInput2(doc.select("#sample-input-2").html())
                    .sampleOutput1(doc.select("#sample-output-1").html())
                    .sampleOutput2(doc.select("#sample-output-2").html())
                    .tag(String.join(",", tagKeys))
                    .tagNames(String.join(",", tagNames))
                    .time(doc.select("#problem-info tbody td:first-child").text()) // 시간 제한은 크롤링하지 않음
                    .memory(doc.select("#problem-info tbody td:nth-child(2)").text()) // 메모리 제한은 크롤링하지 않음
                    .level(solPro.getLevel())
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("크롤링 중 오류 발생", e);
        }
    }

}
