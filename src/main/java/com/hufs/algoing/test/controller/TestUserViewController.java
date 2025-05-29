package com.hufs.algoing.test.controller;

import com.hufs.algoing.global.oauth.PrincipalDetails;
import com.hufs.algoing.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestUserViewController {
//    @GetMapping("/login")
//    public String testLogin() {
//        return "redirect:/test/login.html";
//    }
//    @GetMapping("/signup")
//    public String testSignUp() {
//        return "redirect:/test/signup.html";
//    }
//    @GetMapping("/")
//    public String testIndex() {
//        return "redirect:/test/index.html";
//    }
//
//    @GetMapping("/loginsuccess")
//    public String testLoginSuccess() {
//        return "redirect:/test/loginsuccess.html";
//    }
//    @GetMapping("/loginerror")
//    public String testLoginError() {
//        return "redirect:/test/loginerror.html";
//    }
//
//
//    @GetMapping("/submit/{problemId}")
//    public String testSubmit(@PathVariable Long problemId) {
//        return "redirect:/test/submitting.html?problemId=" + problemId;
//    }

    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public @ResponseBody String test(Authentication authentication, @AuthenticationPrincipal PrincipalDetails p) {

        UserDetails authDetails = (UserDetails) authentication.getPrincipal();

        System.out.println("PrincipalDetails id test: " + p.getUsername());
        System.out.println("(userDetails)authenticationOAuth id test: " + authDetails.getUsername());

        return "test";

    }

    @GetMapping("/test2")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println(principalDetails.getUser());
        return "user";
    }

    @GetMapping("/test3")
    public @ResponseBody String user2(@AuthenticationPrincipal PrincipalDetails p) throws Exception {
        System.out.println(p.getUsername());
        System.out.println(p.getUser().getEmail());
        System.out.println(p.getUser().getHandle());
        System.out.println(p.getUser().getBojId());
        System.out.println(p.getUser().getBojPassword());
        System.out.println(userService.decrypt(p.getUser().getBojPassword()));
        return "user2";
    }


}
