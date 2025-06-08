package com.hufs.algoing.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode{

    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4001", "사용자가 없습니다."),
    HINT_NOT_FOUND(HttpStatus.BAD_REQUEST,"HINT4001","힌트가 없습니다."),
    SNAPSHOT_NOT_FOUND(HttpStatus.BAD_REQUEST,"SNAPSHOT4001","해당 유저의 스냅샷이 없습니다."),
    PROBLEM_NOT_FOUND(HttpStatus.BAD_REQUEST, "PROBLEM4001","해당하는 문제가 없습니다." ),
    BOJ_ID_EXISTS(HttpStatus.BAD_REQUEST, "USER4002", "이미 존재하는 BOJ ID입니다."),
    BOJ_ID_NOT_EXISTS(HttpStatus.BAD_REQUEST, "USER4003", "존재하지 않는 BOJ ID입니다."),
    RECOMMENDATION_LOG_NOT_FOUND(HttpStatus.BAD_REQUEST, "LOG4001", "존재하지 않는 추천 로그입니다."),
    USER_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "USER4004", "사용자가 인증되지 않았습니다."),
    NOT_POST_AUTHOR(HttpStatus.UNAUTHORIZED, "POST4001", "게시글 작성자가 아닙니다."),
    POST_NOT_FOUND(HttpStatus.UNAUTHORIZED, "POST4002", "존재하지 않는 게시글입니다."),
    COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "COMMENT4001", "존재하지 않는 댓글입니다."),
    NOT_COMMENT_AUTHOR(HttpStatus.BAD_REQUEST, "COMMENT4002", "댓글 작성자가 아닙니다.");




    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
