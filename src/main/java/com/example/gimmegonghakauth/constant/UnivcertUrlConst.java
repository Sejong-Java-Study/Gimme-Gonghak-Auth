package com.example.gimmegonghakauth.constant;

public enum UnivcertUrlConst {

    SEND_MAIL("https://univcert.com/api/v1/certify"),
    VERIFY_CODE("https://univcert.com/api/v1/certifycode"),
    VERIFY_STATUS("https://univcert.com/api/v1/status"),
    CLEAR_CERTIFICATION("https://univcert.com/api/v1/clear/");
    private final String url;

    UnivcertUrlConst(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
