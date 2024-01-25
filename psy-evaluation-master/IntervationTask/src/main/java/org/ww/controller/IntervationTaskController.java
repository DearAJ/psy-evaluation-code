package org.ww.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.ww.EntityDO.HotWord;
import org.ww.EntityDO.PsyIntervationFileDO;
import org.ww.EntityDO.PsyIntervationPlanDO;
import org.ww.EntityDO.PsyIntervationRecordDO;
import org.ww.EntityDO.entityFactory.EntityFactory;
import org.ww.EntityDO.valueObject.IntervationRecordType;
import org.ww.EntityDO.valueObject.MsgLevel;
import org.ww.EntityDO.valueObject.ServiceErrorCode;
import org.ww.EntityVO.*;
import org.ww.EntityVO.ProvinceEntityVO.PRIntervationSchemePageVO;
import org.ww.base.BaseEntity;
import org.ww.controller.ProvinceTaskControllor.ProvinceTaskController;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.impl.TaskServiceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Author lqk
 * @Date 2022/2/10 22:40
 * @Version 1.0
 */

@Slf4j
@RestController
@RequestMapping( "/psyIntervationTask")
public class IntervationTaskController {
    private TaskServiceImpl taskService;
    @Autowired
    public IntervationTaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @Autowired
    ProvinceTaskController provinceTaskController;

    // TODO: 2022/6/11 参数的验证最好能整合到filter中
    private Integer checkParams(Object obj){
        if(obj instanceof BaseLevelVO){
            BaseLevelVO schemePageVO = (BaseLevelVO)obj;
            if( schemePageVO.getMsgLevel() == null || schemePageVO.getMsgLevel() == ""){
                return ServiceErrorCode.NULLPARAM.getCode();
            }

            if(schemePageVO.getMsgLevel().equals(MsgLevel.PROVINCE.getVal())){
                if(schemePageVO.getProvince() == null || schemePageVO.getProvince() ==""){
                    return  ServiceErrorCode.NULLPARAM.getCode();
                }
                return 0;
            }else if( schemePageVO.getMsgLevel().equals(MsgLevel.CITY.getVal())){
                if(schemePageVO.getCity() == null || schemePageVO.getCity() ==""){
                    return  ServiceErrorCode.NULLPARAM.getCode();
                }else if(schemePageVO.getProvince() == null || schemePageVO.getProvince() ==""){
                    return ServiceErrorCode.PARAMAERROR.getCode();
                }
                return 0;
            }else if(schemePageVO.getMsgLevel().equals(MsgLevel.COUNTY.getVal())){
                if((schemePageVO.getProvince() == null || schemePageVO.getProvince()=="")
                        ||(schemePageVO.getCounty()==null || schemePageVO.getCounty() == "")
                        ||(schemePageVO.getCity() == null || schemePageVO.getCity()=="")){
                    return ServiceErrorCode.PARAMAERROR.getCode();
                }
                return 0;
            }else if(schemePageVO.getMsgLevel().equals(MsgLevel.SCHOOL.getVal())){
                if((schemePageVO.getSchoolName() == null || schemePageVO.getSchoolName()=="")
                        &&(schemePageVO.getCounty()==null || schemePageVO.getCounty() == "")
                        &&(schemePageVO.getCity() == null || schemePageVO.getCity()=="")
                        ||(schemePageVO.getProvince() == null || schemePageVO.getProvince() == "")){
                    return ServiceErrorCode.PARAMAERROR.getCode();
                }
                return 0;
            }
        }
        return 0;

    }


//    @GetMapping( "readPsyFilesByStuId")
//    public Wrapper<List<BaseEntity>> readPsyFilesByStuId() {
//        List<BaseEntity> res = taskService.readPsyFilesByStuId(3L);
//        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
//    }

