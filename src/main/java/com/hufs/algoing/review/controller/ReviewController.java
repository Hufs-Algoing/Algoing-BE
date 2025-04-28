package com.hufs.algoing.review.controller;

import com.hufs.algoing.review.dto.ReviewRequestDTO;
import com.hufs.algoing.review.dto.ReviewResponseDTO;
import com.hufs.algoing.review.entity.Review;
import com.hufs.algoing.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequestMapping("/review")
@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Mono<ResponseEntity<Review>> requestReview1(@RequestBody ReviewRequestDTO dto) {
        return reviewService.handleReview(dto)
                .map(review -> ResponseEntity.ok(review));
    }





}
