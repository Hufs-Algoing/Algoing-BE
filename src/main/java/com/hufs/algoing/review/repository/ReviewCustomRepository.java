package com.hufs.algoing.review.repository;

import com.hufs.algoing.review.dto.ReviewResponseDTO;
import com.hufs.algoing.review.entity.Review;
import com.hufs.algoing.user.entity.User;

import java.util.List;

public interface ReviewCustomRepository {

    // 해당 유저의 특정 문제 가장 최신 리뷰 가져오기
    Review getRecentReview(Long userId, Long problemNum);

    // 해당 유저의 모든 리뷰 가져오기
    List<Review> getUserReviews(Long userId);
}
