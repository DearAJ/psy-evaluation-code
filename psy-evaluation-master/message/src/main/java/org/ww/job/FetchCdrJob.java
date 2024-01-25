package org.ww.job;

import com.alibaba.fastjson.JSON;
import com.tencentcloudapi.ccc.v20200210.CccClient;
import com.tencentcloudapi.ccc.v20200210.models.*;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ww.controller.HotlineCallController;
import org.ww.domain.TelGroup;
import org.ww.service.HotlineCallService;

import java.util.Date;
import java.util.List;


@Slf4j
@Component
public class FetchCdrJob {

    private static final long PAGE_SIZE = 100;
    private HotlineCallService hotlineCallService;

    public FetchCdrJob(HotlineCallService hotlineCallService)
    {
        this.hotlineCallService = hotlineCallService;
    }

    @Scheduled(fixedDelay = 30 * 1000)
    public void doJob()
    {
        log.info("fetching cdrs...");
        TelGroup telGroup = hotlineCallService.findOldestCdrProceed();
        Long maxTimestamp = 0l;
        if(telGroup != null)
        {
            try {
                Credential cred = new Credential(telGroup.getSecretId(), telGroup.getSecretKey());
                CccClient client = new CccClient(cred, HotlineCallController.TCCC_DEFAULT_REGION);
                Date now = new Date();
                long startPageNum = 0;
                long totalPage = 0;
                do{
                    // 实例化一个请求对象,每个接口都会对应一个request对象
                    DescribeTelCdrRequest req = new DescribeTelCdrRequest();
                    req.setSdkAppId(Long.valueOf(telGroup.getSdkAppId()));
                    req.setStartTimeStamp(telGroup.getCdrLastFetchTimestamp() == null ? 0 : telGroup.getCdrLastFetchTimestamp().getTime() / 1000 - 1);
                    req.setEndTimeStamp(now.getTime() / 1000);
                    req.setPageSize(PAGE_SIZE);
                    req.setPageNumber(startPageNum);
                    // 返回的resp是一个DescribeTelCdrResponse的实例，与请求对象对应
                    DescribeTelCdrResponse resp = client.DescribeTelCdr(req);

                    // Math.ceil
                    totalPage = (resp.getTotalCount() + PAGE_SIZE - 1) / PAGE_SIZE;
                    for(TelCdrInfo raw : resp.getTelCdrs())
                    {

                        // 把腾讯返回的对象转化为系统的对象
                        org.ww.domain.TelCdrInfo telCdrInfo = new org.ww.domain.TelCdrInfo();
                        BeanUtils.copyProperties(raw, telCdrInfo);
                        telCdrInfo.setTelGroupId(telGroup.getId());
                        telCdrInfo.setSdkAppId(telGroup.getSdkAppId());
                        telCdrInfo.setSource(2);
                        telCdrInfo.setProcessStatus(1);
                        telCdrInfo.setTime(unixTimeStamp2Date(raw.getTime()));
                        telCdrInfo.setDirection(raw.getDirection().intValue());
                        telCdrInfo.setDuration(raw.getDuration().intValue());
                        telCdrInfo.setRecordUrl(raw.getRecordURL());
                        telCdrInfo.setSeatUserId(raw.getSeatUser() == null ? null : raw.getSeatUser().getUserId());
                        telCdrInfo.setEndStatus(raw.getEndStatus() == null ? null: raw.getEndStatus().intValue());
                        telCdrInfo.setIvrDuration(raw.getIVRDuration() == null ? 0 : raw.getIVRDuration().intValue());
                        telCdrInfo.setRingTimestamp(unixTimeStamp2Date(raw.getRingTimestamp()));
                        telCdrInfo.setAcceptTimestamp(unixTimeStamp2Date(raw.getAcceptTimestamp()));
                        telCdrInfo.setEndedTimestamp(unixTimeStamp2Date(raw.getEndedTimestamp()));
                        telCdrInfo.setSkillGroupId(raw.getSkillGroupId() == null ? null : raw.getSkillGroupId().intValue());
                        telCdrInfo.setStartTimestamp(unixTimeStamp2Date(raw.getStartTimestamp()));
                        telCdrInfo.setQueuedTimestamp(unixTimeStamp2Date(raw.getQueuedTimestamp()));
                        telCdrInfo.setIvrKeyPressed(formatIVRPath(raw.getIVRKeyPressedEx()));
                        telCdrInfo.setPostIvrKeyPressed(formatIVRPath(raw.getPostIVRKeyPressed()));

                        maxTimestamp = Math.max(maxTimestamp, raw.getTime());

                        hotlineCallService.checkToAddCdr(telCdrInfo);
                    }

                    startPageNum++;
                    Thread.sleep(1000);
                }while(startPageNum < totalPage);

                // update caller callCount;
                hotlineCallService.updateTelCdrCallerCount(telGroup.getId());

                if(maxTimestamp > 0)
                {
                    hotlineCallService.updateTelGroupCdrTimestamp(telGroup.getId(), unixTimeStamp2Date(maxTimestamp));
                }

            } catch (TencentCloudSDKException e) {
                log.error("error1 --> " + e.getMessage());
            }catch (InterruptedException e)
            {
                log.error("error2 --> " + e.getMessage());
            }
        }

        log.debug("ending");
    }

    private String formatIVRPath(IVRKeyPressedElement[] postIVRKeyPressed) {
        if(postIVRKeyPressed == null)
        {
            return null;
        }

        return JSON.toJSONString(postIVRKeyPressed);
    }

    private Date unixTimeStamp2Date(Long time)
    {
        if(time == null || time < 1)
        {
            return null;
        }
        return new Date(time * 1000);
    }
}