    /**
     * 记录展示页面
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(value = "getAllRecordByStuId")
    public Wrapper<IPage<PsyIntervationRecordDO>> GetAllRecordByStuId(@RequestBody FileIdAndStuIdVO fileIdAndStuIdVO){
        if(fileIdAndStuIdVO.getStuId() == null){
            return WrapMapper.wrap(
                    ServiceErrorCode.PARAMAERROR.getCode(),
                    Wrapper.ILLEGAL_ARGUMENT_MESSAGE,
                    new Page<>());
        }
        IPage res =  taskService.getAllRecordByStuId(fileIdAndStuIdVO.getStuId(),fileIdAndStuIdVO.getPageNum(),fileIdAndStuIdVO.getPageSize());
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
    }

    /**
     * 方案展示页面（添加方案后）
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(value="getAllPlanByStuId")
    public Wrapper<IPage<PsyIntervationRecordDO>> GetAllPlanByStuId( @RequestBody FileIdAndStuIdVO fileIdAndStuIdVO){
        if(fileIdAndStuIdVO.getStuId() == null){
            return WrapMapper.wrap(Wrapper.BAD_REQUEST, Wrapper.ILLEGAL_ARGUMENT_MESSAGE,new Page<>());
        }
        IPage res =  taskService.getAllPlanByStuId(fileIdAndStuIdVO.getStuId(),fileIdAndStuIdVO.getPageNum(),fileIdAndStuIdVO.getPageSize());
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
    }


    @RequestMapping(value = "getRcordDetailById")
    public Wrapper<BaseEntity> getRcordDetailById(@RequestBody FileIdAndStuIdVO fileIdAndStuIdVO){
        if(fileIdAndStuIdVO.getRecordId() == null){
            return WrapMapper.wrap(Wrapper.BAD_REQUEST, Wrapper.ILLEGAL_ARGUMENT_MESSAGE,new BaseEntity());
        }

        BaseEntity res =  taskService.getRcordDetailById(fileIdAndStuIdVO);
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
    }

    @RequestMapping(value = "getPlanDetailById")
    public Wrapper<BaseEntity> getPlanDetailById(@RequestBody FileIdAndStuIdVO fileIdAndStuIdVO){
        if(fileIdAndStuIdVO.getPlanId() == null){
            return WrapMapper.wrap(Wrapper.BAD_REQUEST, Wrapper.ILLEGAL_ARGUMENT_MESSAGE,new BaseEntity());
        }

        BaseEntity res =  taskService.getPlanDetailById(fileIdAndStuIdVO);
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
    }

//    @GetMapping( value = "getSchemePageData",params = {"start","size"})
//    public Wrapper<List<BaseEntity>> getSchemePageAllData(Integer start,Integer size) {
//        List<BaseEntity> res = taskService.getSchemePageData(1,10);
//        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
//    }

    /**
     * 预警库信息页面
     * 要学校的只能查看到学校的，区县的只能查看到区县的。
     * @param schemePageVO
     * @return

     */
    @RequestMapping( value = "getSchemePageDataByCondition")
    public Wrapper<IPage<IntervationSchemePageVO>> getSchemePageDataByCondition(@RequestBody PRIntervationSchemePageVO schemePageVO) {

//        IPage<BaseEntity> res = taskService.getSchemePageData(schemePageVO);
//        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
        for(Integer res = checkParams(schemePageVO);res < 0
                || schemePageVO.getUserId() == null
                || schemePageVO.getUserId() <= 0;){
            log.info("wrong request, erro code is "+res.toString());
            return WrapMapper.wrap(ServiceErrorCode.PARAMAERROR.getCode(),
                    ServiceErrorCode.PARAMAERROR.getVal(),
                    new Page<>(1,10));
        }
        return provinceTaskController.getPRSchemePageDataByCondition(schemePageVO);
    }

