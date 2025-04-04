package com.hufs.algoing.user.service;

import com.hufs.algoing.solvedac.entity.SolvedAcProfile;
import com.hufs.algoing.solvedac.service.SolvedAcService;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private SolvedAcService solvedAcService;

    @Autowired
    private UserRepository userRepository;

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

    public User getUserByHandle(String handle) {
        // handle을 이용하여 User 엔티티를 조회
        return (User) userRepository.findByHandle(handle)
                .orElseThrow(() -> new RuntimeException("User not found with handle: " + handle));
    }
}
