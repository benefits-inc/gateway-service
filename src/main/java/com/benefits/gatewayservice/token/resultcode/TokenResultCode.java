package com.benefits.gatewayservice.token.resultcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum TokenResultCode implements ResultCodeIfs{
    // 외부, 내부, description
    OK(HttpStatus.OK.value(), 2000, "유효 토큰"),
    AUTHORIZATION_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED.value(), 4000, "인증 헤더 토큰 없음"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), 4001, "유효 하지 않은 토큰"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(), 4002, "만료된 토큰"),
    TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), 4003, "유효 하지 않은 토큰 - 예외"),
    SUBJECT_EMPTY(HttpStatus.UNAUTHORIZED.value(), 4004, "SUBJECT is empty"),
    INVALID_PAYLOAD(HttpStatus.UNAUTHORIZED.value(), 4005, "페이로드 검증 에러"),

    ;

    private final Integer httpStatusCode;
    private final Integer resultCode;
    private final String message;
}
