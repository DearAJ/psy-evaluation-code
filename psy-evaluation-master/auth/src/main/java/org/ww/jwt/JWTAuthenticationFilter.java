package org.ww.jwt;

import com.alibaba.fastjson.JSONObject;
import org.ww.domain.AuthUser;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.constants.JWTConstants;
import org.ww.constants.RedisConstants;
import org.ww.enums.AuthEnum;
import org.ww.utils.JWTUtils;
import org.ww.utils.Md5Utils;
import org.ww.utils.RSAUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private StringRedisTemplate redisTemplate;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, StringRedisTemplate redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        // password解密
        String decryptPassword = RSAUtils.decrypt(password);
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, decryptPassword));
    }

    @SneakyThrows
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        AuthUser user = (AuthUser) authResult.getPrincipal();

        // 生成token
        String payload = JSONObject.toJSONString(user);
        String jwtToken = JWTUtils.createToken(payload);

        // 生成Key， 把权限放入到redis中
        String keyPrefix = RedisConstants.TOKEN_KEY_PREFIX + user.getId() + ":";
        String keySuffix = Md5Utils.getMD5(jwtToken.getBytes());
        String key = keyPrefix + keySuffix;
        String authKey = key + RedisConstants.AUTH_KEY;

        redisTemplate.opsForValue().set(key, jwtToken, JWTConstants.EXPIRE_TIME, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(authKey, JSONObject.toJSONString(user.getAuthorities()), JWTConstants.EXPIRE_TIME, TimeUnit.SECONDS);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JSONObject.toJSONString(WrapMapper.wrap(200, "登录成功").result(jwtToken)));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.error("========登录认证失败:", failed);
        Wrapper result = null;
        int status = AuthEnum.AUTH_PASS_ERROR.getKey();
        if (failed instanceof UsernameNotFoundException) {
            result = WrapMapper.error(AuthEnum.AUTH_NONEXISTENT.getKey(), AuthEnum.AUTH_NONEXISTENT.getValue());
        } else if (failed instanceof BadCredentialsException) {
            result = WrapMapper.error(AuthEnum.AUTH_PASS_ERROR.getKey(), AuthEnum.AUTH_PASS_ERROR.getValue());
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.getWriter().write(JSONObject.toJSONString(result));
    }

}
