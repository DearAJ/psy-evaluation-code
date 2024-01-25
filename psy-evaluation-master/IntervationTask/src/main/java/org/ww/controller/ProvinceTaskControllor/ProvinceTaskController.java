package org.ww.controller.ProvinceTaskControllor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.ww.EntityDO.ProvinceEntityDO.PRIntervationStatisticDO;
import org.ww.EntityDO.PsyIntervationFileDO;
import org.ww.EntityDO.PsyIntervationPlanDO;
import org.ww.EntityDO.PsyIntervationRecordDO;
import org.ww.EntityDO.valueObject.ServiceErrorCode;
import org.ww.EntityVO.FileIdAndStuIdVO;
import org.ww.EntityVO.IntervationSchemePageVO;
import org.ww.EntityVO.IntervationStatisticVO;
import org.ww.EntityVO.ProvinceEntityVO.PRIntervationSchemePageVO;
import org.ww.base.BaseEntity;
import org.ww.controller.IntervationTaskController;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.ProvinceService.PRService;
import org.ww.service.impl.TaskServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lqk
 * @Date 2022/2/10 22:40
 * @Version 1.0
 */

@Slf4j
@RestController
@RequestMapping( "/psyPRIntervationTask")
public class ProvinceTaskController {
    private PRService prService;
    @Autowired
    public void setPRService(PRService prService) {
        this.prService = prService;
    }

    private IntervationTaskController intervationTaskController;
    @Autowired
    public void setTaskControllere(IntervationTaskController intervationTaskController) {
        this.intervationTaskController = intervationTaskController;
    }

    private TaskServiceImpl taskService;
    @Autowired
    public void setTaskControllere(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }


    private Integer checkInput(Object obj, Object... params) {
        for (Object o : params) {
            if (o == null || o.toString() == "") {
                return ServiceErrorCode.NULLPARAM.getCode();
            }
        }
        if (obj == null) {
            return ServiceErrorCode.NULLPARAM.getCode();
        } else if (obj instanceof PsyIntervationPlanDO) {
            PsyIntervationPlanDO planDO = (PsyIntervationPlanDO) obj;
            if (planDO.getIsSubmit() == null
                    || planDO.getConclusion() == null || planDO.getConclusion() == ""
                    || planDO.getIntervationPlanContent() == null || planDO.getIntervationPlanContent() == ""
                    || planDO.getSymptom() == null
                    || planDO.getSymptom() == "") {
                return ServiceErrorCode.NULLPARAM.getCode();
            }
        } else if (obj instanceof PsyIntervationFileDO) {
            PsyIntervationFileDO fileDO = (PsyIntervationFileDO) obj;
            if (fileDO.getTaskId() == null || fileDO.getStudentId() == null || fileDO.getUserId() == null) {
                return ServiceErrorCode.NULLPARAM.getCode();
            }

        } else if (obj instanceof PsyIntervationRecordDO) {
            PsyIntervationRecordDO recordDO = (PsyIntervationRecordDO) obj;
            if (recordDO.getStuState() == null || recordDO.getStuState() == ""
                    || recordDO.getIntervationRecord() == null || recordDO.getIntervationRecord() == ""
                    || recordDO.getIsTransfer() == null
                    || recordDO.getCrisisLevel() == null || recordDO.getCrisisLevel() == ""
                    || recordDO.getPostProcessing() == null || recordDO.getPostProcessing() == ""
            ) {
                return ServiceErrorCode.NULLPARAM.getCode();
            }
        }

        return 0;
    }

 //get类型
/////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 预警库信息页面
     *
     * @param schemePageVO
     * @return
     */
    @RequestMapping(value = "getPRSchemePageDataByCondition")
    public Wrapper<IPage<IntervationSchemePageVO>> getPRSchemePageDataByCondition(@RequestBody PRIntervationSchemePageVO schemePageVO) {
        String mode = schemePageVO.getMode();
        log.info("======================> {}", schemePageVO);
        if("pre".equals(mode)){
            return prService.getPrePRSchemePageData(schemePageVO);
        }else if ("warn".equals(mode)){
            return prService.getWarnPRSchemePageData(schemePageVO);
        }else{
            return prService.getPRSchemePageData(schemePageVO);
        }
    }

    /**
     * 干预统计页面
     *
     * @return
     */
    @GetMapping(value = "getPRIntervationStaRes")
    public Wrapper<IPage<BaseEntity>> getPRIntervationStaRes(@RequestBody IntervationStatisticVO statisticVO) {
        //       IntervationStatisticVO statisticVO = new IntervationStatisticVO();
//        statisticVO.setPeriod("初中");
        Integer start = statisticVO.getPageNum() == null ? 1 : statisticVO.getPageNum();
        Integer size = statisticVO.getPageSize() == null ? 10 : statisticVO.getPageSize();
        IPage<PRIntervationStatisticDO> pageRes = new Page<>(start, size);

        try {
            IPage<BaseEntity> res = prService.getPRIntervationStaRes(pageRes, statisticVO);
//            if (res == null) {
//                return WrapMapper.wrap(ServiceErrorCode.FINDNULL.getCode(), ServiceErrorCode.FINDNULL.getVal(),pageRes);
//            }
//
//            Integer total = res.size();
//            List page = taskService.getSplitListToPage(res,start,size);
//
//            pageRes.setRecords(page);
//            pageRes.setTotal(total);

            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, res);
        }catch (Exception e){
            log.error("ERROR ====> {}", e);
            pageRes.setRecords(new ArrayList<>());
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        }
    }


    /**
     * 记录展示页面
     * @param
     * @param
     * @param
     * @return
     */
    @GetMapping(value = "getAllRecordByStuId")
    public Wrapper<IPage<PsyIntervationRecordDO>> GetAllRecordByStuId(@RequestBody FileIdAndStuIdVO fileIdAndStuIdVO){
        return intervationTaskController.GetAllRecordByStuId(fileIdAndStuIdVO);
    }

    /**
     * 方案展示页面（添加方案后）
     */
    @GetMapping(value="getAllPlanByStuId")
    public Wrapper<IPage<PsyIntervationRecordDO>> GetAllPlanByStuId(@RequestBody FileIdAndStuIdVO fileIdAndStuIdVO){

        return  intervationTaskController.GetAllPlanByStuId(fileIdAndStuIdVO);
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////
//    @PostMapping(value = "addIntervationRecord")
//    public Wrapper<String> addIntervationRecord(@RequestBody PsyIntervationRecordDO recordDO,@RequestBody FileIdAndStuIdVO fileIdAndStuIdVO) {
//
//        return taskController.addIntervationRecord(recordDO,fileIdAndStuIdVO);
//
//    }
//
//    @PostMapping(value = "addIntervationPlan")
//    public Wrapper<String> addIntervationPlan(@RequestBody PsyIntervationPlanDO planDO,@RequestBody FileIdAndStuIdVO fileIdAndStuIdVO) {
//        return taskController.addIntervationPlan(planDO,fileIdAndStuIdVO);
//
//    }



}
