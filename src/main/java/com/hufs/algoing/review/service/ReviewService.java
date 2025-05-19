package com.hufs.algoing.review.service;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hufs.algoing.global.chatgpt.service.GPTService;

import com.hufs.algoing.review.dto.ReviewRequestDTO;
import com.hufs.algoing.review.dto.ReviewResponseDTO;
import com.hufs.algoing.review.entity.Review;
import com.hufs.algoing.review.repository.ReviewRepository;
import com.hufs.algoing.snapshot.Service.SnapShotService;
import com.hufs.algoing.snapshot.entity.Snapshot;
import com.hufs.algoing.snapshot.repository.SnapShotRepository;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.scheduler.Schedulers;


@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final GPTService gptService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final SnapShotService snapShotService;

    // 해당 문제의 가장 최신 리뷰
    public ReviewResponseDTO getRecentReview(User user, Long problemNum) {
        Review review = reviewRepository.getRecentReview(user.getUserId(), problemNum);
        return new ReviewResponseDTO(review.getSummary());
    }

    public Mono<ReviewResponseDTO> handleReview(ReviewRequestDTO dto, User user) {

        log.info(dto.toString());
        Mono<JsonNode> readReviewMono = handleReadReview(dto)
                .doOnSubscribe(sub->log.info("readReview"))
                .subscribeOn(Schedulers.boundedElastic());

        Mono<JsonNode> optReviewMono = handleOptReview(dto)
                .doOnSubscribe(sub->log.info("optReview"))
                .subscribeOn(Schedulers.boundedElastic());

        Mono<JsonNode> dupReviewMono = handleDupReview(dto)
                .doOnSubscribe(sub->log.info("dupReview"))
                .subscribeOn(Schedulers.boundedElastic());

        return Mono.zip(readReviewMono, optReviewMono, dupReviewMono)
                .flatMap(tuple3 -> {
                    JsonNode readReview = tuple3.getT1();
                    JsonNode optReview = tuple3.getT2();
                    JsonNode dupReview = tuple3.getT3();

                    return handlesummary(readReview, optReview, dupReview)

                            .flatMap(summary -> {

                                String finalSummary = summary.get("review").asText();

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
                                        .build();

                                snapShotService.checkAndSaveSnapShot(user, review);
                                reviewRepository.save(review);

                                ReviewResponseDTO finalSummaryDTO = ReviewResponseDTO.builder()
                                        .summary(finalSummary)
                                        .build();

                                return Mono.just(finalSummaryDTO);
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