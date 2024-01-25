package org.ww.controller;

import com.alibaba.fastjson.JSON;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.ww.interceptor.HotlineInterceptor;
import org.ww.utils.JWTUtils;
import org.ww.vo.UserVo;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    protected HttpServletRequest request;

    protected String currentUser()
    {

        try {
            String token = request.getHeader("Authorization");
            SignedJWT signedJWT = JWTUtils.getSignedJWT(token);
            Object payload = signedJWT.getJWTClaimsSet().getClaim("payload");
            UserVo userVo = JSON.parseObject(payload.toString(), UserVo.class);
            String username = userVo.getUsername();

            return username;
        }catch (Exception e)
        {
            throw new RuntimeException("非法操作");
        }

    }

    protected Long currentTelGroupId()
    {
        return (Long) request.getAttribute(HotlineInterceptor.PARAM_TELGROUP);
    }

    protected String currentUser2()
    {
        return (String) request.getAttribute(HotlineInterceptor.PARAM_USERNAME);
    }
}
