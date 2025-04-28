package com.hufs.algoing.review.repository;

import com.hufs.algoing.review.entity.Review;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import static com.hufs.algoing.review.entity.QReview.review;

@Repository
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    // 가장 최신 리뷰
    @Override
    public Review getReview(Long userId, Long problemNum) {
        return jpaQueryFactory
                .selectFrom(review)
                .where(review.user.userId.eq(userId))
                .where(review.problemNum.eq(problemNum))
                .orderBy(review.createdAt.desc())
                .fetchFirst();
    }
}
