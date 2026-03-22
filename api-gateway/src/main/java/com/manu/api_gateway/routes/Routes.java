package com.manu.api_gateway.routes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> productServiceRoutes() {
        return route("product_service")
                .route(path("/api/product"), http())
                .before(uri("http://localhost:8080"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoutes() {
        return route("order_service")
                .route(path("/api/order"), http())
                .before(uri("http://localhost:8081"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoutes() {
        return route("inventory_service")
                .route(path("/api/inventory"), http())
                .before(uri("http://localhost:8082"))
                .build();
    }
}