package com.hufs.algoing.problem.service;

import com.hufs.algoing.problem.entity.Problem;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

@Service
public class ProblemCrawlService{
    private static final String BASE_URL = "https://www.acmicpc.net/problem/";

    public String getProblemUrl(Long problemId) {
        return BASE_URL + problemId;
    }

    public Problem crawlProblem(Long problemId) {
        try{
            String url = getProblemUrl(problemId);
            Document doc = Jsoup.connect(url).get();



            return Problem.builder()
                    .problemId(problemId)
                    .title(doc.select("#problem_title").text())
                    .description(doc.select("#problem_description").html())
                    .limit(doc.select("#problem_limit").html())
                    .input(doc.select("#problem_input p").text())
                    .output(doc.select("#problem_output p").text())
                    .sampleInput1(doc.select("#sample-input-1").html())
                    .sampleIntput2(doc.select("#sample-input-2").html())
                    .sampleOutput1(doc.select("#sample-output-1").html())
                    .sampleOutput2(doc.select("#sample-output-2").html())
                    .tag("tag") // 태그는 크롤링하지 않음
                    .time(doc.select("#problem-info tbody td:first-child").text()) // 시간 제한은 크롤링하지 않음
                    .memory(doc.select("#problem-info tbody td:nth-child(2)").text()) // 메모리 제한은 크롤링하지 않음
                    .level(0L) // 난이도는 크롤링하지 않음
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("크롤링 중 오류 발생", e);
        }
    }

}
