package org.ww.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tencentcloudapi.ccc.v20200210.CccClient;
import com.tencentcloudapi.ccc.v20200210.models.CreateSDKLoginTokenRequest;
import com.tencentcloudapi.ccc.v20200210.models.CreateSDKLoginTokenResponse;
import com.tencentcloudapi.ccc.v20200210.models.DescribeCallInMetricsRequest;
import com.tencentcloudapi.ccc.v20200210.models.DescribeCallInMetricsResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ww.domain.TelCdrInfo;
import org.ww.domain.TelContact;
import org.ww.domain.TelGroup;
import org.ww.domain.TelNote;
import org.ww.dto.CallInfoDto;
import org.ww.dto.PrivateMessageDto;
import org.ww.dto.TelCdrDto;
import org.ww.interceptor.HotlineInterceptor;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.HotlineBlacklistService;
import org.ww.service.HotlineCallService;
import org.ww.service.HotlineContactService;
import org.ww.utils.PhoneUtils;

@Slf4j
@RestController
@RequestMapping("/hotline/call")
public class HotlineCallController extends BaseController {

    private final HotlineCallService hotlineCallService;

    private final HotlineBlacklistService hotlineBlacklistService;

    private final HotlineContactService hotlineContactService;

    public HotlineCallController(HotlineCallService hotlineCallService,
                                 HotlineBlacklistService hotlineBlacklistService,
                                         HotlineContactService hotlineContactService)
    {
        this.hotlineCallService = hotlineCallService;
        this.hotlineBlacklistService = hotlineBlacklistService;
        this.hotlineContactService = hotlineContactService;
    }

    public static final String TCCC_DEFAULT_REGION = "ap-shanghai";

    @RequestMapping("/metrics")
    public Wrapper<Object> metrics()
    {
        String username = currentUser();
        TelGroup telGroup = hotlineCallService.findTelGroupByUsername(username);
        if(telGroup == null)
        {
            return WrapMapper.error("该账号无法接打电话");
        }

        try {
            Credential cred = new Credential(telGroup.getSecretId(), telGroup.getSecretKey());
            CccClient client1 = new CccClient(cred, TCCC_DEFAULT_REGION);

            DescribeCallInMetricsRequest request = new DescribeCallInMetricsRequest();
            request.setSdkAppId(Long.valueOf(telGroup.getSdkAppId()));
            request.setEnabledNumber(true);
            request.setEnabledSkillGroup(true);

            DescribeCallInMetricsResponse response = client1.DescribeCallInMetrics(request);
            return WrapMapper.ok(response);
        } catch (TencentCloudSDKException e) {

        }

        return WrapMapper.error();
    }

    @RequestMapping("/sdkUrl")
    public Wrapper<Object> sdkUrl()
    {
        String username = currentUser();
        TelGroup telGroup = hotlineCallService.findTelGroupByUsername(username);
        if(telGroup == null)
        {
            return WrapMapper.error("该账号无法接打电话");
        }

        try {
            Credential cred = new Credential(telGroup.getSecretId(), telGroup.getSecretKey());
            CccClient client1 = new CccClient(cred, TCCC_DEFAULT_REGION);

            CreateSDKLoginTokenRequest request = new CreateSDKLoginTokenRequest();
            request.setSdkAppId(Long.valueOf(telGroup.getSdkAppId()));
            request.setSeatUserId(telGroup.getSeatAccount());

            CreateSDKLoginTokenResponse response = client1.CreateSDKLoginToken(request);
            return WrapMapper.ok(response);
        } catch (TencentCloudSDKException e) {

        }

        return WrapMapper.error();
    }

