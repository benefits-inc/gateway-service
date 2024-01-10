package com.benefits.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
public class WebFilterCorsConfig {

    private static final String ALLOWED_HEADERS = "x-requested-with, authorization, access_token, Content-Type";
    private static final String ALLOWED_METHODS = "GET, PUT, POST, DELETE, OPTIONS"; // PATCH 는 현재 사용안하고 있으니 제외

    // 브라우저의 cors 정책 gateway 에서 front origin(http://localhost:3001) 허용
    // 각 서비스에서는 게이트웨이 origin(http://localhost:8000, http://localhost:3001) 만 허용
    private static final String ALLOWED_ORIGIN = "http://localhost:3001";
    private static final String MAX_AGE = "3600";

    // true 로 설정할 경우 allowed_origin 에 '*' 을 입력할 수 없고,
    //http://localhost:3000 이렇게 특정해줘야 한다.
    private static final String ALLOWED_CREDENTIALS = "true";


    @Bean
    public WebFilter corsFilter() {

        return (ServerWebExchange ctx, WebFilterChain chain) -> {

            ServerHttpRequest request = ctx.getRequest();

            if (CorsUtils.isPreFlightRequest(request)) {
                ServerHttpResponse response = ctx.getResponse();
                HttpHeaders headers = response.getHeaders();
                // 권장하지 않지만 게이트웨이에서 모든 origin 을 와일드카드 *로 허용해도 각 서비스에서 게이트웨이만 허용하기 때문에
                // 사용하려면 각 서비스에서도 패턴을 *로 맞춰줘야 됨 .allowedOriginPatterns(*)
                // headers.add("Access-Control-Allow-Origin", "*");
                headers.add("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
                headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);
                headers.add("Access-Control-Max-Age", MAX_AGE);
                headers.add("Access-Control-Allow-Headers",ALLOWED_HEADERS);
                headers.add("Access-Control-Allow-Credentials",ALLOWED_CREDENTIALS);
                //headers.setAccessControlExposeHeaders();

                // 요청 메소드가 preflight 이면 허용
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }
            return chain.filter(ctx);
        };
    }
}