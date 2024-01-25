package org.ww.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ww.EntityDO.PsyIntervationFileDO;
import org.ww.EntityDO.PsyIntervationPlanDO;
import org.ww.EntityDO.PsyIntervationRecordDO;
import org.ww.EntityDO.entityFactory.EntityFactory;
import org.ww.EntityVO.IntervationSchemePageVO;
import org.ww.EntityVO.IntervationStatisticVO;
import org.ww.base.BaseEntity;
import org.ww.mapper.IntervationStatisticMapper;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.impl.TaskServiceImpl;

import java.util.List;

/**
 * @Author lqk
 * @Date 2022/2/10 22:40
 * @Version 1.0
 */

@Slf4j
@RestController
@RequestMapping( "/psyIntervationTest")
public class TestController {
//    private final TaskServiceImpl taskService;
//    @Autowired
//    public TestController(TaskServiceImpl taskService) {
//        this.taskService = taskService;
//    }
//
//    @GetMapping( "readPsyFilesByStuId")
//    public Wrapper<List<BaseEntity>> readPsyFilesByStuId() {
//        List<BaseEntity> res = taskService.readPsyFilesByStuId(3L);
//        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
//    }
//
//
//    @GetMapping("getAllRecordByStuId")
//    public Wrapper<IPage<PsyIntervationRecordDO>> GetAllRecordByStuId(){
//        IPage res =  taskService.getAllRecordByStuId(160L,1,10);
//        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
//    }
//
//    @GetMapping("getAllPlanByStuId")
//    public Wrapper<IPage<PsyIntervationRecordDO>> GetAllPlanByStuId(){
//        IPage res =  taskService.getAllPlanByStuId(160L,1,10);
//        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
//    }
//
//    @GetMapping( "getSchemePageData")
//    public Wrapper<List<BaseEntity>> getSchemePageData() {
//        List<BaseEntity> res = taskService.getSchemePageData(1,10);
//        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
//    }
//
//    @GetMapping( value = "getSchemePageData2")
//    public Wrapper<IPage<BaseEntity>> getSchemePageData2() {
//        IntervationSchemePageVO p = IntervationSchemePageVO.builder().
//                stuClass("1班").stuGrade("一年级").pageNum(1).pageSize(10).build();
//        IPage<BaseEntity> res = taskService.getSchemePageData(p);
//        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
//    }
//
//
//
//    @GetMapping( value = "getIntervationStaRes")
//    public Wrapper<List<BaseEntity>> getIntervationStaRes() {
//        IntervationStatisticVO statisticVO = new IntervationStatisticVO();
//        statisticVO.setPeriod("初中");
//        List res = taskService.getIntervationStaRes(statisticVO);
//        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
//    }
//
//    @PostMapping(value = "createIntervationSchemeItem")
//    public Wrapper<String> createIntervationSchemeItem() {
//
//        PsyIntervationFileDO fileDO = EntityFactory.getBaseFileDO(1L,169L,1L);
//
//        try {
//            Integer res = taskService.createIntervationSchemeItem(fileDO);
//            if(res < 0){
//                return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE,res.toString());
//            }else {
//                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, "");
//            }
//        }catch (Exception e){
//            return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE,e.getMessage());
//        }
//
//
//
//    }
//
//    @PostMapping(value = "addIntervationRecord")
//    public Wrapper<String> addIntervationRecord() {
//
//        PsyIntervationRecordDO recordDO = EntityFactory.getTestRecordDO();
//        Long fileId = 3L;
//        Long userId = 271L;
//        try {
//            taskService.addIntervationRecord(userId,fileId, recordDO);
//        }catch (Exception e){
//            return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE,e.getMessage());
//        }
//        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,"");
//
//    }
//
//    @PostMapping(value = "addIntervationPlan")
//    public Wrapper<String> addIntervationPlan() {
//
//        PsyIntervationPlanDO planDO = EntityFactory.getTestPlanDO();
//        Long fileId = 4L;
//        Long userId = 3L;
//        try {
//            taskService.addIntervationPlan(userId, fileId, planDO);
//            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, "");
//        }catch (Exception e){
//            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, e.getMessage());
//        }
//
//    }

}
