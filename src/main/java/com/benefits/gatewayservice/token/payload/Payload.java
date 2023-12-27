package com.benefits.gatewayservice.token.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payload {
    private String sub;
    private String role;
    private String email;
    private String exp;
}
