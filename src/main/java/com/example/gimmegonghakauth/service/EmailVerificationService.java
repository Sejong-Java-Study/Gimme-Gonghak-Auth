package com.example.gimmegonghakauth.service;

import com.example.gimmegonghakauth.constant.UnivCertTypeConst;
import com.example.gimmegonghakauth.constant.UnivcertErrorMessageConst;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.univcert.api.UnivCert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationService {

    @Value("${univcert.apikey}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String sendVerificationEmail(String email, String univName, boolean univCheck) {

        try {
            String response = objectMapper.writeValueAsString(
                UnivCert.certify(apiKey, email, univName, univCheck));
            if (response.contains("\"success\":true")) {
                return UnivcertErrorMessageConst.SEND_MAIL_SUCCESS.getErrorMessage();
            } else {
                return handleBadRequestException(response, UnivCertTypeConst.SEND_MAIL.getType());
            }
        } catch (Exception e) {
            return UnivcertErrorMessageConst.UNEXPECTED_ERROR.getErrorMessage();
        }
    }

    public String verifyEmailCode(String email, String univName, int code) {
        try {
            String response = objectMapper.writeValueAsString(
                UnivCert.certifyCode(apiKey, email, univName, code));
            if (response.contains("\"success\":true")) {
                return UnivcertErrorMessageConst.VERIFY_CODE_SUCCESS.getErrorMessage();
            } else {
                return handleBadRequestException(response, UnivCertTypeConst.VERIFY_CODE.getType());
            }
        } catch (Exception e) {
            return UnivcertErrorMessageConst.UNEXPECTED_ERROR.getErrorMessage();
        }
    }

    public String verifyStatus(String email) {
        try {
            String response = objectMapper.writeValueAsString(UnivCert.status(apiKey, email));
            if (response.contains("\"success\":true")) {
                return UnivcertErrorMessageConst.VERIFY_STATUS_SUCCESS.getErrorMessage();
            } else {
                return handleBadRequestException(response,
                    UnivCertTypeConst.VERIFY_STATUS.getType());
            }
        } catch (Exception e) {
            return UnivcertErrorMessageConst.UNEXPECTED_ERROR.getErrorMessage();
        }
    }

    public String clearCertification(String email) {
        try {
            String response = objectMapper.writeValueAsString(UnivCert.clear(apiKey, email));
            if (response.contains("\"success\":true")) {
                return UnivcertErrorMessageConst.CLEAR_CERTIFICATION_SUCCESS.getErrorMessage();
            } else {
                return handleBadRequestException(response,
                    UnivCertTypeConst.CLEAR_CERTIFICATION.getType());
            }
        } catch (Exception e) {
            // 기타 에러 처리

            return UnivcertErrorMessageConst.UNEXPECTED_ERROR.getErrorMessage();
        }
    }

    private String handleBadRequestException(String response, String type) {
        String errorMessage;

        switch (type) {
            case "이메일 발송":
                if (response.contains("이미 완료된 요청입니다.")) {
                    errorMessage = UnivcertErrorMessageConst.SEND_MAIL_COMPLETED.getErrorMessage();
                    break;
                } else if (response.contains("대학과 일치하지 않는 메일 도메인입니다.")) {
                    errorMessage = UnivcertErrorMessageConst.SEND_MAIL_SEJONG.getErrorMessage();
                    break;
                } else {
                    errorMessage = UnivcertErrorMessageConst.UNEXPECTED_ERROR.getErrorMessage();
                    break;
                }
            case "이메일 코드 인증":
                if (response.contains("이미 완료된 요청입니다.")) {
                    errorMessage = UnivcertErrorMessageConst.VERIFY_CODE_COMPLETED.getErrorMessage();
                    break;
                } else if (response.contains("대학과 일치하지 않는 메일 도메인입니다.")) {
                    errorMessage = UnivcertErrorMessageConst.VERIFY_CODE_SEJONG.getErrorMessage();
                    break;
                } else if (response.contains("일치하지 않는 인증코드입니다.")) {
                    errorMessage = UnivcertErrorMessageConst.VERIFY_CODE_FAIL.getErrorMessage();
                    break;
                } else {
                    errorMessage = UnivcertErrorMessageConst.UNEXPECTED_ERROR.getErrorMessage();
                    break;
                }
            case "인증 확인":
                if (response.contains("인증 요청 이력이 존재하지 않습니다.")) {
                    errorMessage = UnivcertErrorMessageConst.VERIFY_STATUS_FAIL.getErrorMessage();
                    break;
                } else if (response.contains("인증되지 않은 메일입니다.")) {
                    errorMessage = UnivcertErrorMessageConst.VERIFY_STATUS_NULL.getErrorMessage();
                    break;
                } else {
                    errorMessage = UnivcertErrorMessageConst.UNEXPECTED_ERROR.getErrorMessage();
                    break;
                }
            case "인증 초기화":
                if (response.contains("인증 요청 이력이 존재하지 않습니다.")) {
                    errorMessage = UnivcertErrorMessageConst.CLEAR_CERTIFICATION_NULL.getErrorMessage();
                    break;
                } else {
                    errorMessage = UnivcertErrorMessageConst.UNEXPECTED_ERROR.getErrorMessage();
                    break;
                }
            default:
                errorMessage = UnivcertErrorMessageConst.UNEXPECTED_ERROR.getErrorMessage();
                break;
        }
        return errorMessage;
    }
}
