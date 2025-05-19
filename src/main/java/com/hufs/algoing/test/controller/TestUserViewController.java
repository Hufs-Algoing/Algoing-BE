package com.hufs.algoing.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TestUserViewController {
    @GetMapping("/login")
    public String testLogin() {
        return "redirect:/test/login.html";
    }
    @GetMapping("/signup")
    public String testSignUp() {
        return "redirect:/test/signup.html";
    }
    @GetMapping("/")
    public String testIndex() {
        return "redirect:/test/index.html";
    }

    @GetMapping("/loginsuccess")
    public String testLoginSuccess() {
        return "redirect:/test/loginsuccess.html";
    }
    @GetMapping("/loginerror")
    public String testLoginError() {
        return "redirect:/test/loginerror.html";
    }


    @GetMapping("/submit/{problemId}")
    public String testSubmit(@PathVariable Long problemId) {
        return "redirect:/test/submitting.html?problemId=" + problemId;
    }



}
