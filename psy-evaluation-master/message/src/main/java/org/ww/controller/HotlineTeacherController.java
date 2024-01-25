package org.ww.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ww.domain.TelTeacher;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.HotlineTeacherService;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/hotline/teacher")
public class HotlineTeacherController extends BaseController{


    private HotlineTeacherService hotlineTeacherService;

    public HotlineTeacherController(HotlineTeacherService hotlineTeacherService)
    {
        this.hotlineTeacherService = hotlineTeacherService;
    }

//    @RequestMapping("/list")
//    public Wrapper<Object> list(Integer pageNum, Integer pageSize)
//    {
//        Long telGroupId = currentTelGroupId();
//
//        pageNum = (pageNum == null ? 1 : pageNum);
//        pageSize = (pageSize == null ? Integer.MAX_VALUE : pageSize);
//
//        return WrapMapper.ok(hotlineTeacherService.list(telGroupId, pageNum, pageSize));
//    }

    @RequestMapping("/list")
    public Wrapper<Object> listDetails(Long teacherId, Integer pageNum, Integer pageSize)
    {
        Long telGroupId = currentTelGroupId();

        pageNum = (pageNum == null ? 1 : pageNum);
        pageSize = (pageSize == null ? Integer.MAX_VALUE : pageSize);

        return WrapMapper.ok(hotlineTeacherService.listDetails(teacherId, telGroupId, pageNum, pageSize));
    }

    @PostMapping("/add")
    public Wrapper<Object> add(@RequestBody TelTeacher teacher)
    {
        teacher.setTelGroupId(currentTelGroupId());
        teacher.setCreateBy(currentUser2());
        teacher.setCreateTime(new Date());
        teacher.setDeleted(0);

        return WrapMapper.ok(hotlineTeacherService.add(teacher));
    }

    @RequestMapping("/remove")
    public Wrapper<Object> remove(Long id)
    {
        return WrapMapper.ok(hotlineTeacherService.remove(currentTelGroupId(), id));
    }

    @PostMapping("/update")
    public Wrapper<Object> update(@RequestBody TelTeacher teacher)
    {
        return WrapMapper.ok(hotlineTeacherService.update(currentTelGroupId(), teacher));
    }


}
