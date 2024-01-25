package org.ww.config;/**
 * @author liuyue
 * @date 10/22/2022 22:53
 */

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;

/**
 * @ClassName FallbackController
 * @Description TODO
 * @Author liuyu
 * @Date 10/22/2022 22:53
 * @Version 1.0
 **/

@Slf4j
@RestController
public class FallbackController {

    @RequestMapping(value = "/fallback")
    public HashMap defaultFallBack(ServerWebExchange exchange) {
        log.warn("服务降级...{}", Objects.toString(exchange.getAttribute(ServerWebExchangeUtils.HYSTRIX_EXECUTION_EXCEPTION_ATTR)));
        LinkedHashSet originalUris = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);

        Exception exception = exchange.getAttribute(ServerWebExchangeUtils.HYSTRIX_EXECUTION_EXCEPTION_ATTR);
        log.error("1 hystrix error {} {}",originalUris, exception);


        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "您的操作太频繁，请稍后重试");
        response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return response;

    }

    @Bean
    WebFilter dataNotFoundToBadRequest() {
        return (exchange, next) -> next.filter(exchange)
                .onErrorResume(Exception.class, e -> {
                    LinkedHashSet originalUris = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);

                    Exception exception = exchange.getAttribute(ServerWebExchangeUtils.HYSTRIX_EXECUTION_EXCEPTION_ATTR);
                    log.error("2 hystrix error {} {}",originalUris, exception);
                    ServerHttpResponse response = exchange.getResponse();
//                    response.setStatusCode(HttpStatus.BAD_REQUEST);
//                    return response.setComplete();

                    HashMap<String, Object> res = new HashMap<>();
                    res.put("message", "您的操作太频繁，请稍后重试");
                    res.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
//                    ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                            .contentType(MediaType.APPLICATION_JSON_UTF8)
//                            .body(BodyInserters.fromObject(res));
                    return response.setComplete();
                });
    }

}
