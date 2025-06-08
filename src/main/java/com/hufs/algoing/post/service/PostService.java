package com.hufs.algoing.post.service;

import com.hufs.algoing.post.dto.PostRequestDTO;
import com.hufs.algoing.post.dto.PostResponseDTO;
import com.hufs.algoing.post.entity.Post;
import com.hufs.algoing.post.repository.PostRepository;
import com.hufs.algoing.problem.entity.SubmittedProblem;
import com.hufs.algoing.problem.repository.SubmittedProblemRepository;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SubmittedProblemRepository submittedProblemRepository;


    public PostResponseDTO createPost(PostRequestDTO dto, User user, Long submittedProblemId) {

        SubmittedProblem submittedProblem = submittedProblemRepository.findById(submittedProblemId)
                .orElseThrow(() -> new IllegalArgumentException("Submitted Problem not found"));
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .language(submittedProblem.getLanguage())
                .user(user)
                .submittedProblem(submittedProblem)
                .build();

        Post saved = postRepository.save(post);

        return new PostResponseDTO(saved);

    }
}
