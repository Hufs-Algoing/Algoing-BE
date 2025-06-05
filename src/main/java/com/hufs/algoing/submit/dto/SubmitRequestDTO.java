package com.hufs.algoing.submit.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitRequestDTO {
    @Schema(description = "제출 사용자 ID", example = "1")
    private Long userId;
    @Schema(description = "제출 문제 번호", example = "1000")
    private Long problemNum;
    @Schema(description = "제출 코드 언어 ('Java 11', 'Python 3', 'C++17')")
    private String language;
    @Schema(description = "제출 코드 본문", example = "import java.util.*;\n\nclass Solution {\n    public int solution(int a, int b) {\n        return a + b;\n    }\n}")
    private String code;
    @Schema(description = "이 제출이 특정 추천 세션을 통해 발생했다면 해당 세션 ID (UUID)", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890", nullable = true)
    private String recommendationSessionId;
}
