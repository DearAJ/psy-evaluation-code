package org.ww.interceptor;

import com.alibaba.fastjson.JSON;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.ww.domain.TelGroup;
import org.ww.service.HotlineCallService;
import org.ww.utils.JWTUtils;
import org.ww.vo.UserVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HotlineInterceptor implements HandlerInterceptor {

    public static final int TYPE_SEAT = 1;
    public static final int TYPE_ADMIN = 2;
    public static final int TYPE_BOTH = 3;

    public static final String PARAM_USERNAME = "P_UNAME";

    public static final String PARAM_TELGROUP = "P_TGROUP";
    public static final String PARAM_TELSDK_ID = "P_TSDKAPPID";

    @Autowired
    private HotlineCallService hotlineCallService;
    private int type;
    public HotlineInterceptor(int type)
    {
        this.type = type;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        SignedJWT signedJWT = JWTUtils.getSignedJWT(token);
        Object payload = signedJWT.getJWTClaimsSet().getClaim("payload");
        UserVo userVo = JSON.parseObject(payload.toString(), UserVo.class);
        String username = userVo.getUsername();
        request.setAttribute(PARAM_USERNAME, username);
        TelGroup group = null;
        switch (type){
            case TYPE_SEAT:
                group = hotlineCallService.findTelGroupByUsername(username);
                break;
            case TYPE_ADMIN:
                group = hotlineCallService.findTelGroupByAdmin(username);
                break;
            case TYPE_BOTH:
                group = hotlineCallService.findTelGroupByUsernameOrAdmin(username);
                break;
        }

        if(group == null)
        {
            throw new RuntimeException("非法操作");
        }

        request.setAttribute(PARAM_TELGROUP, group.getId());
        request.setAttribute(PARAM_TELSDK_ID, group.getSdkAppId());

        return true;
    }
}
