package com.example.gimmegonghakauth.constant;

public enum UnivCertTypeConst {
    SEND_MAIL("이메일 발송"),
    VERIFY_CODE("이메일 코드 인증"),
    VERIFY_STATUS("인증 확인"),
    CLEAR_CERTIFICATION("인증 초기화");
    private final String type;

    UnivCertTypeConst(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
