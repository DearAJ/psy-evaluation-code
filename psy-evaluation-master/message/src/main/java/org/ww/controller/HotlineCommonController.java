package org.ww.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.ww.domain.TelCommonCode;
import org.ww.domain.TelGroup;
import org.ww.domain.TelTeacher;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.HotlineCallService;
import org.ww.service.HotlineCommonCodeService;
import org.ww.service.HotlineTeacherService;
import org.ww.service.RegionService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/hotline")
public class HotlineCommonController extends BaseController {

    private final HotlineCallService hotlineCallService;

    private final HotlineCommonCodeService hotlineCommonCodeService;

    private final HotlineTeacherService hotlineTeacherService;

    private final RegionService regionService;

//    private final
    public HotlineCommonController(HotlineCallService hotlineCallService,
                                   HotlineCommonCodeService hotlineCommonCodeService,
                                   HotlineTeacherService hotlineTeacherService,
                                   RegionService regionService)
    {
        this.hotlineCallService = hotlineCallService;
        this.hotlineCommonCodeService = hotlineCommonCodeService;
        this.hotlineTeacherService = hotlineTeacherService;
        this.regionService = regionService;
    }


    @PostMapping("/genAccount")
    public Wrapper<Object> genAccount(@RequestBody JSONObject info)
    {

        TelGroup telGroup = info.toJavaObject(TelGroup.class);

        if(telGroup == null || telGroup.getAdmin() == null)
        {
            return WrapMapper.error();
        }

        String username = hotlineCallService.generateAccount(telGroup);
        return WrapMapper.ok(username);
    }

    @GetMapping("/listCodes")
    public Wrapper<Object> listCodes(String pageName)
    {
        List<TelCommonCode> codes = hotlineCommonCodeService.listByPage(pageName);

        Map result = codes.stream().collect(Collectors.groupingBy(TelCommonCode::getGroupName));
        if("hotline_record_frm".equals(pageName))
        {
            // 热线信息记录页
            IPage<TelTeacher> teacherIPage = hotlineTeacherService.list(null, currentTelGroupId(), 1, Integer.MAX_VALUE);
            result.put("teachers", teacherIPage.getRecords());

            result.put("regions", regionService.allRegionTree());
        }

        return WrapMapper.ok(result);
    }



}
