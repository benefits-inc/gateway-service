package com.benefits.gatewayservice.config;

import com.benefits.gatewayservice.common.spec.Api;
import com.benefits.gatewayservice.filter.AuthSellerHeaderFilter;
import com.benefits.gatewayservice.filter.AuthSuperHeaderFilter;
import com.benefits.gatewayservice.filter.AuthUserHeaderFilter;
//import com.benefits.gatewayservice.filter.AuthorizationHeaderFilter;
import com.benefits.gatewayservice.token.model.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class GatewayRouterConfig {

    //private final AuthorizationHeaderFilter authorizationHeaderFilter;
    private final AuthSellerHeaderFilter authSellerHeaderFilter; // seller, super
    private final AuthUserHeaderFilter authUserHeaderFilter; // user, super
    private final AuthSuperHeaderFilter authSuperHeaderFilter; // super
    private final ObjectMapper objectMapper;


    @Bean
    public RouteLocator benefitsRouteLocator(RouteLocatorBuilder builder){
        return builder.routes()
                // user-service SWAGGER
                .route(
                        it -> it
                                .path("/user-service/swagger-ui.html", "/user-service/swagger-ui/**")
                                .filters( f -> f
                                        .rewritePath("/user-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://user-service")
                )
                // /v3/api-docs/** 앞에 prefix 유지
                .route(
                        it -> it
                                .path( "/user-service/v3/api-docs/**")
                                .uri("lb://user-service")
                )
                // user-service API
                .route(
                        it -> it
                                .path("/user-service/open-api/**")
                                .filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/user-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://user-service")
                )
                .route(
                        it -> it
                                .path("/user-service/open-api/logout")
                                .filters( f -> f
                                        // .removeRequestHeader("Cookie")
                                        .rewritePath("/user-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://user-service")
                )
                .route(
                        it -> it
                                //.path("/user-service/restore","/user-service/actuator/**")
                                .path("/user-service/actuator/**")
                                .and()
                                .method("GET", "POST")
                                .filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/user-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://user-service")
                )
                .route(
                        it -> it.path("/user-service/auth-user/restore").
                                filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/user-service/(?<segment>.*)","/${segment}")
                                        .filter(authUserHeaderFilter)
                                        .modifyResponseBody(String.class, Object.class, MediaType.APPLICATION_JSON_VALUE,
                                                (exchange, string) -> {
                                                    try {
                                                        if(exchange.getResponse().getStatusCode().equals(HttpStatus.CREATED)){
                                                            var response = objectMapper.readValue(string, TokenResponse.class);
                                                            return Mono.just(response);
                                                        }
                                                        var response = objectMapper.readValue(string, Api.class);
                                                        return Mono.just(response);
                                                    } catch (JsonProcessingException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                })
                                ).
                                uri("lb://user-service")
                )
                .route(
                        it -> it.path("/user-service/auth-user/**").
                                filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/user-service/(?<segment>.*)","/${segment}")
                                        .filter(authUserHeaderFilter)
                                        .modifyResponseBody(String.class, Api.class, MediaType.APPLICATION_JSON_VALUE,
                                                (exchange, string) -> {
                                                    try {
                                                        var response = objectMapper.readValue(string, Api.class);
                                                        return Mono.just(response);
                                                    } catch (JsonProcessingException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                })
                                ).
                                uri("lb://user-service")
                )
                .route(
                        it -> it.path("/user-service/auth-super/**").
                                filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/user-service/(?<segment>.*)","/${segment}")
                                        .filter(authSuperHeaderFilter) // 관리자용 필터로 변경
                                        .modifyResponseBody(String.class, Api.class, MediaType.APPLICATION_JSON_VALUE,
                                                (exchange, string) -> {
                                                    try {
                                                        var response = objectMapper.readValue(string, Api.class);
                                                        return Mono.just(response);
                                                    } catch (JsonProcessingException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                })
                                ).
                                uri("lb://user-service")
                )
                // order-service SWAGGER
                .route(
                        it -> it
                                .path("/order-service/swagger-ui.html", "/order-service/swagger-ui/**")
                                .filters( f -> f
                                        .rewritePath("/order-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://order-service")
                )
                .route(
                        it -> it
                                .path( "/order-service/v3/api-docs/**")
                                .uri("lb://order-service")
                )
                // order-service
                .route(
                        it -> it
                                .path("/order-service/actuator/**")
                                .and()
                                .method("GET", "POST")
                                .filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/order-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://order-service")
                )
                .route(
                        it -> it.path("/order-service/auth-user/**").
                                filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/order-service/(?<segment>.*)","/${segment}")
                                        .filter(authUserHeaderFilter)
                                        .modifyResponseBody(String.class, Api.class, MediaType.APPLICATION_JSON_VALUE,
                                                (exchange, string) -> {
                                                    try {
                                                        var response = objectMapper.readValue(string, Api.class);
                                                        return Mono.just(response);
                                                    } catch (JsonProcessingException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                })
                                ).
                                uri("lb://order-service")
                )
                .route(
                        it -> it.path("/order-service/auth-seller/**").
                                filters( f -> f
                                        .rewritePath("/order-service/(?<segment>.*)","/${segment}")
                                        .filter(authSellerHeaderFilter)
                                        .modifyResponseBody(String.class, Api.class, MediaType.APPLICATION_JSON_VALUE,
                                                (exchange, string) -> {
                                                    try {
                                                        var response = objectMapper.readValue(string, Api.class);
                                                        return Mono.just(response);
                                                    } catch (JsonProcessingException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                })
                                ).
                                uri("lb://order-service")
                )
                .route(
                        it -> it.path("/order-service/auth-super/**").
                                filters( f -> f
                                        .rewritePath("/order-service/(?<segment>.*)","/${segment}")
                                        .filter(authSuperHeaderFilter)
                                        .modifyResponseBody(String.class, Api.class, MediaType.APPLICATION_JSON_VALUE,
                                                (exchange, string) -> {
                                                    try {
                                                        var response = objectMapper.readValue(string, Api.class);
                                                        return Mono.just(response);
                                                    } catch (JsonProcessingException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                })
                                ).
                                uri("lb://order-service")
                )
                // product-service SWAGGER
                .route(
                        it -> it
                                .path("/product-service/swagger-ui.html", "/product-service/swagger-ui/**")
                                .filters( f -> f
                                        .rewritePath("/product-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://product-service")
                )
                .route(
                        it -> it
                                .path( "/product-service/v3/api-docs/**")
                                .uri("lb://product-service")
                )
                // product-service
                .route(
                        it -> it
                                .path("/product-service/actuator/**")
                                .and()
                                .method("GET", "POST")
                                .filters( f -> f
                                        .rewritePath("/product-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://product-service")
                )
                .route(
                        it -> it
                                .path("/product-service/open-api/**")
                                .and()
                                .method("GET", "POST")
                                .filters( f -> f
                                        .rewritePath("/product-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://product-service")
                )
                .route(
                        it -> it.path("/product-service/auth-seller/**").
                                filters( f -> f
                                        .rewritePath("/product-service/(?<segment>.*)","/${segment}")
                                        .filter(authSellerHeaderFilter) // seller, supervisor
                                        .modifyResponseBody(String.class, Api.class, MediaType.APPLICATION_JSON_VALUE,
                                                (exchange, string) -> {
                                                    try {
                                                        var response = objectMapper.readValue(string, Api.class);
                                                        return Mono.just(response);
                                                    } catch (JsonProcessingException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                })
                                ).
                                uri("lb://product-service")
                )
                .route(
                        it -> it.path("/product-service/auth-super/**").
                                filters( f -> f
                                        .rewritePath("/product-service/(?<segment>.*)","/${segment}")
                                        .filter(authSuperHeaderFilter) // supervisor
                                        .modifyResponseBody(String.class, Api.class, MediaType.APPLICATION_JSON_VALUE,
                                                (exchange, string) -> {
                                                    try {
                                                        var response = objectMapper.readValue(string, Api.class);
                                                        return Mono.just(response);
                                                    } catch (JsonProcessingException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                })
                                ).
                                uri("lb://product-service")
                )
                // review-service SWAGGER
                .route(
                        it -> it
                                .path("/review-service/swagger-ui.html", "/review-service/swagger-ui/**")
                                .filters( f -> f
                                        .rewritePath("/review-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://review-service")
                )
                .route(
                        it -> it
                                .path( "/review-service/v3/api-docs/**")
                                .uri("lb://review-service")
                )
                // review-service
                .route(
                        it -> it
                                .path("/review-service/actuator/**")
                                .and()
                                .method("GET", "POST")
                                .filters( f -> f
                                        .rewritePath("/review-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://review-service")
                )
                .route(
                        it -> it
                                .path("/review-service/open-api/**")
                                .filters( f -> f
                                        .rewritePath("/review-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://review-service")
                )
                .route(
                        it -> it
                                .path("/review-service/auth-user/**")
                                .filters( f -> f
                                        .rewritePath("/review-service/(?<segment>.*)","/${segment}")
                                        .filter(authUserHeaderFilter)
                                        .modifyResponseBody(String.class, Api.class, MediaType.APPLICATION_JSON_VALUE,
                                                (exchange, string) -> {
                                                    try {
                                                        var response = objectMapper.readValue(string, Api.class);
                                                        return Mono.just(response);
                                                    } catch (JsonProcessingException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                })
                                )
                                .uri("lb://review-service")
                )
                .route(
                        it -> it.path("/review-service/auth-super/**").
                                filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/review-service/(?<segment>.*)","/${segment}")
                                        .filter(authSuperHeaderFilter) // 관리자용 검증으로 변경
                                        .modifyResponseBody(String.class, Api.class, MediaType.APPLICATION_JSON_VALUE,
                                                (exchange, string) -> {
                                                    try {
                                                        var response = objectMapper.readValue(string, Api.class);
                                                        return Mono.just(response);
                                                    } catch (JsonProcessingException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                })
                                ).
                                uri("lb://review-service")
                )
                // seller-service SWAGGER
                .route(
                        it -> it
                                .path("/seller-service/swagger-ui.html", "/seller-service/swagger-ui/**")
                                .filters( f -> f
                                        .rewritePath("/seller-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://seller-service")
                )
                .route(
                        it -> it
                                .path( "/seller-service/v3/api-docs/**")
                                .uri("lb://seller-service")
                )
                // seller-service
                .route(
                        it -> it
                                .path("/seller-service/login")
                                .and()
                                .method("POST")
                                .filters( f -> f
                                        .rewritePath("/seller-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://seller-service")
                )
                .route(
                        it -> it
                                .path("/seller-service/restore","/seller-service/actuator/**")
                                .and()
                                .method("GET", "POST")
                                .filters( f -> f
                                        .rewritePath("/seller-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://seller-service")
                )
                .route(
                        it -> it.path("/seller-service/auth-super/**").
                                filters( f -> f
                                        .rewritePath("/seller-service/(?<segment>.*)","/${segment}")
                                        .filter(authSuperHeaderFilter) // seller 등록, 수정은 오직 관리자만
                                        .modifyResponseBody(String.class, Api.class, MediaType.APPLICATION_JSON_VALUE,
                                                (exchange, string) -> {
                                                    try {
                                                        var response = objectMapper.readValue(string, Api.class);
                                                        return Mono.just(response);
                                                    } catch (JsonProcessingException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                })
                                ).
                                uri("lb://seller-service")
                )
                // supervisor-service SWAGGER
                .route(
                        it -> it
                                .path("/supervisor-service/swagger-ui.html", "/supervisor-service/swagger-ui/**")
                                .filters( f -> f
                                        .rewritePath("/supervisor-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://supervisor-service")
                )
                .route(
                        it -> it
                                .path( "/supervisor-service/v3/api-docs/**")
                                .uri("lb://supervisor-service")
                )
                // supervisor-service
                .route(
                        it -> it
                                .path("/supervisor-service/login")
                                .and()
                                .method("POST")
                                .filters( f -> f
                                        .rewritePath("/supervisor-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://supervisor-service")
                )
                .route(
                        it -> it
                                .path("/supervisor-service/restore","/supervisor-service/actuator/**")
                                .and()
                                .method("GET", "POST")
                                .filters( f -> f
                                        .rewritePath("/supervisor-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://supervisor-service")
                )
                .route(
                        it -> it.path("/supervisor-service/auth-super/**").
                                filters( f -> f
                                        .rewritePath("/supervisor-service/(?<segment>.*)","/${segment}")
                                        .filter(authSuperHeaderFilter) // test@gmail.com 테스트 수퍼바이저 계정 제공
                                        .modifyResponseBody(String.class, Api.class, MediaType.APPLICATION_JSON_VALUE,
                                                (exchange, string) -> {
                                                    try {
                                                        var response = objectMapper.readValue(string, Api.class);
                                                        return Mono.just(response);
                                                    } catch (JsonProcessingException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                })
                                ).
                                uri("lb://supervisor-service")
                )
                .build();
    }
}
