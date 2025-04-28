package com.hufs.algoing.review.service;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hufs.algoing.global.chatgpt.service.GPTService;

import com.hufs.algoing.review.dto.ReviewRequestDTO;
import com.hufs.algoing.review.dto.ReviewResponseDTO;
import com.hufs.algoing.review.entity.Review;
import com.hufs.algoing.review.repository.ReviewRepository;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final GPTService gptService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;


    // Todo - 유저의 가장 최근 리뷰 가져오도록 수정
    public ReviewResponseDTO getReview(Long userId, Long problemNum) {
        Review review = reviewRepository.getReview(userId, problemNum);
        return new ReviewResponseDTO(review.getSummary());
    }

    public Mono<Review> handleReview(ReviewRequestDTO dto) {

        Mono<JsonNode> readReviewMono = handleReadReview(dto);
        Mono<JsonNode> optReviewMono = handleOptReview(dto);
        Mono<JsonNode> dupReviewMono = handleDupReview(dto);

        return Mono.zip(readReviewMono, optReviewMono, dupReviewMono)
                .flatMap(tuple3 -> {
                    JsonNode readReview = tuple3.getT1();
                    JsonNode optReview = tuple3.getT2();
                    JsonNode dupReview = tuple3.getT3();

                    return handlesummary(readReview, optReview, dupReview)
                            .flatMap(summary -> {

                                String finalSummary = summary.get("review").asText();

                                User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

                                Review review = Review.builder()
                                        .code(dto.getCode())
                                        .problemNum(dto.getProblemNum())
                                        .language(dto.getLanguage())
                                        .user(user)
                                        .readbility(readReview.get("readbility").asLong())
                                        .readReview(readReview.get("readReview").asText())
                                        .optimization(optReview.get("optimization").asLong())
                                        .optReview(optReview.get("optReview").asText())
                                        .duplicate(dupReview.get("duplicate").asLong())
                                        .dupReview(dupReview.get("dupReview").asText())
                                        .summary(finalSummary)
                                        .createdAt(LocalDateTime.now())
                                        .build();

                                reviewRepository.save(review);


                                return Mono.just(review);
                            });
                });
    }


    public Mono<JsonNode> handlesummary(JsonNode readReview, JsonNode optReview, JsonNode dupReview) {
        return gptService.requestSummary(readReview,optReview,dupReview)
                .flatMap(response -> {
                    try {
                        String content = response.get("choices").get(0).get("message").get("content").asText();
                        log.info("GPT 응답 content: {}", content);

                        JsonNode parsed = objectMapper.readTree(content);
                        String review = parsed.get("review").asText();

                        ObjectNode jsonResponse = objectMapper.createObjectNode();
                        jsonResponse.put("review", review);

                        return Mono.just(jsonResponse);

                    } catch (Exception e) {
                        log.error("Exception [Err_Location] : {}", e.getStackTrace()[0]);
                        return Mono.empty();
                    }
                });
    }

    public Mono<JsonNode> handleReadReview(ReviewRequestDTO dto) {
        return gptService.requestReadbility(dto)
                .flatMap(response -> {
                    try {
                        String content = response.get("choices").get(0).get("message").get("content").asText();
                        log.info("GPT 응답 content: {}", content);
                        JsonNode parsed = objectMapper.readTree(content);
                        Long readbility = parsed.get("readability").asLong();
                        String readReview = parsed.get("review").asText();

                        ObjectNode jsonResponse = objectMapper.createObjectNode();
                        jsonResponse.put("readbility", readbility);
                        jsonResponse.put("readReview", readReview);
                        return Mono.just(jsonResponse);

                    } catch (Exception e) {
                        log.error("Exception [Err_Location] : {}", e.getStackTrace()[0]);
                        return Mono.empty();
                    }
                });
    }

    public Mono<JsonNode> handleOptReview(ReviewRequestDTO dto) {
        return gptService.requestOptimization(dto)
                .flatMap(response -> {
                    try {
                        String content = response.get("choices").get(0).get("message").get("content").asText();
                        log.info("GPT 응답 content: {}", content);
                        JsonNode parsed = objectMapper.readTree(content);
                        Long optimization = parsed.get("optimization").asLong();
                        String optReview = parsed.get("review").asText();

                        ObjectNode jsonResponse = objectMapper.createObjectNode();
                        jsonResponse.put("optimization", optimization);
                        jsonResponse.put("optReview", optReview);
                        return Mono.just(jsonResponse);

                    } catch (Exception e) {
                        log.error("Exception [Err_Location] : {}", e.getStackTrace()[0]);
                        return Mono.empty();
                    }
                });
    }

    public Mono<JsonNode> handleDupReview(ReviewRequestDTO dto) {
        return gptService.requestDuplicate(dto)
                .flatMap(response -> {
                    try {
                        String content = response.get("choices").get(0).get("message").get("content").asText();
                        log.info("GPT 응답 content: {}", content);
                        JsonNode parsed = objectMapper.readTree(content);
                        Long duplicate = parsed.get("duplicate").asLong();
                        String dupReview = parsed.get("review").asText();

                        ObjectNode jsonResponse = objectMapper.createObjectNode();
                        jsonResponse.put("duplicate", duplicate);
                        jsonResponse.put("dupReview", dupReview);
                        return Mono.just(jsonResponse);

                    } catch (Exception e) {
                        log.error("Exception [Err_Location] : {}", e.getStackTrace()[0]);
                        return Mono.empty();
                    }
                });
    }



}
