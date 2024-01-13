package com.benefits.gatewayservice.config;

import com.benefits.gatewayservice.filter.AuthSellerHeaderFilter;
import com.benefits.gatewayservice.filter.AuthSuperHeaderFilter;
import com.benefits.gatewayservice.filter.AuthUserHeaderFilter;
//import com.benefits.gatewayservice.filter.AuthorizationHeaderFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayRouterConfig {

    //private final AuthorizationHeaderFilter authorizationHeaderFilter;
    private final AuthSellerHeaderFilter authSellerHeaderFilter; // seller, super
    private final AuthUserHeaderFilter authUserHeaderFilter; // user, super
    private final AuthSuperHeaderFilter authSuperHeaderFilter; // super


    @Bean
    public RouteLocator benefitsRouteLocator(RouteLocatorBuilder builder){
        return builder.routes()
                // user-service SWAGGER
                .route(
                        it -> it
                                .path("/user-service/swagger-ui.html", "/user-service/swagger-ui/**", "/v3/api-docs/**")
                                .filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/user-service/(?<segment>.*)","/${segment}")
                                )
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
                                .path("/user-service/login")
                                .and()
                                .method("POST")
                                .filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/user-service/(?<segment>.*)","/${segment}")
                                )

                                .uri("lb://user-service")
                )
                .route(
                        it -> it
                                .path("/user-service/restore","/user-service/actuator/**")
                                .and()
                                .method("GET", "POST")
                                .filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/user-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://user-service")
                )
                .route(
                        it -> it.path("/user-service/auth-api/**").
                                filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/user-service/(?<segment>.*)","/${segment}")
                                        .filter(authUserHeaderFilter)
                                ).
                                uri("lb://user-service")
                )
                .route(
                        it -> it.path("/user-service/**").
                                filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/user-service/(?<segment>.*)","/${segment}")
                                        .filter(authSuperHeaderFilter) // 관리자용 필터로 변경
                                ).
                                uri("lb://user-service")
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
                        it -> it.path("/order-service/auth-api/**").
                                filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/order-service/(?<segment>.*)","/${segment}")
                                        .filter(authUserHeaderFilter)
                                ).
                                uri("lb://order-service")
                )
                .route(
                        it -> it.path("/order-service/**").
                                filters( f -> f
                                        .rewritePath("/order-service/(?<segment>.*)","/${segment}")
                                        .filter(authSuperHeaderFilter)
                                ).
                                uri("lb://order-service")
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
                        it -> it.path("/product-service/**").
                                filters( f -> f
                                        .rewritePath("/product-service/(?<segment>.*)","/${segment}")
                                        .filter(authSellerHeaderFilter) // seller, supervisor
                                ).
                                uri("lb://product-service")
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
                                .path("/review-service/auth-api/**")
                                .filters( f -> f
                                        .rewritePath("/review-service/(?<segment>.*)","/${segment}")
                                        .filter(authUserHeaderFilter)
                                )
                                .uri("lb://review-service")
                )
                .route(
                        it -> it.path("/review-service/**").
                                filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/review-service/(?<segment>.*)","/${segment}")
                                        .filter(authSuperHeaderFilter) // 관리자용 검증으로 변경
                                ).
                                uri("lb://review-service")
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
                        it -> it.path("/seller-service/**").
                                filters( f -> f
                                        .rewritePath("/seller-service/(?<segment>.*)","/${segment}")
                                         .filter(authSuperHeaderFilter) // seller 등록, 수정은 오직 관리자만
                                ).
                                uri("lb://seller-service")
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
                        it -> it.path("/supervisor-service/**").
                                filters( f -> f
                                        .rewritePath("/supervisor-service/(?<segment>.*)","/${segment}")
                                        //.filter(authSuperHeaderFilter)
                                ).
                                uri("lb://supervisor-service")
                )
                .build();
    }


}
