package org.ww.job;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ww.domain.TelCdrInfo;
import org.ww.dto.TelCdrDto;
import org.ww.service.HotlineCallService;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;

@Slf4j
@Component
public class DownloadTelRecordJob {

    private HotlineCallService hotlineCallService;

    @Value("${hotline.download.tel-record:/opt/psy-data/tel/record}")
    private String basePath;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMM");

    public DownloadTelRecordJob(HotlineCallService hotlineCallService)
    {
        this.hotlineCallService = hotlineCallService;
    }
    @Scheduled(fixedDelay = 30 * 1000)
    public void doJob() throws Exception
    {
        TelCdrInfo cdr = hotlineCallService.getTelRecordToDownload();
        if(cdr == null)
        {
            return;
        }

        log.info("downloading: " + cdr.getRecordUrl());

        String subDir = df.format(cdr.getTime());
        File dir = new File(basePath, subDir);
        if(!dir.exists())
        {
            dir.mkdirs();
        }
        File toDownload = new File(dir, cdr.getSessionId() + ".mp3");
        FileUtils.copyURLToFile(new URL(cdr.getRecordUrl()), toDownload);

        hotlineCallService.updateTelCdrLocalPath(cdr.getId(), subDir + "/" + toDownload.getName());

    }
}
