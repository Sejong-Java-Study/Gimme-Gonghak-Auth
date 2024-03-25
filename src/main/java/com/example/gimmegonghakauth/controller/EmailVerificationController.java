package com.example.gimmegonghakauth.controller;

import com.example.gimmegonghakauth.dto.UserJoinDto;
import com.example.gimmegonghakauth.service.EmailVerificationService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class EmailVerificationController {


    private final String UNIV_NAME = "세종대학교";

    @Autowired
    private EmailVerificationService emailVerificationService;

    @PostMapping("/send-verification-email")
    public String sendVerificationEmail(@RequestBody UserJoinDto userJoinDto) {
        String email = userJoinDto.getEmail();
        boolean univCheck = true; // 대학 재학 여부 확인 여부

        return emailVerificationService.sendVerificationEmail(email, UNIV_NAME, univCheck);
    }

    @PostMapping("/verify-code")
    public String verifyEmailCode(@RequestBody UserJoinDto userJoinDto) {

        String email = userJoinDto.getEmail();
        int code = userJoinDto.getVerifyCode(); // 입력된 인증번호

        return emailVerificationService.verifyEmailCode(email, UNIV_NAME, code);
    }

    @PostMapping("/verify-status")
    public String verifyStatus(@RequestBody UserJoinDto userJoinDto) {
        String email = userJoinDto.getEmail();

        return emailVerificationService.verifyStatus(email);
    }

    @PostMapping("/certification/clear")
    public String clearCertification(@RequestBody Map<String, String> requestData) {
        String email = requestData.get("email");

        return emailVerificationService.clearCertification(email);
    }

}

