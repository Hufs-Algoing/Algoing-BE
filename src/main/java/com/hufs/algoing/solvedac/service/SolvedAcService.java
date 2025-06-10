package com.hufs.algoing.solvedac.service;

import com.hufs.algoing.global.code.ErrorStatus;
import com.hufs.algoing.global.exception.custom.BojIdNotExistException;
import com.hufs.algoing.solvedac.dto.SolvedAcProblemDTO;
import com.hufs.algoing.solvedac.dto.SolvedAcProfileDTO;
import com.hufs.algoing.solvedac.dto.SolvedAcSearchDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SolvedAcService {

    public SolvedAcProfileDTO getSolvedAcProfile(String bojId) throws BojIdNotExistException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://solved.ac/api/v3/user/show?handle=" + bojId;

        try {
            ResponseEntity<SolvedAcProfileDTO> response = restTemplate.getForEntity(url, SolvedAcProfileDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            // 404 에러 처리: 존재하지 않는 사용자일 경우 null 반환 또는 예외 재정의 가능
            throw new BojIdNotExistException(ErrorStatus.BOJ_ID_NOT_EXISTS);
        }
    }

    public SolvedAcProblemDTO getSolvedAcProblemInfo(Long problemId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://solved.ac/api/v3/problem/show?problemId=" + problemId;

        ResponseEntity<SolvedAcProblemDTO> response = restTemplate.getForEntity(url, SolvedAcProblemDTO.class);
        return response.getBody();
    }

    public List<SolvedAcProblemDTO> getProblemsByTagAndTier(String tag, int minTier, int maxTier, int count) {
        RestTemplate restTemplate = new RestTemplate();
        String query = String.format("tag:%s+tier:%d..%d", tag, minTier, maxTier);
        String apiUrl = "https://solved.ac/api/v3/search/problem?query=" + query;

        SolvedAcSearchDTO response = restTemplate.getForObject(apiUrl, SolvedAcSearchDTO.class);
        if (response == null || response.getItems() == null) return List.of();
        return response.getItems().stream().limit(count).toList();
    }


}
