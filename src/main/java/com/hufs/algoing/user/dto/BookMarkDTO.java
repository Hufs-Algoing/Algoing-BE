package com.hufs.algoing.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookMarkDTO {
    private Long bookMarkId;
    private Long userId;
    private Long problemId;
    private String title;

    public BookMarkDTO(Long bookMarkId, Long userId, Long problemId, String title) {
        this.bookMarkId = bookMarkId;
        this.userId = userId;
        this.problemId = problemId;
        this.title = title;
    }
}
