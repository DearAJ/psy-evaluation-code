//package org.ww.config;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.web.reactive.function.server.RequestPredicates;
//import org.springframework.web.reactive.function.server.RouterFunction;
//import org.springframework.web.reactive.function.server.RouterFunctions;
//import org.springframework.web.server.WebFilter;
//import org.ww.handler.HystrixFallbackHandler;
//
//@Configuration
//@AllArgsConstructor
//@Slf4j
//public class RouterFunctionConfiguration {
//
//    private final HystrixFallbackHandler hystrixFallbackHandler;
//
//    @Bean
//    public RouterFunction routerFunction() {
//
//        return RouterFunctions.route(
//                RequestPredicates.path("/fallback")
//                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), hystrixFallbackHandler);
//    }
//    @Bean
//    WebFilter dataNotFoundToBadRequest() {
//        return (exchange, next) -> next.filter(exchange)
//                .onErrorResume(Exception.class, e -> {
//                    Exception exception = exchange.getAttribute(ServerWebExchangeUtils.HYSTRIX_EXECUTION_EXCEPTION_ATTR);
//                    log.error("error {}",exception);
//                    ServerHttpResponse response = exchange.getResponse();
//                    response.setStatusCode(HttpStatus.BAD_REQUEST);
//                    return response.setComplete();
//                });
//    }
//}