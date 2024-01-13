package com.benefits.gatewayservice.filter;

import com.benefits.gatewayservice.common.spec.Api;
import com.benefits.gatewayservice.common.spec.Result;
import com.benefits.gatewayservice.token.ifs.TokenHelperIfs;
import com.benefits.gatewayservice.token.resultcode.ResultCodeIfs;
import com.benefits.gatewayservice.token.resultcode.TokenResultCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthSuperHeaderFilter implements GatewayFilter {

    private final ObjectMapper objectMapper;
    private final TokenHelperIfs tokenHelperIfs;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
            return onError(exchange,
                    TokenResultCode.AUTHORIZATION_TOKEN_NOT_FOUND, HttpStatus.UNAUTHORIZED);
//            throw new ApiException(TokenResultCode.AUTHORIZATION_TOKEN_NOT_FOUND);
        }



        var authorizationHeader =
                Objects.requireNonNull(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);

        var jwt = authorizationHeader.replace("Bearer", "");

        //request.getMethod()
        //request.getURI()

        // token validation return
        var jwtValid = tokenHelperIfs.validationToken(jwt, List.of("SUPERVISOR"));

        if(!jwtValid.equals(TokenResultCode.OK)){
            return onError(exchange, jwtValid, HttpStatus.UNAUTHORIZED);
        }

        return chain.filter(exchange);
    }


    // WebFlux 방식 비동기 처리 반환 단위 단일 값 Mono, 단일 값 아닌 것 Flux
    private Mono<Void> onError(ServerWebExchange exchange, ResultCodeIfs jwtValid, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse(); // 비동기 방식 이고 response에서 servlet 방식 사용 하지 않음

        response.setStatusCode(httpStatus);

        var spec = Api.builder()
                .result(Result.builder()
                        .resultCode(jwtValid.getHttpStatusCode())
                        .resultMessage(httpStatus.getReasonPhrase())
                        .resultDescription("코드(" + jwtValid.getResultCode() + ") - " + jwtValid.getMessage())
                        .build())
                .build();

        var resultApiSpec = "";
        try {
            resultApiSpec = objectMapper.writeValueAsString(spec);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        byte[] bytes = resultApiSpec.getBytes(StandardCharsets.UTF_8);

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

        return response.writeWith(Flux.just(buffer));
        /*

         */
//        return response.setComplete();
    }

}
