package com.hufs.algoing.submit.service;

import com.hufs.algoing.global.code.ErrorStatus;
import com.hufs.algoing.global.exception.custom.UserNotFoundException;
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
    public void judgePoint(RecaptchaResponseDTO dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(ErrorStatus.USER_NOT_FOUND));
        if (dto.isCorrect()) {
            int originPoint = user.getUserPoint();
            user.updatePoint(originPoint + 5);
        }
        log.info("유저 id {}의 포인트가 {}로 증가했습니다.", user.getUserId(), user.getUserPoint());
    }

    public RecaptchaResponseDTO solveAndJudge(SubmitRequestDTO dto) throws Exception {
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

        // 결과에 따라 포인트 적립
        judgePoint(result, dto.getUserId());

        return result;
    }


}





