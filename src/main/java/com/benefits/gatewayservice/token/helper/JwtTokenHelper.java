package com.benefits.gatewayservice.token.helper;

import com.benefits.gatewayservice.token.resultcode.ResultCodeIfs;
import com.benefits.gatewayservice.token.resultcode.TokenResultCode;
import com.benefits.gatewayservice.token.ifs.TokenHelperIfs;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Primary
@RequiredArgsConstructor
public class JwtTokenHelper implements TokenHelperIfs {

    // @Value("${token.secret.key}")
    private final Environment env;


    @Override
    public ResultCodeIfs validationToken(String token) {
        var secretKey = Objects.requireNonNull(env.getProperty("token.secret.key"));

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
