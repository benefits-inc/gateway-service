package com.benefits.gatewayservice.config;

import com.benefits.gatewayservice.filter.AuthorizationHeaderFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayRouterConfig {

    private final AuthorizationHeaderFilter authorizationHeaderFilter;


    @Bean
    public RouteLocator benefitsRouteLocator(RouteLocatorBuilder builder){
        return builder.routes()
                // user-service
                .route(
                        it -> it
                                .path("/user-service/login", "/user-service/users")
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
                        it -> it.path("/user-service/**").
                                filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/user-service/(?<segment>.*)","/${segment}")
                                                .filter(authorizationHeaderFilter)
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
                        it -> it.path("/order-service/**").
                                filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/order-service/(?<segment>.*)","/${segment}")
                                        .filter(authorizationHeaderFilter)
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
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/product-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://product-service")
                )
                .route(
                        it -> it.path("/product-service/**").
                                filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/product-service/(?<segment>.*)","/${segment}")
                                        .filter(authorizationHeaderFilter)
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
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/review-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://review-service")
                )
                .route(
                        it -> it.path("/review-service/**").
                                filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/review-service/(?<segment>.*)","/${segment}")
                                        .filter(authorizationHeaderFilter)
                                ).
                                uri("lb://review-service")
                )
                // manager-service
                .route(
                        it -> it
                                .path("/manager-service/actuator/**")
                                .and()
                                .method("GET", "POST")
                                .filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/manager-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://manager-service")
                )
                .route(
                        it -> it.path("/manager-service/**").
                                filters( f -> f
                                        //.removeRequestHeader("Cookie")
                                        .rewritePath("/manager-service/(?<segment>.*)","/${segment}")
                                        .filter(authorizationHeaderFilter)
                                ).
                                uri("lb://manager-service")
                )
                .build();
    }


}
