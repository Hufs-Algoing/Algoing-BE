package com.hufs.algoing.user.service;

import com.hufs.algoing.problem.dto.ZandiDTO;
import com.hufs.algoing.problem.entity.ProblemStatus;
import com.hufs.algoing.problem.repository.SubmittedProblemRepository;
import com.hufs.algoing.solvedac.dto.SolvedAcProfileDTO;
import com.hufs.algoing.solvedac.service.SolvedAcService;
import com.hufs.algoing.user.dto.UserDTO;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
}
