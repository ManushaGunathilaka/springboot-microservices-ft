package com.manu.api_gateway.routes;

import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class Routes {

    // =========================
    // PRODUCT SERVICE
    // =========================
    @Bean
    public RouterFunction<ServerResponse> productServiceRoutes() {
        return route("product_service")
                .route(path("/api/product"), http())
                .before(uri("http://localhost:8080"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "productServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    // =========================
    // ORDER SERVICE
    // =========================
    @Bean
    public RouterFunction<ServerResponse> orderServiceRoutes() {
        return route("order_service")
                .route(path("/api/order"), http())
                .before(uri("http://localhost:8081"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "orderServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    // =========================
    // INVENTORY SERVICE
    // =========================
    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoutes() {
        return route("inventory_service")
                .route(path("/api/inventory"), http())
                .before(uri("http://localhost:8082"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "inventoryServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    // =========================
    // SWAGGER ROUTES (WITH CIRCUIT BREAKER)
    // =========================

    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
        return route("product_service_swagger")
                .route(path("/aggregate/product-service/v3/api-docs"), http())
                .before(uri("http://localhost:8080"))
                .before(setPath("/v3/api-docs"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "productSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {
        return route("order_service_swagger")
                .route(path("/aggregate/order-service/v3/api-docs"), http())
                .before(uri("http://localhost:8081"))
                .before(setPath("/v3/api-docs"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "orderSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute() {
        return route("inventory_service_swagger")
                .route(path("/aggregate/inventory-service/v3/api-docs"), http())
                .before(uri("http://localhost:8082"))
                .before(setPath("/v3/api-docs"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "inventorySwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    // =========================
    // FALLBACK ROUTE
    // =========================
    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return route("fallback_route")
                .GET("/fallbackRoute", request ->
                        ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("Service is currently unavailable. Please try again later."))
                .build();
    }
}