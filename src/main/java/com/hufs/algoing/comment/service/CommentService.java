package com.hufs.algoing.comment.service;

import com.hufs.algoing.comment.dto.CommentRequestDTO;
import com.hufs.algoing.comment.dto.CommentResponseDTO;
import com.hufs.algoing.comment.entity.Comment;
import com.hufs.algoing.comment.repository.CommentRepository;
import com.hufs.algoing.global.code.ErrorStatus;
import com.hufs.algoing.global.exception.custom.CommentException;
import com.hufs.algoing.global.exception.custom.PostException;
import com.hufs.algoing.global.exception.custom.UserNotFoundException;
import com.hufs.algoing.post.entity.Post;
import com.hufs.algoing.post.repository.PostRepository;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // 특정 게시글 댓글 목록 조회
    public List<CommentResponseDTO> getCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(ErrorStatus.POST_NOT_FOUND));

        List<Comment> comments = commentRepository.findByPost(post);

        return comments.stream()
                .map(CommentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 댓글 작성
    public CommentResponseDTO createComment(Long postId, CommentRequestDTO commentRequestDTO) {
        Post post = postRepository.findByPostId(postId);
        User user = userRepository.findById(commentRequestDTO.getUserId()).orElseThrow(() -> new UserNotFoundException(ErrorStatus.USER_NOT_FOUND));

        Comment comment = Comment.builder()
                .code(commentRequestDTO.getCode())
                .user(user)
                .post(post)
                .content(commentRequestDTO.getContent())
                .isAdopted(false)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return CommentResponseDTO.fromEntity(savedComment);
    }

    // 댓글 채택
    @Transactional
    public CommentResponseDTO adoptComment(Long commentId, Long postAuthorId) {
        Comment commentToAdopt = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorStatus.COMMENT_NOT_FOUND));

        Post post = commentToAdopt.getPost();

        if (!post.getUser().getUserId().equals(postAuthorId)) {
            throw new PostException(ErrorStatus.NOT_POST_AUTHOR);
        }

        commentToAdopt.adopt();
        Comment adoptedComment = commentRepository.save(commentToAdopt);

        // 채택 당사자에게 포인트 부여
        User commentAuthor = commentToAdopt.getUser();
        int originPoint = commentAuthor.getUserPoint();
        commentAuthor.updatePoint(originPoint + 10);
        userRepository.save(commentAuthor);

        return CommentResponseDTO.fromEntity(adoptedComment);
    }

    public void deleteComment(Long commentId, Long userId) {
        Comment commentToDelete = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorStatus.COMMENT_NOT_FOUND));

        // 댓글 작성자와 로그인 사용자가 일치하는지 비교
        if (!commentToDelete.getUser().getUserId().equals(userId)) {
            throw new CommentException(ErrorStatus.NOT_COMMENT_AUTHOR);
        }

        commentRepository.delete(commentToDelete);

    }





}
