package org.ww.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ww.domain.TelBlacklist;
import org.ww.domain.TelContact;
import org.ww.dto.BlacklistDto;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.HotlineBlacklistService;
import org.ww.service.HotlineCallService;
import org.ww.service.HotlineContactService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/hotline/blacklist")
public class HotlineBlacklistController extends BaseController{
    private final HotlineCallService hotlineCallService;

    private final HotlineBlacklistService hotlineBlacklistService;

    private final HotlineContactService hotlineContactService;

    public HotlineBlacklistController(HotlineCallService hotlineCallService,
                                 HotlineBlacklistService hotlineBlacklistService,
                                      HotlineContactService hotlineContactService)
    {
        this.hotlineCallService = hotlineCallService;
        this.hotlineBlacklistService = hotlineBlacklistService;
        this.hotlineContactService = hotlineContactService;
    }

    @RequestMapping("/isBlocked")
    public Wrapper<Object> isBlocked(String caller)
    {
        if(StringUtils.isBlank(caller))
        {
            return WrapMapper.error();
        }

        Long telGroupId = currentTelGroupId();
        int ret = hotlineBlacklistService.isBlocked(telGroupId, caller);
        return WrapMapper.ok(ret);
    }

    @RequestMapping("/list")
    public Wrapper<Object> list(Integer pageNum, Integer pageSize)
    {
        Long telGroupId = currentTelGroupId();

        pageNum = (pageNum == null ? 1 : pageNum);
        pageSize = (pageSize == null ? Integer.MAX_VALUE : pageSize);

        return WrapMapper.ok(hotlineBlacklistService.list(telGroupId, pageNum, pageSize));
    }

    @PostMapping("/add")
    public Wrapper<Object> add(@RequestBody TelBlacklist black)
    {
        if(StringUtils.isBlank(black.getPhone()))
        {
            return WrapMapper.error("请输入【phone】参数");
        }

        black.setDeleted(0);
        black.setCreateUsername(currentUser2());
        black.setTelGroupId(currentTelGroupId());

        return WrapMapper.ok(hotlineBlacklistService.checkToAdd(black));
    }

    @RequestMapping("/remove")
    public Wrapper<Object> remove(String phone)
    {
        if(StringUtils.isBlank(phone))
        {
            return WrapMapper.error("请输入【phone】参数");
        }
        return WrapMapper.ok(hotlineBlacklistService.removeByPhone(phone, currentTelGroupId()));
    }

    @RequestMapping("/details")
    public Wrapper<Object> details(String phone)
    {
        if(StringUtils.isBlank(phone))
        {
            return WrapMapper.error("请输入【phone】参数");
        }

        Long telGroupId = currentTelGroupId();

        BlacklistDto blacklist = hotlineBlacklistService.findDetailsByPhone(telGroupId, phone);

        return WrapMapper.ok(blacklist);

    }
}
