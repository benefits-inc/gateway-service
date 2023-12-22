package com.benefits.gatewayservice.token.resultcode;

public interface ResultCodeIfs {
    Integer getHttpStatusCode();
    Integer getResultCode();
    String getMessage();
}
