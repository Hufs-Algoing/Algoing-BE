package com.hufs.algoing.solvedac.service;

import com.hufs.algoing.solvedac.entity.SolvedAcProfile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SolvedAcService {
    private static final String API_URL = "https://solved.ac/api/v3/user/show?handle=";

    public SolvedAcProfile getSolvedAcProfile(String handle) {
        RestTemplate restTemplate = new RestTemplate();
        String url = API_URL + handle;

        ResponseEntity<SolvedAcProfile> response = restTemplate.getForEntity(url, SolvedAcProfile.class);
        return response.getBody(); // JSON 데이터를 UserProfile 객체로 매핑
    }

}
