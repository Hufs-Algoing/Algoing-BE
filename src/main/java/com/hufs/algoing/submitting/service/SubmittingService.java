package com.hufs.algoing.submitting.service;

import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.problem.entity.ProblemStatus;
import com.hufs.algoing.problem.entity.SubmittedProblem;
import com.hufs.algoing.problem.repository.ProblemRepository;
import com.hufs.algoing.problem.repository.SubmittedProblemRepository;
import com.hufs.algoing.user.entity.User;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubmittingService {
    private WebDriver d;

    @Autowired
    private SubmittedProblemRepository submittedProblemRepository;
    @Autowired
    private ProblemRepository problemRepository;

    public SubmittingService() {

    }

    public ProblemStatus submit(Long problemId, String code, String language, @AuthenticationPrincipal User p) throws InterruptedException {

        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--user-data-dir=/tmp/chrome_profile_" + p.getUserId());
        d = new ChromeDriver(options);

        try {
            d.get("https://www.acmicpc.net/submit/" + problemId);
            Thread.sleep(1000);
//            자동로그인 로직 임시입니다!!!! 사용하려면 일단 자기 ID/PW 입력하세요
            if (d.getPageSource().contains("login")) {
                d.findElement(By.name("login_user_id")).sendKeys("***");
                d.findElement(By.name("login_password")).sendKeys("***");
                d.findElement(By.id("submit_button")).click();
                Thread.sleep(1000);
            }

            d.findElement(By.id("language_chosen")).click();
            List<WebElement> langs = d.findElements(By.cssSelector(".chosen-results li"));
            for (WebElement lang : langs) {
                if (lang.getText().equals(language)) {
                    lang.click();
                    break;
                }
            }

//        이 부분은 테스트 상 단순 텍스트상자를 이용한 것이므로, 실제 적용시 에디터에 맞게  조정해야함.
            WebElement codeMirror = d.findElement(By.className("CodeMirror-line"));
            codeMirror.click();
            Thread.sleep(2000);
            ((JavascriptExecutor) d).executeScript(
                    "document.querySelector('.CodeMirror').CodeMirror.setValue(arguments[0]);", code
            );
            Thread.sleep(1000);

            d.findElement(By.id("submit_button")).click();
            Thread.sleep(2000);

            while (d.findElement(By.cssSelector("#status-table tbody tr:first-child .result span")).getText().contains("채점 중")) {
                Thread.sleep(1000); // 1초 대기
            }

            String resultText = d.findElement(By.cssSelector("#status-table tbody tr:first-child .result span")).getText();
            ProblemStatus status;
            if (resultText.equals("맞았습니다!!")) {
                status = ProblemStatus.SOLVED;
            } else if (resultText.equals("틀렸습니다")) {
                status = ProblemStatus.FAILED;
            } else {
                status = ProblemStatus.ERROR;
            }

            SubmittedProblem submission = SubmittedProblem.builder()
                    .userId(p)  // 인증된 사용자 객체
                    .problemId((Problem) problemRepository.findByProblemId(problemId).orElseThrow())  // 문제 ID
                    .answer(code)
                    .language(language)
                    .status(status)
                    .submittedAt(LocalDateTime.now())
                    .submittedDate(LocalDate.now())
                    .build();

            submittedProblemRepository.save(submission);

            return status;
        }finally{
            d.quit();
        }
    }
}
