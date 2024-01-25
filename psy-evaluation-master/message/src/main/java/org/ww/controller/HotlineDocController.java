package org.ww.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ww.domain.TelDoc;
import org.ww.domain.TelTeacher;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.HotlineDocService;
import org.ww.service.HotlineTeacherService;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/hotline/doc")
public class HotlineDocController extends BaseController{

    private HotlineDocService hotlineDocService;

    public HotlineDocController(HotlineDocService hotlineDocService)
    {
        this.hotlineDocService = hotlineDocService;
    }

    @PostMapping("/add")
    public Wrapper<Object> add(@RequestBody TelDoc doc)
    {
        doc.setTelGroupId(currentTelGroupId());
        doc.setCreateBy(currentUser2());
        doc.setCreateTime(new Date());
        doc.setDeleted(0);
        if(doc.getPriority() == null)
        {
            doc.setPriority(0);
        }

        return WrapMapper.ok(hotlineDocService.add(doc));
    }

    @RequestMapping("/remove")
    public Wrapper<Object> remove(Long id)
    {
        return WrapMapper.ok(hotlineDocService.remove(currentTelGroupId(), id));
    }

    @RequestMapping("/query")
    public Wrapper<Object> query(String keywords, Integer pageNum, Integer pageSize)
    {
        pageNum = (pageNum == null ? 1 : pageNum);
        pageSize = (pageSize == null ? Integer.MAX_VALUE : pageSize);

        return WrapMapper.ok(hotlineDocService.query(currentTelGroupId(), keywords, pageNum, pageSize));
    }

    @RequestMapping("/details")
    public Wrapper<Object> details(Long id)
    {
        return WrapMapper.ok(hotlineDocService.get(currentTelGroupId(), id));
    }

    @PostMapping("/update")
    public Wrapper<Object> update(@RequestBody TelDoc doc)
    {
        doc.setUpdateBy(currentUser2());
        return WrapMapper.ok(hotlineDocService.update(currentTelGroupId(), doc));
    }

}
