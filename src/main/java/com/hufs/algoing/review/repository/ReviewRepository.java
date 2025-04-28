package com.hufs.algoing.review.repository;

import com.hufs.algoing.review.entity.Review;
import com.hufs.algoing.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {
    Review findByUserUserIdAndProblemNum(Long userId, Long problemNum);


}
