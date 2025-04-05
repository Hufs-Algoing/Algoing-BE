package com.hufs.algoing.aisolved.service;

import com.hufs.algoing.aisolved.dto.AISolvedDTO;
import com.hufs.algoing.aisolved.entity.AISolved;
import com.hufs.algoing.aisolved.repository.AISolvedRepository;
import com.hufs.algoing.global.chatgpt.service.GPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AISolvedService {
    private final AISolvedRepository aiSolvedRepository;
    private final GPTService gptService;

    // 문제 번호에 해당하는 AISolved 정보
    public AISolvedDTO getAISolved(Long problemNum) {
        AISolved aiSolved = aiSolvedRepository.findByProblem_ProblemNum(problemNum);

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
