package com.hufs.algoing.review.controller;

import com.hufs.algoing.review.dto.ReviewRequestDTO;
import com.hufs.algoing.review.dto.ReviewResponseDTO;
import com.hufs.algoing.review.entity.Review;
import com.hufs.algoing.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Review API", description = "코드 리뷰 API")
@RequestMapping("/reviews")
@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "리뷰 요청 API", description = "해당 문제에 대한 AI 리뷰를 요청합니다.")
    @PostMapping
    public Mono<ResponseEntity<ReviewResponseDTO>> requestReview(@RequestBody ReviewRequestDTO dto) {
        return reviewService.handleReview(dto)
                .map(reviewResponseDto -> ResponseEntity.ok(reviewResponseDto));
    }





}






