package com.example.demo.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    RESOURCE_NOT_FOUND(404, "E001", "해당 객체가 존재하지 않습니다."),
    AUTH_FAIL(400, "E002", "비밀번호가 올바르지 않습니다."),
    AUTH_FORBIDDEN(403, "E003", "권한에 맞지않는 접근입니다.");

    private final int httpcode;
    private final String mycode;
    private final String description;
}
