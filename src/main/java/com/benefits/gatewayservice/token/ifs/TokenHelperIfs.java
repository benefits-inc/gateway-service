package com.benefits.gatewayservice.token.ifs;

import com.benefits.gatewayservice.token.resultcode.ResultCodeIfs;

import java.util.List;

public interface TokenHelperIfs {
    //    ResultCodeIfs validationToken(String token);
    ResultCodeIfs validationToken(String token, List<String> roles);
}