    /**
     *获取干预内容（危机等级 干预方案 干预记录）
     * @param fileIdAndStuIdVO
     * @return
     */
    @RequestMapping( value = "getIntervationContentByFileId")
    public Wrapper<IPage<RecordAndPlanVO>> getIntervationContentByFileId(@RequestBody FileIdAndStuIdVO fileIdAndStuIdVO) {
        if(fileIdAndStuIdVO.getFileId() == null
            || fileIdAndStuIdVO.getUserId() == null){
            return WrapMapper.wrap(ServiceErrorCode.NULLPARAM.getCode(),
                    ServiceErrorCode.NULLPARAM.getVal(),
                    new Page<>(1,10));
        }

        Wrapper<IPage<RecordAndPlanVO>> res = taskService.getIntervationContentByFileId(fileIdAndStuIdVO,false);
        return res;
    }
    /**
     *根据学生id获取干预内容（危机等级 干预方案 干预记录）
     * @param fileIdAndStuIdVO
     * @return
     */
    @RequestMapping( value = "getIntervationContentByStuId")
    public Wrapper<IPage<RecordAndPlanVO>> getIntervationContentByStuId(@RequestBody FileIdAndStuIdVO fileIdAndStuIdVO) {
        if(fileIdAndStuIdVO.getStuId() == null
                || fileIdAndStuIdVO.getUserId() == null){
            return WrapMapper.wrap(ServiceErrorCode.NULLPARAM.getCode(),
                    ServiceErrorCode.NULLPARAM.getVal(),
                    new Page<>(1,10));
        }

        Wrapper<IPage<RecordAndPlanVO>> res = taskService.getIntervationContentByFileId(fileIdAndStuIdVO,true);
        return res;
    }

    /**
     * 干预统计页面
     * 由于 机构数量分组会减少数据量，所以直接读到内存里来分页
     * @return
     */
    @RequestMapping( value = "getIntervationStaRes")
    public Wrapper<IPage<BaseEntity>> getIntervationStaRes(@RequestBody IntervationStatisticVO statisticVO) {

        for(Integer res = checkParams(statisticVO);res < 0;){
            log.info("wrong request, erro code is "+res.toString());
            return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ILLEGAL_ARGUMENT_MESSAGE, new Page<>(1,10));
        }

        return provinceTaskController.getPRIntervationStaRes(statisticVO);
    }



    @RequestMapping( value = "getIntervationStudentInfo")
    public Wrapper<HashMap> getIntervationStudentInfo(@RequestBody IntervationStatisticVO statisticVO){
        if(checkParams(statisticVO) < 0 ){

            return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ILLEGAL_ARGUMENT_MESSAGE, new HashMap());
        }
        HashMap res =  taskService.getIntervationStudentInfo(statisticVO);
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, res);
    }
