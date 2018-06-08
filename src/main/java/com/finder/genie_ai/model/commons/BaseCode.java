package com.finder.genie_ai.model.commons;

public enum BaseCode {
    SUCCESS(200, "OK"),
    BAD_REQUEST(400, "잘못된 파라미터 요청"),
    DUPLICATED(409, "중복된 데이터 존재"),
    SERVER_ERROR(500, "서버 에러");

    private final int code;
    private final String message;

    BaseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMeesage() {
        return message;
    }
}
