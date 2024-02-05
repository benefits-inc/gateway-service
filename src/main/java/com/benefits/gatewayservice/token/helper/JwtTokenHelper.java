package com.benefits.gatewayservice.token.helper;

import com.benefits.gatewayservice.token.payload.Payload;
import com.benefits.gatewayservice.token.resultcode.ResultCodeIfs;
import com.benefits.gatewayservice.token.resultcode.TokenResultCode;
import com.benefits.gatewayservice.token.ifs.TokenHelperIfs;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
public class JwtTokenHelper implements TokenHelperIfs {

    // @Value("${token.secret.key}")
    private final Environment env;
    private final RedisTemplate<String, String> redisTemplate;


    @Override
    public ResultCodeIfs validationToken(String token, List<String> roles) {

        var redisToken = Optional.ofNullable(redisTemplate.opsForValue().get(token));
        if(redisToken.isPresent()){
            return TokenResultCode.EXPIRED_TOKEN;
        }

        var token_payload = token.split("\\.")[1];
        byte[] decodedPayloadByte = Base64.getDecoder().decode(token_payload);

        Payload payload = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            payload = objectMapper.readValue(decodedPayloadByte, Payload.class);
        } catch (IOException e) {
            return TokenResultCode.INVALID_PAYLOAD;
        }

        if(payload.getRole() == null){
            return TokenResultCode.INVALID_PAYLOAD;
        }

        if( ! roles.contains(payload.getRole()) ){
            return TokenResultCode.INVALID_PAYLOAD;
        }

        var secretKey = "";
        switch (payload.getRole()) {
            case "USER" -> secretKey = Objects.requireNonNull(env.getProperty("token.secret.user.key"));
            case "SELLER" -> secretKey = Objects.requireNonNull(env.getProperty("token.secret.seller.key"));
            case "SUPERVISOR" -> secretKey = Objects.requireNonNull(env.getProperty("token.secret.supervisor.key"));
            default -> {
                return TokenResultCode.INVALID_TOKEN;
            }
        }
        //var secretKey = Objects.requireNonNull(env.getProperty("token.secret.user.key"));

        var key = Keys.hmacShaKeyFor(secretKey.getBytes());

        var parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();

        try {
            var result = parser.parseClaimsJws(token);
            var subject = result.getBody().getSubject();

            if(subject.isEmpty()){
                return TokenResultCode.SUBJECT_EMPTY;
            }

            return TokenResultCode.OK;

        }catch (Exception e){
            if(e instanceof SignatureException){
                // 토큰이 유효하지 않을 때
                return TokenResultCode.INVALID_TOKEN;
            } else if (e instanceof ExpiredJwtException) {
                // 만료된 토큰
                return TokenResultCode.EXPIRED_TOKEN;
            }else {
                // 그 외 예외
                return TokenResultCode.TOKEN_EXCEPTION;
            }
        }
    }
}
