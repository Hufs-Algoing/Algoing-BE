package com.hufs.algoing.global.chatgpt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hufs.algoing.aisolved.entity.AISolved;
import com.hufs.algoing.aisolved.repository.AISolvedRepository;
import com.hufs.algoing.global.exception.BadWebClientRequestException;
import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.problem.repository.ProblemRepository;
import com.hufs.algoing.review.dto.ReviewRequestDTO;
import com.hufs.algoing.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class GPTService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ProblemRepository problemRepository;
    private final AISolvedRepository aiSolvedRepository;
    private final ReviewRepository reviewRepository;


    @SneakyThrows
    public void analyzeAllProblems() {
        List<Problem> problems = problemRepository.findAll();
        Flux.fromIterable(problems)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(this::analyzeAndSave)
                .sequential()
                .doOnComplete(() -> log.info("모든 문제를 분석 완료했습니다."))
                .subscribe();
    }

    private Mono<Void> analyzeAndSave(Problem problem) {

        log.info("분석 시작 - 문제: {}, Thread: {}", problem.getProblemId(), Thread.currentThread().getName());
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", "gpt-4o-mini");

        ObjectNode inputOutputJson = objectMapper.createObjectNode();
        inputOutputJson.put("input", problem.getInput());
        inputOutputJson.put("output", problem.getOutput());

        ObjectNode constraintsJson = objectMapper.createObjectNode();
        constraintsJson.put("time", problem.getTime());
        constraintsJson.put("memory", problem.getMemory());

        ArrayNode messages = objectMapper.createArrayNode();
        // Todo- content에 앞에 제목 붙여줄 필요?
        messages.add(createMessage("system", getInstruction("analyzeInstructions.md")));
        messages.add(createMessage("user", "problem:" + problem.getDescription()));
        messages.add(createMessage("user", "Input, Output\n" + inputOutputJson.toPrettyString()));
        messages.add(createMessage("user", "Time limit, Memory limit\n" + constraintsJson.toPrettyString()));

        requestBody.set("messages", messages);

        ObjectNode responseFormat = objectMapper.createObjectNode();
        responseFormat.put("type", "json_object");
        requestBody.set("response_format", responseFormat);

        return webClient
                .post()
                //.uri(aiUrl)
                //.header("Authorization", "Bearer " + aiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(statusCode -> statusCode.is4xxClientError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new BadWebClientRequestException(
                                                response.statusCode().value(),
                                                String.format("4xx 외부 요청 오류. statusCode: %s, response: %s, header: %s",
                                                        response.statusCode().value(),
                                                        body,
                                                        response.headers().asHttpHeaders())
                                        )
                                ))
                )
                .onStatus(statusCode -> statusCode.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new WebClientResponseException(
                                                response.statusCode().value(),
                                                String.format("5xx 외부 시스템 오류. %s", body),
                                                response.headers().asHttpHeaders(),
                                                null,
                                                null
                                        )
                                ))
                )
                .bodyToMono(JsonNode.class)
                .flatMap(response -> {
                    try {
                        String content = response.get("choices").get(0).get("message").get("content").asText();
                        JsonNode parsed = objectMapper.readTree(content);

                        AISolved aiSolved = AISolved
                                .builder()
                                .readLevel(parsed.get("readLevel").asLong())
                                .optLevel(parsed.get("optLevel").asLong())
                                .dupLevel(parsed.get("dupLevel").asLong())
                                .readTip(parsed.get("readTip").asText())
                                .optTip(parsed.get("optTip").asText())
                                .dupTip(parsed.get("dupTip").asText())
                                .pattern(parsed.get("pattern").asText())
                                .problem(problem)
                                .build();

                        aiSolvedRepository.save(aiSolved);
                    } catch (Exception e) {
                        log.error("Exception [Err_Location] : {}", e.getStackTrace()[0]);
                    }
                    return Mono.empty();
                });
    }

    // 최종 리뷰 요청
    public Mono<JsonNode> requestSummary(JsonNode readReview, JsonNode optReview, JsonNode dupReview) {

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", "gpt-4o-mini");

        ObjectNode allReview = objectMapper.createObjectNode();
        allReview.set("readReview", readReview);
        allReview.set("optReview", optReview);
        allReview.set("dupReview", dupReview);

        ArrayNode messages = objectMapper.createArrayNode();
        messages.add(createMessage("system", getInstruction("summaryInstructions.md")));
        messages.add(createMessage("user", String.valueOf(allReview)));

        requestBody.set("messages", messages);

        ObjectNode responseFormat = objectMapper.createObjectNode();
        responseFormat.put("type", "json_object");
        requestBody.set("response_format", responseFormat);

        return webClient
                .post()
                //.uri(aiUrl)
                //.header("Authorization", "Bearer " + aiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(statusCode -> statusCode.is4xxClientError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new BadWebClientRequestException(
                                                response.statusCode().value(),
                                                String.format("4xx 외부 요청 오류. statusCode: %s, response: %s, header: %s",
                                                        response.statusCode().value(),
                                                        body,
                                                        response.headers().asHttpHeaders())
                                        )
                                ))
                )
                .onStatus(statusCode -> statusCode.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new WebClientResponseException(
                                                response.statusCode().value(),
                                                String.format("5xx 외부 시스템 오류. %s", body),
                                                response.headers().asHttpHeaders(),
                                                null,
                                                null
                                        )
                                ))
                )
                .bodyToMono(JsonNode.class);

    }

    // 리뷰 - 가독성 요청
    public Mono<JsonNode> requestReadbility(ReviewRequestDTO dto){

        log.info("가독성 리뷰 - 유저: {}, Thread: {}", dto.getUserId(), Thread.currentThread().getName());

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", "gpt-4o-mini");

        AISolved aiSolved = aiSolvedRepository.findByProblem_ProblemId(dto.getProblemNum());
        String readTip = aiSolved.getReadTip();

        ObjectNode codeJson = objectMapper.createObjectNode();
        codeJson.put("readTip", readTip);
        codeJson.put("language", dto.getLanguage());
        codeJson.put("code", dto.getCode());

        ArrayNode messages = objectMapper.createArrayNode();
        messages.add(createMessage("system", getInstruction("readInstructions.md")));
        messages.add(createMessage("user", String.valueOf(codeJson)));

        requestBody.set("messages", messages);

        ObjectNode responseFormat = objectMapper.createObjectNode();
        responseFormat.put("type", "json_object");
        requestBody.set("response_format", responseFormat);

        return webClient
                .post()
                //.uri(aiUrl)
                //.header("Authorization", "Bearer " + aiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(statusCode -> statusCode.is4xxClientError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new BadWebClientRequestException(
                                                response.statusCode().value(),
                                                String.format("4xx 외부 요청 오류. statusCode: %s, response: %s, header: %s",
                                                        response.statusCode().value(),
                                                        body,
                                                        response.headers().asHttpHeaders())
                                        )
                                ))
                )
                .onStatus(statusCode -> statusCode.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new WebClientResponseException(
                                                response.statusCode().value(),
                                                String.format("5xx 외부 시스템 오류. %s", body),
                                                response.headers().asHttpHeaders(),
                                                null,
                                                null
                                        )
                                ))
                )
                .bodyToMono(JsonNode.class);

    }

    // 리뷰 - 최적성 요청
    public Mono<JsonNode> requestOptimization(ReviewRequestDTO dto){

        log.info("최적성 리뷰 - 유저: {}, Thread: {}", dto.getUserId(), Thread.currentThread().getName());

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", "gpt-4o-mini");

        AISolved aiSolved = aiSolvedRepository.findByProblem_ProblemId(dto.getProblemNum());
        String optTip = aiSolved.getOptTip();

        ObjectNode codeJson = objectMapper.createObjectNode();
        codeJson.put("optTip", optTip);
        codeJson.put("language", dto.getLanguage());
        codeJson.put("code", dto.getCode());

        ArrayNode messages = objectMapper.createArrayNode();
        messages.add(createMessage("system", getInstruction("optInstructions.md")));
        messages.add(createMessage("user", String.valueOf(codeJson)));

        requestBody.set("messages", messages);

        ObjectNode responseFormat = objectMapper.createObjectNode();
        responseFormat.put("type", "json_object");
        requestBody.set("response_format", responseFormat);

        return webClient
                .post()
                //.uri(aiUrl)
                //.header("Authorization", "Bearer " + aiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(statusCode -> statusCode.is4xxClientError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new BadWebClientRequestException(
                                                response.statusCode().value(),
                                                String.format("4xx 외부 요청 오류. statusCode: %s, response: %s, header: %s",
                                                        response.statusCode().value(),
                                                        body,
                                                        response.headers().asHttpHeaders())
                                        )
                                ))
                )
                .onStatus(statusCode -> statusCode.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new WebClientResponseException(
                                                response.statusCode().value(),
                                                String.format("5xx 외부 시스템 오류. %s", body),
                                                response.headers().asHttpHeaders(),
                                                null,
                                                null
                                        )
                                ))
                )
                .bodyToMono(JsonNode.class);

    }

    // 리뷰 - 중복성 요청
    public Mono<JsonNode> requestDuplicate(ReviewRequestDTO dto){

        log.info("최적성 리뷰 - 유저: {}, Thread: {}", dto.getUserId(), Thread.currentThread().getName());

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", "gpt-4o-mini");

        AISolved aiSolved = aiSolvedRepository.findByProblem_ProblemId(dto.getProblemNum());
        String dupTip = aiSolved.getDupTip();

        ObjectNode codeJson = objectMapper.createObjectNode();
        codeJson.put("dupTip", dupTip);
        codeJson.put("language", dto.getLanguage());
        codeJson.put("code", dto.getCode());

        ArrayNode messages = objectMapper.createArrayNode();
        messages.add(createMessage("system", getInstruction("dupInstructions.md")));
        messages.add(createMessage("user", String.valueOf(codeJson)));

        requestBody.set("messages", messages);

        ObjectNode responseFormat = objectMapper.createObjectNode();
        responseFormat.put("type", "json_object");
        requestBody.set("response_format", responseFormat);

        return webClient
                .post()
                //.uri(aiUrl)
                //.header("Authorization", "Bearer " + aiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(statusCode -> statusCode.is4xxClientError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new BadWebClientRequestException(
                                                response.statusCode().value(),
                                                String.format("4xx 외부 요청 오류. statusCode: %s, response: %s, header: %s",
                                                        response.statusCode().value(),
                                                        body,
                                                        response.headers().asHttpHeaders())
                                        )
                                ))
                )
                .onStatus(statusCode -> statusCode.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new WebClientResponseException(
                                                response.statusCode().value(),
                                                String.format("5xx 외부 시스템 오류. %s", body),
                                                response.headers().asHttpHeaders(),
                                                null,
                                                null
                                        )
                                ))
                )
                .bodyToMono(JsonNode.class);

    }

    public String getInstruction(String file) {
        try (InputStream inputStream = new ClassPathResource("instructions/" + file).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Exception [Err_Location] : {}", e.getStackTrace()[0]);
            return "";
        }
    }

    public ObjectNode createMessage(String role, String content) {
        ObjectNode message = objectMapper.createObjectNode();
        message.put("role", role);
        message.put("content", content);
        return message;
    }
}




