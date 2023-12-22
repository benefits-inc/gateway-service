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
                .route(
                        it -> it
                                .path("/user-service/login", "/user-service/users")
                                .and()
                                .method("POST")
                                .filters( f -> f
                                        .removeRequestHeader("Cookie")
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
//                                        .removeRequestHeader("Cookie")
                                        .rewritePath("/user-service/(?<segment>.*)","/${segment}")
                                )
                                .uri("lb://user-service")
                )
                .route(
                        it -> it.path("/user-service/**").
                                filters( f -> f
                                        .removeRequestHeader("Cookie")
                                        .rewritePath("/user-service/(?<segment>.*)","/${segment}")
                                                .filter(authorizationHeaderFilter)
                                ).
                                uri("lb://user-service")
                )
                .build();
    }


}
