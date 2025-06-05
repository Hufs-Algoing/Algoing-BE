package com.hufs.algoing.submit.service;

import com.hufs.algoing.global.code.ErrorStatus;
import com.hufs.algoing.global.exception.custom.ProblemNotFoundException;
import com.hufs.algoing.global.exception.custom.UserNotFoundException;
import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.problem.entity.ProblemStatus;
import com.hufs.algoing.problem.entity.SubmittedProblem;
import com.hufs.algoing.problem.repository.ProblemRepository;
import com.hufs.algoing.problem.repository.SubmittedProblemRepository;
import com.hufs.algoing.recommendation.service.RecommendService;
import com.hufs.algoing.submit.dto.RecaptchaRequestDTO;
import com.hufs.algoing.submit.dto.RecaptchaResponseDTO;
import com.hufs.algoing.submit.dto.SubmitRequestDTO;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;
import com.hufs.algoing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;


@Service
@RequiredArgsConstructor
@Slf4j
public class SubmitService {
    private final UserRepository userRepository;
    private final WebClient.Builder webClientBuilder;
    private final UserService userService;
    private final ProblemRepository problemRepository;
    private final SubmittedProblemRepository submittedProblemRepository;

    public RecaptchaRequestDTO submit(SubmitRequestDTO dto) throws Exception {

        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new UserNotFoundException(ErrorStatus.USER_NOT_FOUND));
        String bojId = user.getBojId();
        String bojPassword = userService.decrypt(user.getBojPassword());

        return RecaptchaRequestDTO.builder()
                .userId(dto.getUserId())
                .code(dto.getCode())
                .language(dto.getLanguage())
                .bojId(bojId)
                .bojPassword(bojPassword)
                .problemNum(dto.getProblemNum())
                .build();

    }

    // 포인트 적립
    public void judgePoint(Problem problem, RecaptchaResponseDTO resultDTO, User user)  {
        if(resultDTO.isCorrect()){
            int originPoint = user.getUserPoint();
            Long level  = problem.getLevel();

            if(level>=1 && level<=5){ // bronze
                user.updatePoint(originPoint+5);
            }
            else if (level>=6 && level<=10) { // silver
                user.updatePoint(originPoint+10);
            }
            else if (level>=11 && level<=15){ // gold
                user.updatePoint(originPoint+15);
            }
            else if (level>=16 && level<=20){ // platinum
                user.updatePoint(originPoint+20);
            }
            else if(level>=21 && level<=25){ // diamond
                user.updatePoint(originPoint+25);
            }
            else if(level>=26 && level<=30){ // ruby
                user.updatePoint(originPoint+30);
            }
            log.info("유저 id - {}의 포인트가 {}에서 {}로 증가했습니다.", user.getUserId(), originPoint, user.getUserPoint());
        }
    }

    public RecaptchaResponseDTO solveAndJudge(SubmitRequestDTO dto) throws Exception {
        User user = userRepository.findById(dto.getUserId()).orElseThrow(()-> new UserNotFoundException(ErrorStatus.USER_NOT_FOUND));
        ProblemStatus status;

        RecaptchaRequestDTO recapDTO = submit(dto);

        RecaptchaResponseDTO result = webClientBuilder.build()
                .post()
                .uri("http://43.200.206.181:5000/start")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(recapDTO)
                .retrieve()
                .bodyToMono(RecaptchaResponseDTO.class)
                .timeout(Duration.ofSeconds(180))
                .doOnError(e -> log.error("파이썬 서버 요청 중 오류 발생", e))
                .onErrorReturn(new RecaptchaResponseDTO() {{
                    setMessage("TimeOut 또는 Error 발생 ");
                    setCorrect(false);
                }})
                .block();

        Problem problem = problemRepository.findById(dto.getProblemNum()).orElseThrow(()-> new ProblemNotFoundException(ErrorStatus.PROBLEM_NOT_FOUND));

        // 결과에 따라 포인트 적립
        judgePoint(problem, result, user);

        if(result.isCorrect()){
            status = ProblemStatus.SOLVED;
        }else{
            status = ProblemStatus.FAILED;
        }

        SubmittedProblem submitted = SubmittedProblem.builder()
                .userId(user)
                .problemId(problem)
                .answer(dto.getCode())
                .language(dto.getLanguage())
                .status(status)
                .recommendationSessionId(dto.getRecommendationSessionId())
                .build();

        // 제출 문제 저장
        submittedProblemRepository.save(submitted);

        return result;
    }


}





