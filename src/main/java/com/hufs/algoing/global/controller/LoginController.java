package com.hufs.algoing.global.controller;


import com.hufs.algoing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class LoginController {

    private final UserService userService;

}
