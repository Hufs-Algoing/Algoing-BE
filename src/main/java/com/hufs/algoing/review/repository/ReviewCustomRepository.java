package com.hufs.algoing.review.repository;

import com.hufs.algoing.review.entity.Review;
import com.hufs.algoing.user.entity.User;

public interface ReviewCustomRepository {
    Review getReview(Long userId, Long problemNum);
}
