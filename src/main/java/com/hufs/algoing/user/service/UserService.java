package com.hufs.algoing.user.service;

import com.hufs.algoing.solvedac.entity.SolvedAcProfile;
import com.hufs.algoing.solvedac.service.SolvedAcService;
import com.hufs.algoing.user.dto.UserDTO;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    @Autowired
    private SolvedAcService solvedAcService;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void updateUserData(String handle) {
        // solved.ac API로부터 유저 정보 가져오기
        SolvedAcProfile profile = solvedAcService.getSolvedAcProfile(handle);
        // User 엔티티로 변환 후 저장
        User user = new User();
        user.setBio(profile.getBio());
        user.setTier(profile.getTier());
        user.setSolvedCount(profile.getSolvedCount());
        user.setProfileImageUrl(profile.getProfileImageUrl());
        userRepository.save(user);

    }

    //회원가입

    public Long signup(UserDTO userDTO) {
        return userRepository.save(User.builder()
                .email(userDTO.getEmail())
                .password(bCryptPasswordEncoder.encode(userDTO.getPassword()))
                .build()).getUserId();
    }
}
