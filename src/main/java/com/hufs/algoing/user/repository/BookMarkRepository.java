package com.hufs.algoing.user.repository;

import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.user.entity.BookMark;
import com.hufs.algoing.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookMarkRepository extends JpaRepository<BookMark, Long> {
    Optional<BookMark> findByUserIdAndProblemId(User user, Problem problem);
    List<BookMark> findAllByUserIdUserIdAndStatusIsTrue(Long userId);
}


