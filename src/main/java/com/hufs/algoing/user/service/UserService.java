package com.hufs.algoing.user.service;

import com.hufs.algoing.global.code.ErrorStatus;
import com.hufs.algoing.global.exception.custom.BojIdExistException;
import com.hufs.algoing.global.exception.custom.ProblemNotFoundException;
import com.hufs.algoing.global.exception.custom.UserNotFoundException;
import com.hufs.algoing.global.oauth.PrincipalDetails;
import com.hufs.algoing.problem.dto.SubmittedProblemDTO;
import com.hufs.algoing.problem.dto.ZandiDTO;
import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.problem.entity.ProblemStatus;
import com.hufs.algoing.problem.repository.ProblemRepository;
import com.hufs.algoing.problem.repository.SubmittedProblemRepository;
import com.hufs.algoing.review.dto.SearchReviewResponseDTO;
import com.hufs.algoing.review.entity.Review;
import com.hufs.algoing.review.repository.ReviewCustomRepository;
import com.hufs.algoing.solvedac.dto.SolvedAcProfileDTO;
import com.hufs.algoing.solvedac.service.SolvedAcService;
import com.hufs.algoing.user.dto.BookMarkDTO;
import com.hufs.algoing.user.dto.BojInsertDTO;
import com.hufs.algoing.user.dto.UserInfoDTO;
import com.hufs.algoing.user.entity.BookMark;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.BookMarkRepository;
import com.hufs.algoing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class UserService {

    private static final String AES_ALGORITHM = "AES";
    private static final String SECRET_KEY = "1234567890123456"; // 16-byte key (예제용)
    private final UserRepository userRepository;
    @Autowired
    private SolvedAcService solvedAcService;
    @Autowired
    private SubmittedProblemRepository submittedProblemRepository;
    @Autowired
    private BookMarkRepository bookMarkRepository;
    @Qualifier("reviewCustomRepositoryImpl")
    @Autowired
    private ReviewCustomRepository reviewCustomRepository;
    @Autowired
    private ProblemRepository problemRepository;

    public User getUserByUserId(Long userId) {
        // User 엔티티를 userId로 조회
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorStatus.USER_NOT_FOUND));
    }

    public void updateUserSolvedAcData(String bojId) throws Exception {
        // solved.ac API로부터 유저 정보 가져오기
        SolvedAcProfileDTO profile = solvedAcService.getSolvedAcProfile(bojId);
        // User 엔티티로 변환 후 저장
        User user = userRepository.findByBojId(bojId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setBio(profile.getBio());
        user.setTier(profile.getTier());
//        user.setSolvedCount(profile.getSolvedCount());

        userRepository.save(user);

    }

    // 가입 후 핸들 입력
    public void insertBoj(BojInsertDTO bojInsertDTO, @AuthenticationPrincipal PrincipalDetails principal) throws Exception {
// 현재 인증된 사용자의 이메일로 User 엔티티를 조회
        User user = userRepository.findByEmail(principal.getUser().getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        user.setBojId(bojInsertDTO.getBojId());
        // bojId 중복 확인
                if (userRepository.findByBojId(bojInsertDTO.getBojId()).isPresent()) {
                    throw new BojIdExistException(ErrorStatus.BOJ_ID_EXISTS);
                }else if(solvedAcService.getSolvedAcProfile(bojInsertDTO.getBojId()) == null) {
                    throw new BojIdExistException(ErrorStatus.BOJ_ID_NOT_EXISTS);
                }
        user.setBojPassword(encrypt(bojInsertDTO.getBojPassword()));
        // User 엔티티를 저장
        userRepository.save(user);

        // SolvedAcService를 사용하여 유저 정보를 업데이트
        updateUserSolvedAcData(bojInsertDTO.getBojId());
    }

    public List<ZandiDTO> getUserActivity(User user) {
        return submittedProblemRepository.findGroupedByDate(user, ProblemStatus.SOLVED);
    }

    //MYPAGE: 유저가 푼 문제 조회
    public List<SubmittedProblemDTO> searchUserSolve(long userId) {

        //유저 정보 확인
        User solvedUser = userRepository.findById(userId)
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
    public List<SearchReviewResponseDTO> searchUserReviewed(long userId) {

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

    private String encrypt(String data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String encryptedData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        return new String(cipher.doFinal(decoded));
    }

    public UserInfoDTO toUserInfoDTO(User user) {

        return new UserInfoDTO(user);
    }
}

