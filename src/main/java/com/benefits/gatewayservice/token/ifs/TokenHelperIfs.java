package com.benefits.gatewayservice.token.ifs;

import com.benefits.gatewayservice.token.resultcode.ResultCodeIfs;

public interface TokenHelperIfs {
    ResultCodeIfs validationToken(String token);
}
