package com.hufs.algoing.aisolved.service;

import com.hufs.algoing.aisolved.dto.AISolvedDTO;
import com.hufs.algoing.aisolved.entity.AISolved;
import com.hufs.algoing.aisolved.repository.AISolvedRepository;
import com.hufs.algoing.global.chatgpt.service.GPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AISolvedService {
    private final AISolvedRepository aiSolvedRepository;
    private final GPTService gptService;

    // 문제 번호에 해당하는 AISolved 정보
    public AISolvedDTO getAISolved(Long problemId) {
        AISolved aiSolved = aiSolvedRepository.findByProblem_ProblemId(problemId);

        return new AISolvedDTO(
                aiSolved.getReadLevel(),
                aiSolved.getOptLevel(),
                aiSolved.getDupLevel(),
                aiSolved.getReadTip(),
                aiSolved.getOptTip(),
                aiSolved.getDupTip(),
                aiSolved.getPattern()
        );
    }




}