//    @RequestMapping( value = "alerReleaseInfo")
//    public Wrapper<HashMap> alerRelease(@RequestBody BaseLevelVO baseLevelVO){
//        if(checkParams(baseLevelVO) < 0 ){
//
//            return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ILLEGAL_ARGUMENT_MESSAGE, new HashMap());
//        }
//        HashMap res =  taskService.alerRelease(baseLevelVO);
//        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, res);
//    }


    /**
     * 获取超时预警学生id列表
     * @param
     * @return
     */
    @RequestMapping( value = "getExpiringStudent")
    public Wrapper<Integer> getExpiringStudent(@RequestBody IntervationStatisticVO statisticVO){
        if(checkParams(statisticVO) < 0 ){

            return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ILLEGAL_ARGUMENT_MESSAGE, 0);
        }
        List<Long> res =  taskService.getExpiringStudent(statisticVO);
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, res.size());
    }

    @RequestMapping( value = "getAllHotWords")
    public Wrapper<List<HotWord>> getAllHotWords(){
        List<HotWord> res =  taskService.getAllHotWords();
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, res);
    }

    @RequestMapping( value = "getHotWordByName")
    public Wrapper<List<HotWord>> getHotWordByName(@RequestBody HotWord word){
        List<HotWord> res =  taskService.getHotWordByName(word);
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, res);
    }

    @GetMapping( value = "getHotWordList")
    public Wrapper<List<HotWordVO>> getHotWordByName(){
        List<HotWordVO> res =  taskService.getHotWordVO();
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, res);
    }

    @RequestMapping( value = "selectUnSubmitPlan")
    public Wrapper<BaseEntity> selectUnSubmitPlan(@RequestBody FileIdAndStuIdVO fileIdAndStuIdVO) {
        if(fileIdAndStuIdVO.getFileId() == null || fileIdAndStuIdVO.getFileId() <= 0){
            return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ILLEGAL_ARGUMENT_MESSAGE, new BaseEntity());
        }
        BaseEntity res = taskService.selectUnSubmitPlan(fileIdAndStuIdVO.getFileId());
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
    }


    @RequestMapping( value = "selectUnSubmitRecordOrCrisis")
    public Wrapper<BaseEntity> selectUnSubmitRecordOrCrisis(@RequestBody PsyIntervationRecordDO recordDO) {
        if(recordDO.getFileId() == null
                || recordDO.getFileId() <= 0
                || recordDO.getRecordType() == null
                || recordDO.getRecordType() == ""
                || recordDO.getUserId() == null){
            return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ILLEGAL_ARGUMENT_MESSAGE, new BaseEntity());
        }
        BaseEntity res = taskService.selectUnSubmitRecordOrCrisis(recordDO);
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 学生第一次进入预警库的时候创建，其planid、recordid将为空的
     * @param fileDO
     * @return
     */
    @PostMapping(value = "createIntervationSchemeItem")
    public Wrapper<String> createIntervationSchemeItem(@RequestBody PsyIntervationFileDO fileDO) {

//        PsyIntervationFileDO fileDO = EntityFactory.getBaseFileDO(1L,169L,1L);

        try {
            Integer res = 0;
            if((res = taskService.checkInput(fileDO) )< 0 ) {
                return WrapMapper.wrap(
                        ServiceErrorCode.PARAMAERROR.getCode(),
                        ServiceErrorCode.PARAMAERROR.getVal(),
                        res.toString());
            }

            res = taskService.createIntervationSchemeItem(fileDO);
            if(res < 0){
                return WrapMapper.wrap(res, ServiceErrorCode.getErrorMsgById(res,"createIntervationSchemeItem"),res.toString());
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, "0");

        }catch (Exception e){
            return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE,e.getMessage());
        }



    }

    /**
     * 添加干预记录
     * @param
     * @return
     */
    @PostMapping(value = "addIntervationRecordOrCrisis")
    public Wrapper<String> addIntervationRecordOrCrisis(@RequestBody RecordAndHotwordsVO recordAndHotWords) {

        if( recordAndHotWords == null){
            return WrapMapper.wrap(
                    Wrapper.SUCCESS_CODE,
                    Wrapper.SUCCESS_MESSAGE,
                    ServiceErrorCode.PARAMAERROR.getCode().toString());
        }

        PsyIntervationRecordDO recordParamsVO = recordAndHotWords.getRecordDO();
        List<String> hotWords = recordAndHotWords.getHotWords();
//        PsyIntervationRecordDO recordDO = EntityFactory.getTestRecordDO();
//        Long fileId = 3L;
//        Long userId = 271L;
        Integer res = 0;
        try {

            Long fileId = recordParamsVO.getFileId();
            Long userId = recordParamsVO.getUserId();
            if((res = taskService.checkInput(recordParamsVO,fileId,userId) )< 0
                    || recordParamsVO.getRecordType() == null
                    || recordParamsVO.getRecordType() == ""
                    || recordParamsVO.getFileId() == null
                    ||recordParamsVO.getFileId() <= 0L
                    || recordParamsVO.getUserId() == null
                    ||recordParamsVO.getUserId() <= 0L
                    || (recordParamsVO.getRecordType().equals(IntervationRecordType.RECORD.getVal()) == false
                            && recordParamsVO.getRecordType().equals( IntervationRecordType.CRISIS.getVal()) == false)) {

                return WrapMapper.wrap(
                        ServiceErrorCode.PARAMAERROR.getCode(),
                        ServiceErrorCode.PARAMAERROR.getVal(),
                        ServiceErrorCode.PARAMAERROR.getCode().toString());
            }

            recordParamsVO.setIntervationTime(new Timestamp(System.currentTimeMillis()));

            if(recordParamsVO.getIntervationExpireTime() == null){
                recordParamsVO.setIntervationExpireTime(new Timestamp(System.currentTimeMillis()+ EntityFactory.timeGap));
            }

            res = taskService.addIntervationRecordOrCrisis(userId,fileId, recordParamsVO);
            if(res < 0 ) {
                return WrapMapper.wrap(
                        res,
                        ServiceErrorCode.getErrorMsgById(res,"addIntervationRecordOrCrisis"),
                        res.toString());
            }

            //更新记录成功才更新热词
            taskService.updataHotWord(hotWords);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res.toString());
        }catch (Exception e){
            log.error("ERROR ====> {}", e);
            return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE,"");
        }


    }



    /**
     * 添加干预方案
     */
    @PostMapping(value = "addIntervationPlan")
    public Wrapper<String> addIntervationPlan(@RequestBody PsyIntervationPlanDO planParamsVO) {

//        PsyIntervationPlanDO planDO = EntityFactory.getTestPlanDO();
//        Long fileId = 4L;
//        Long userId = 3L;
        try {
            Integer res = 0;
            Long fileId = planParamsVO.getFileId();
            Long userId = planParamsVO.getUserId();

            if((res = taskService.checkInput(planParamsVO,fileId,userId) )< 0 ) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE, res.toString());
            }
            res = taskService.addIntervationPlan(userId, fileId, planParamsVO);
            if(res < 0) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE, res.toString());
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, res.toString());
        }catch (Exception e){
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, e.getMessage());
        }

    }

    @RequestMapping( value = "addHotWord")
    public Wrapper<Integer> addHotWord(@RequestBody HotWord word){
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, taskService.addHotWord(word.getName()));
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @PostMapping(value = "updateRecordOrCrisisById")
    public Wrapper<Integer> updateRecordOrCrisisById(@RequestBody RecordAndHotwordsVO recordAndHotWords) {

//        PsyIntervationRecordDO recordDO = EntityFactory.getTestRecordDO();
//        Long fileId = 3L;
//        Long userId = 271L;
        Integer res = 0;
        try {
            PsyIntervationRecordDO recordParamsVO = recordAndHotWords.getRecordDO();
            List<String> hotWords = recordAndHotWords.getHotWords();
            Long fileId = recordParamsVO.getFileId();
            Long userId = recordParamsVO.getUserId();

            if((res = taskService.checkInput(recordParamsVO,fileId,userId) )< 0
                    || recordParamsVO.getId() == null
                    || recordParamsVO.getRecordType() == null
                    || recordParamsVO.getRecordType() == ""
                    || recordParamsVO.getFileId() == null
                    ||recordParamsVO.getFileId() <= 0L
                    || recordParamsVO.getUserId() == null
                    ||recordParamsVO.getUserId() <= 0L
                    || (recordParamsVO.getRecordType().equals(IntervationRecordType.RECORD.getVal()) == false
                    && recordParamsVO.getRecordType().equals( IntervationRecordType.CRISIS.getVal()) == false)) {

                return WrapMapper.wrap(ServiceErrorCode.PARAMAERROR.getCode(), ServiceErrorCode.PARAMAERROR.getVal(), ServiceErrorCode.PARAMAERROR.getCode());
            }

            if(recordParamsVO.getIntervationExpireTime() == null){
                recordParamsVO.setIntervationExpireTime(new Timestamp(System.currentTimeMillis()+ EntityFactory.timeGap));
            }
            recordParamsVO.setCreateTime(new Timestamp(System.currentTimeMillis()));

            Wrapper<Integer> actRes = taskService.updateRecordOrCrisisById(recordParamsVO);
            if(actRes.getCode() >= 0 ) {
                //更新成功记录才更新热词
                taskService.updataHotWord(recordAndHotWords.getHotWords());
            }

            return actRes;
        }catch (Exception e){
            log.error("ERROR ====> {}", e);
            return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE,ServiceErrorCode.WRONGACTION.getCode());
        }


    }



    @PostMapping(value = "updateIntervationPlanById")
    public Wrapper<String> updateIntervationPlanById(@RequestBody PsyIntervationPlanDO planParamsVO) {

        try {
            Integer res = 0;
            Long fileId = planParamsVO.getFileId();
            Long userId = planParamsVO.getUserId();

            if((res = taskService.checkInput(planParamsVO,fileId,userId,planParamsVO.getId()) )< 0 ) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE, res.toString());
            }
            res = taskService.updateIntervationPlanById(planParamsVO);

            if(res < 0) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE, res.toString());
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, res.toString());
        }catch (Exception e){
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, e.getMessage());
        }
    }
}
