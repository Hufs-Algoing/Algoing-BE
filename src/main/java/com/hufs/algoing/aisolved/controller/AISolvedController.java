package com.hufs.algoing.aisolved.controller;

import com.hufs.algoing.global.chatgpt.service.GPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AISolvedController {

    private final GPTService gptService;

    @PostMapping
    public void analyzeAll() {
        gptService.analyzeAllProblems();
    }


}
