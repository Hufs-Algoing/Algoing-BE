package com.hufs.algoing.comment.repository;

import com.hufs.algoing.comment.entity.Comment;
import com.hufs.algoing.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
}
