package org.ww.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class UriKeyResolver implements KeyResolver {

    // uri限流
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return Mono.just(exchange.getRequest().getURI().getPath());
//        return Mono.just(exchange.getRequest().getHeaders().get("Authorization").toString());
    }

}
