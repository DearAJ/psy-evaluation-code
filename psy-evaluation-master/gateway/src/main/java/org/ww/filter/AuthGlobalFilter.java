package org.ww.filter;

import com.alibaba.fastjson.JSON;
import org.ww.config.ExclusionUrlConfig;
import org.ww.constants.JWTConstants;
import org.ww.constants.RedisConstants;
import org.ww.enums.AuthEnum;
import org.ww.result.WrapMapper;
import org.ww.utils.JWTUtils;
import org.ww.utils.Md5Utils;
import org.ww.vo.Authority;
import org.ww.vo.UserVo;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.List;

@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private ExclusionUrlConfig exclusionUrlConfig;

    AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String headerToken = request.getHeaders().getFirst(JWTConstants.TOKEN_HEADER);
        log.info("headerToken:{}", headerToken);

        String path = request.getURI().getPath();
        log.info("request path:{}", path);

        // 判断是否是过滤的路径， 是的话就放行
        if (isExclusionUrl(path)) {
            return chain.filter(exchange);
        }

        // 只要带上了token， 就需要判断Token是否有效
        if (!StringUtils.isEmpty(headerToken) && !JWTUtils.verifierToken(headerToken)) {
            return getVoidMono(response, AuthEnum.AUTH_NO_TOKEN.getKey(), AuthEnum.AUTH_NO_TOKEN.getValue());
        }

        // 判断请求的URL是否有权限
//        boolean permission = hasPermission(headerToken, path);
//        if (!permission) {
//            return getVoidMono(response, AuthEnum.AUTH_NO_ACCESS.getKey(), AuthEnum.AUTH_NO_ACCESS.getValue());
//        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<Void> getVoidMono(ServerHttpResponse response, int i, String msg) {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(HttpStatus.OK);
        byte[] bits = JSON.toJSONString(WrapMapper.error(i, msg)).getBytes();
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        return response.writeWith(Mono.just(buffer));
    }

    private boolean isExclusionUrl(String path) {
        List<String> exclusions = exclusionUrlConfig.getUrl();
        if (exclusions.size() == 0) {
            return false;
        }
        return exclusions.stream().anyMatch(action -> antPathMatcher.match(action, path));
    }

    private boolean hasPermission(String headerToken, String path) {
        try {
            if (StringUtils.isEmpty(headerToken)) {
                return false;
            }

            SignedJWT jwt = JWTUtils.getSignedJWT(headerToken);
            Object payload = jwt.getJWTClaimsSet().getClaim("payload");
            UserVo user = JSON.parseObject(payload.toString(), UserVo.class);
            // 生成Key， 把权限放入到redis中
            String keyPrefix = RedisConstants.TOKEN_KEY_PREFIX + user.getId() + ":";
            String token = headerToken.replace(JWTConstants.TOKEN_PREFIX, "");
            String keySuffix = Md5Utils.getMD5(token.getBytes());
            String key = keyPrefix + keySuffix;
            String authKey = key + RedisConstants.AUTH_KEY;

            String authStr = redisTemplate.opsForValue().get(authKey);
            if (StringUtils.isEmpty(authStr)) {
                return false;
            }

            List<Authority> authorities = JSON.parseArray(authStr, Authority.class);
            return authorities.stream().anyMatch(authority -> antPathMatcher.match(authority.getAuthority(), path));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
