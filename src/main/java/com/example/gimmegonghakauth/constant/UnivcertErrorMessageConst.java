package com.example.gimmegonghakauth.constant;

public enum UnivcertErrorMessageConst {

    SEND_MAIL_SUCCESS("인증메일 발송에 성공했습니다."),
    SEND_MAIL_COMPLETED("이미 인증된 메일입니다. 인증번호 확인없이 회원가입이 가능합니다."),
    SEND_MAIL_SEJONG("세종대학교 학생 이메일(@sju.ac.kr)로 인증해주세요."),
    VERIFY_CODE_SUCCESS("인증번호 확인에 성공했습니다."),
    VERIFY_CODE_FAIL("인증번호가 일치하지 않습니다."),
    VERIFY_CODE_COMPLETED("이미 인증된 메일입니다."),
    VERIFY_CODE_SEJONG("세종대학교 학생 이메일(@sju.ac.kr)로 인증해주세요."),
    VERIFY_STATUS_SUCCESS("인증된 이메일입니다."),

    VERIFY_STATUS_FAIL("인증되지않은 이메일입니다."),
    VERIFY_STATUS_NULL("먼저 이메일 인증을 완료해주세요."),
    CLEAR_CERTIFICATION_SUCCESS("초기화되었습니다."),
    CLEAR_CERTIFICATION_NULL("이메일 정보가 존재하지않습니다."),
    UNEXPECTED_ERROR("에러 발생!");

    private final String errorMessage;

    UnivcertErrorMessageConst(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
