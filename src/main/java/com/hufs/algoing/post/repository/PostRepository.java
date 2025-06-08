package com.hufs.algoing.post.repository;

import com.hufs.algoing.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {


    Post findByPostId(Long postId);
}
