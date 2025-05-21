package com.hufs.algoing.user.service;

import com.hufs.algoing.global.code.ErrorStatus;
import com.hufs.algoing.global.exception.custom.ProblemNotFoundException;
import com.hufs.algoing.global.exception.custom.UserNotFoundException;
import com.hufs.algoing.problem.dto.SubmittedProblemDTO;
import com.hufs.algoing.problem.dto.ZandiDTO;
import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.problem.entity.ProblemStatus;
import com.hufs.algoing.problem.repository.ProblemRepository;
import com.hufs.algoing.problem.repository.SubmittedProblemRepository;
import com.hufs.algoing.review.dto.ReviewResponseDTO;
import com.hufs.algoing.review.dto.SearchReviewResponseDTO;
import com.hufs.algoing.review.entity.Review;
import com.hufs.algoing.review.repository.ReviewCustomRepository;
import com.hufs.algoing.solvedac.dto.SolvedAcProfileDTO;
import com.hufs.algoing.solvedac.service.SolvedAcService;
import com.hufs.algoing.user.dto.BookMarkDTO;
import com.hufs.algoing.user.dto.UserDTO;
import com.hufs.algoing.user.entity.BookMark;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;
import com.hufs.algoing.user.repository.BookMarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;


@RequiredArgsConstructor
@Service
public class UserService {

    @Autowired
    private SolvedAcService solvedAcService;

    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private SubmittedProblemRepository submittedProblemRepository;
    @Autowired
    private BookMarkRepository bookMarkRepository;


    @Qualifier("reviewCustomRepositoryImpl")
    @Autowired
    private ReviewCustomRepository reviewCustomRepository;
    @Autowired
    private ProblemRepository problemRepository;


    public void updateUserData(String handle) {
        // solved.ac API로부터 유저 정보 가져오기
        SolvedAcProfileDTO profile = solvedAcService.getSolvedAcProfile(handle);
        // User 엔티티로 변환 후 저장
        User user = userRepository.findByHandle(handle)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setBio(profile.getBio());
        user.setTier(profile.getTier());
//        user.setSolvedCount(profile.getSolvedCount());
        user.setProfileImageUrl(profile.getProfileImageUrl());
        userRepository.save(user);

    }

    //회원가입

    public Long signup(UserDTO userDTO) {
        return userRepository.save(User.builder()
                .email(userDTO.getEmail())
                .password(bCryptPasswordEncoder.encode(userDTO.getPassword()))
                .nickname(userDTO.getNickname())
                .build()).getUserId();
    }

    // 가입 후 핸들 입력
    public Long insertHandle(UserDTO userDTO, @AuthenticationPrincipal User principal) {
// 현재 인증된 사용자의 이메일로 User 엔티티를 조회
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        // userDTO에서 핸들 값을 가져와서 User 엔티티에 저장
        user.setHandle(userDTO.getHandle());
        // User 엔티티를 저장
        userRepository.save(user);

        // SolvedAcService를 사용하여 유저 정보를 업데이트
        updateUserData(userDTO.getHandle());

        // 생성된 ID를 반환
        return user.getUserId();
    }

    public List<ZandiDTO> getUserActivity(User user){
        return submittedProblemRepository.findGroupedByDate(user, ProblemStatus.SOLVED);
    }

    //MYPAGE: 유저가 푼 문제 조회
    public List<SubmittedProblemDTO> searchUserSolve(long userId){

        //유저 정보 확인
        User solvedUser=userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorStatus.USER_NOT_FOUND));

        // 유저가 푼 문제 가져오기
        return submittedProblemRepository.findByUserId(solvedUser)
                .stream()
                .map(entity -> new SubmittedProblemDTO(
                        entity.getSubmittedProblemId(),
                        entity.getUserId().getUserId(),
                        entity.getProblemId().getProblemId(),
                        entity.getProblemId().getTitle(),
                        entity.getAnswer(),
                        entity.getProblemId().getTag(),
                        entity.getProblemId().getLevel(),
                        entity.getLanguage(),
                        entity.getSubmittedAt(),
                        entity.getSubmittedDate()
                ))
                .toList();
    }

    //MYPAGE:저장된 AI 리뷰 받은 문제 조회
    public List<SearchReviewResponseDTO> searchUserReviewed(long userId){

        //유저 정보 확인
       userRepository.findById(userId)
               .orElseThrow(() -> new UserNotFoundException(ErrorStatus.USER_NOT_FOUND));

        // 해당 유저의 모든 리뷰 가져오기
        List<Review> reviews = reviewCustomRepository.getUserReviews(userId);

        // Review 엔티티를 SearchReviewResponseDTO로 변환
        return reviews.stream()
                .map(review -> new SearchReviewResponseDTO(
                        review.getId(),
                        review.getUser().getUserId(),
                        review.getProblemNum(),
                        review.getSummary(),
                        review.getCreatedAt(),
                        review.getCode(),
                        review.getLanguage()
                ))
                .toList();
    }

    //MYPAGE: 유저가 북마크한 문제
    //북마크 등록, 수정(PATCH)
    public boolean updateBookMark(Long userId, Long problemId, boolean status) {
        User bookMarkUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorStatus.USER_NOT_FOUND));

        Problem problems = problemRepository.findById(problemId)
                .orElseThrow(() -> new ProblemNotFoundException(ErrorStatus.PROBLEM_NOT_FOUND));

        //유저가 해당 문제를 북마크한 기록이 있는지 조회
        Optional<BookMark> optional = bookMarkRepository.findByUserIdAndProblemId(bookMarkUser, problems);

        BookMark bookmark;
        if (optional.isPresent()) {
            // 기존 북마크가 있다면 status 변경
            bookmark = optional.get();
            bookmark.setStatus(!bookmark.isStatus());
        } else {
            // 북마크가 없으면 새로 생성하고 기본 상태는 true로 등록
            bookmark = BookMark.registerBookMark(bookMarkUser, problems);
        }

        // 북마크 저장
        bookMarkRepository.save(bookmark);

        return bookmark.isStatus();
    }

    //북마크 조회(GET)
    public List<BookMarkDTO> getUserBookmarks(Long userId) {

        User searchBookMarkuser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorStatus.USER_NOT_FOUND));

        // 유저가 북마크한 문제 리스트 조회
        List<BookMark> bookmarks = bookMarkRepository.findAllByUserIdUserIdAndStatusIsTrue(userId);

        return bookmarks.stream()
                .map(bookmark -> new BookMarkDTO(
                        bookmark.getBookMarkId(),
                        bookmark.getUserId().getUserId(),
                        bookmark.getProblemId().getProblemId(),
                        bookmark.getProblemId().getTitle()
                ))
                .toList();

    }
}

