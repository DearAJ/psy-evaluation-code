package org.ww.controller;

import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.ww.domain.TelContact;
import org.ww.dto.StatisticDto;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.HotlineCallService;
import org.ww.service.HotlineContactService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/hotline/statistics")
public class HotlineStatisticsController  extends BaseController{

    private HotlineCallService hotlineCallService;

    private HotlineContactService hotlineContactService;

    public HotlineStatisticsController(HotlineCallService hotlineCallService,
                                       HotlineContactService hotlineContactService)
    {
        this.hotlineCallService = hotlineCallService;
        this.hotlineContactService = hotlineContactService;
    }

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    @RequestMapping("/byContinuousDate")
    public Wrapper<Object> byContinuousDate(@DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, Integer size)
    {
        if(size == null || size <=0)
        {
            size = 10;
        }

        if(startDate == null)
        {
            startDate = new Date();
        }

        return WrapMapper.ok(hotlineCallService.cdrStatisticsByContinuousDate(currentTelGroupId(), startDate, size));

    }

    @RequestMapping("/byDate")
    public Wrapper<Object> byDate(@DateTimeFormat(pattern = "yyyy-MM-dd") Date selectedDate, Integer pageNum, Integer pageSize)
    {
        pageNum = (pageNum == null ? 1 : pageNum);
        pageSize = (pageSize == null ? Integer.MAX_VALUE : pageSize);

        return WrapMapper.ok(hotlineCallService.cdrStatisticsByDate(currentTelGroupId(), selectedDate, pageNum, pageSize));

    }

    @ResponseBody
    @RequestMapping("/exportByDate")
    public Wrapper<Object> exportByDate(HttpServletResponse response) throws IOException
    {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("通话统计", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), StatisticDto.class).sheet("通话统计")
                .doWrite(hotlineCallService.cdrStatisticsByDate(currentTelGroupId(), null, 1, Integer.MAX_VALUE).getRecords());

        return WrapMapper.ok();

    }

    @RequestMapping("/byType")
    public Wrapper<Object> byType()
    {
        return WrapMapper.ok(hotlineCallService.noteStatisticsByType(currentTelGroupId()));
    }

    @RequestMapping("/byLevel")
    public Wrapper<Object> byLevel()
    {
        return WrapMapper.ok(hotlineCallService.noteStatisticsByLevel(currentTelGroupId()));
    }

    @RequestMapping("/byRegion")
    public Wrapper<Object> byRegion()
    {
        return WrapMapper.ok(hotlineContactService.contactStatisticsByRegion(currentTelGroupId()));
    }

}
