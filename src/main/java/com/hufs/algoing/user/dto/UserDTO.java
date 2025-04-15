package com.hufs.algoing.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String handle;

//    @NotEmpty(message = "이메일을 입력하세요.")
    private String email;

//    @NotEmpty(message = "비밀번호를 입력하세요.")
    private String password;
//
//    @NotEmpty(message = "비밀번호 확인을 입력하세요.")
//    private String passwordConfirm;


}