    @RequestMapping("/lastCall")
    public Wrapper<Object> lastCall(@RequestBody CallInfoDto info)
    {
        if(info == null || StringUtils.isBlank(info.getSessionId()))
        {
            return WrapMapper.error();
        }

        Long telGroupId = (Long) request.getAttribute(HotlineInterceptor.PARAM_TELGROUP);
        String telSdkAppId = (String) request.getAttribute(HotlineInterceptor.PARAM_TELSDK_ID);

        TelCdrInfo telCdrInfo = new TelCdrInfo();
        telCdrInfo.setSessionId(info.getSessionId());
        telCdrInfo.setCaller(info.getCaller());
        telCdrInfo.setCallee(info.getCallee());

        telCdrInfo.setTelGroupId(telGroupId);
        telCdrInfo.setSdkAppId(telSdkAppId);
        telCdrInfo.setSource(1);
        telCdrInfo.setProcessStatus(0);

        if("1".equals("" + info.getDirection()))
        {
            // TODO: 呼出
        }else {
            // 呼入
            telCdrInfo.setDirection(0);
            telCdrInfo.setBlocked(hotlineBlacklistService.isBlocked(telGroupId, info.getCaller()));
            TelCdrInfo newCdr = hotlineCallService.checkToAddCdr(telCdrInfo);
            if(telCdrInfo.getBlocked() == 1)
            {
                // 已在黑名单，不需要返回信息
                return WrapMapper.ok();
            }

            // 查询上次的记录
            CallInfoDto result = hotlineCallService.getLastCallInfo(telCdrInfo);

            result.setTelCdrId(newCdr.getId());
            result.setSessionId(info.getSessionId());
            result.setCaller(info.getCaller());
            result.setCallee(info.getCallee());
            result.setDirection(0);
            result.setBlocked(info.getBlocked());

            return WrapMapper.ok(result);
        }

        return WrapMapper.ok();
    }

    @PostMapping("/note")
    public Wrapper<Object> note(@RequestBody CallInfoDto note)
    {
        if(note == null || StringUtils.isBlank(note.getSessionId()))
        {
            return WrapMapper.error();
        }

        if("1".equals(String.valueOf(note.getDirection())))
        {
            // TODO: 呼出
            return WrapMapper.error();
        }

        Long telGroupId = (Long) request.getAttribute(HotlineInterceptor.PARAM_TELGROUP);

        TelNote telNote = note.getNote();
        if(telNote != null)
        {
            telNote.setPhone(note.getCaller());
            hotlineCallService.saveTelNoteBySessionId(telNote, note.getSessionId());

            TelContact telContact = note.getContact();
            if(telContact == null)
            {
                telContact = new TelContact();
            }

            telContact.setTelGroupId(telGroupId);
            telContact.setPhone(note.getCaller());
            telContact.setLastQuestionType(telNote.getQuestionType());
            telContact.setLastQuestionLevel(telNote.getQuestionLevel());
            hotlineContactService.saveByPhone(telContact);

        }



        return WrapMapper.ok();
    }

    @RequestMapping("/callInHistoryByPhone")
    public Wrapper<Object> callInHistoryByPhone(String caller, Integer pageNum, Integer pageSize)
    {
        if(StringUtils.isBlank(caller))
        {
            return WrapMapper.error("请输入参数【caller】");
        }

        caller = PhoneUtils.toFullPhoneNumber(caller);

        pageNum = (pageNum == null ? 1 : pageNum);
        pageSize = (pageSize == null ? Integer.MAX_VALUE : pageSize);

        IPage<TelCdrDto> result = hotlineCallService.listCallIn(currentTelGroupId(), caller, 1, pageNum, pageSize);

        return WrapMapper.ok(result);
    }

    @RequestMapping("/received")
    public Wrapper<Object> received(Integer pageNum, Integer pageSize)
    {
        Long telGroupId = (Long) request.getAttribute(HotlineInterceptor.PARAM_TELGROUP);

        pageNum = (pageNum == null ? 1 : pageNum);
        pageSize = (pageSize == null ? Integer.MAX_VALUE : pageSize);

        IPage<TelCdrDto> result = hotlineCallService.listCallIn(telGroupId, null, 1, pageNum, pageSize);

        return WrapMapper.ok(result);
    }

    @RequestMapping("/missed")
    public Wrapper<Object> missed(Integer pageNum, Integer pageSize)
    {
        Long telGroupId = (Long) request.getAttribute(HotlineInterceptor.PARAM_TELGROUP);

        pageNum = (pageNum == null ? 1 : pageNum);
        pageSize = (pageSize == null ? Integer.MAX_VALUE : pageSize);

        IPage<TelCdrDto> result = hotlineCallService.listCallIn(telGroupId, null, 0, pageNum, pageSize);

        return WrapMapper.ok(result);
    }


}
