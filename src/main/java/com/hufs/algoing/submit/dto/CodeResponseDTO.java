package com.hufs.algoing.submit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CodeResponseDTO {
    // 1. 프로그램의 표준 출력
    private String stdout;

    // 2. 프로그램의 표준 에러 출력 (기술적이고 상세한 로그)
    private String stderr;

    // 3. 코드 실행 결과 상태 (필수!)
    // 예: "SUCCESS", "COMPILE_ERROR", "RUNTIME_ERROR", "TIMEOUT", "UNSUPPORTED_LANGUAGE", "SERVER_ERROR"
    private String status;

    // 4. 사용자에게 친화적인 에러 메시지 (선택 사항이지만 강력 권장!)
    // stderr 내용을 요약하거나 특정 상황에 대한 안내 메시지
    private String errorMessage;

    // 5. 코드 실행 시간 (성공/실패 무관하게 중요)
    private Long executionTimeMs;
}
