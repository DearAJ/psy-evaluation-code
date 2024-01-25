package org.ww.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ww.EntityDO.*;
import org.ww.EntityDO.entityFactory.EntityFactory;
import org.ww.EntityDO.valueObject.CrisisLevel;
import org.ww.EntityDO.valueObject.IntervationRecordType;
import org.ww.EntityDO.valueObject.ServiceErrorCode;
import org.ww.EntityVO.*;
import org.ww.base.BaseEntity;
import org.ww.domain.School;
import org.ww.domain.Students;
import org.ww.domain.Task;
import org.ww.mapper.*;
import org.ww.mapper.ProvicesMapper.PRIntervationStatisticMapper;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.ProvinceService.PRIntervationSchemeServiceImpl;
import org.ww.service.TaskService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    PsyIntervationRecordMapper psyRecordMapper;
    @Autowired
    PsyIntervationFileMapper fileMapper;
    @Autowired
    PsyIntervationPlanMapper psyPlanMapper;
    @Autowired
    PsyIntervationStuMapper stuMapper;
    @Autowired
    PsyIntervationUserMapper userMapper;

    @Autowired
    PsyIntervationManagerMapper managerMapper;
    @Autowired
    TaskMapper taskMapper;

    @Autowired
    PRIntervationStatisticMapper prStatisticMapper;


    @Autowired
    IntervationSchemeServiceImpl schemeService;

    @Autowired
    PRIntervationSchemeServiceImpl prService;

    @Autowired
    EntityFactory entityFactory;

    @Autowired
    SchoolMapper schoolMapper;

    @Autowired
    PsyIntervationRecordMapper recordMapper;
    @Autowired
    HotWordMapper hotWordMapper;

    @Override
    public List<BaseEntity> readPsyFilesByStuId(Long stuId) {
        List<BaseEntity> res = fileMapper.selectByStuId(stuId);
        return res;
    }


    public Long getMonthStartTime() {

        Long currentTime = System.currentTimeMillis();

        String timeZone = "GMT+8:00";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(currentTime);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();


    }

    public Integer checkInput(Object obj,Object ...params){
        for(Object o : params){
            if(o == null || o.toString() == ""){
                return ServiceErrorCode.NULLPARAM.getCode();
            }
        }
        if( obj == null) {
            return ServiceErrorCode.NULLPARAM.getCode();
        }else if(obj instanceof  PsyIntervationPlanDO){
            PsyIntervationPlanDO planDO = (PsyIntervationPlanDO) obj;
            if(planDO.getIsSubmit() == null
                    || planDO.getConclusion()==null || planDO.getConclusion()==""
                    || planDO.getIntervationPlanContent() == null || planDO.getIntervationPlanContent()==""
                    ||planDO.getSymptom() == null || planDO.getSymptom() == ""){
                return ServiceErrorCode.NULLPARAM.getCode();
            }
        }else if( obj instanceof PsyIntervationFileDO){
            PsyIntervationFileDO fileDO = (PsyIntervationFileDO) obj;
            if( fileDO.getTaskId() == null || fileDO.getStudentId() ==null || fileDO.getUserId() == null){
                return ServiceErrorCode.NULLPARAM.getCode();
            }

        }else if( obj instanceof PsyIntervationRecordDO){
            PsyIntervationRecordDO recordDO = (PsyIntervationRecordDO) obj;
            if(recordDO.getStuState() == null || recordDO.getStuState() ==""
                    ||recordDO.getIntervationRecord() ==null || recordDO.getIntervationRecord() == ""
                    ||recordDO.getIsTransfer() == null
                    ||recordDO.getCrisisLevel() == null || recordDO.getCrisisLevel() == ""
                    ||recordDO.getPostProcessing() == null || recordDO.getPostProcessing() == ""
            ){
                return ServiceErrorCode.NULLPARAM.getCode();
            }
        }

        return 0;
    }
    public Integer checkParams(Object ...params){

        for(Object o : params) {
            if (o == null) {
                log.info("错误的参数，recordDO == null");
                return ServiceErrorCode.NULLPARAM.getCode();
            }
        }
        return 0;
    }

    public Integer checkInsertResult(IdBaseEntity obj){

        if(obj instanceof  PsyIntervationFileDO) {
            if(1 > fileMapper.selectCount(new QueryWrapper<PsyIntervationFileDO>().eq("id",obj.getId()))){
                return ServiceErrorCode.INSERTFAILE.getCode();
            }
        }else if(obj instanceof  PsyIntervationPlanDO){
            if(1 > psyPlanMapper.selectCount(new QueryWrapper<PsyIntervationPlanDO>().eq("id",obj.getId()))){
                return ServiceErrorCode.INSERTFAILE.getCode();
            }
        }else if(obj instanceof  PsyIntervationRecordDO){
            if(1 > psyRecordMapper.selectCount(new QueryWrapper<PsyIntervationRecordDO>().eq("id",obj.getId()))){
                return ServiceErrorCode.INSERTFAILE.getCode();
            }
        }

        return 0;
    }

    /**
     /* *
     *当是新入库的记录 或者 未保存的记录 使用这个
     * @param obj
     * @param file
     * @param isInsert true：插入；false：更新 plan和record
     * @return
     * @throws Exception
     */
    private Integer updataFileItem(IdBaseEntity obj, PsyIntervationFileDO file,Boolean isInsert) throws Exception {
        if(file.getId() == null){
            return ServiceErrorCode.PARAMAERROR.getCode();
        }
        int isSuccess = -1;
        if(obj instanceof PsyIntervationRecordDO) {
            PsyIntervationRecordDO record = (PsyIntervationRecordDO) obj;
            if(record.getRecordType().equals(IntervationRecordType.RECORD.getVal())){
                if (record.getCrisisLevel().equals(CrisisLevel.DLEAVEL.getVal()) ){
                    record.setIntervationExpireTime(null);
                }
            }
            if(isInsert) {
                isSuccess = psyRecordMapper.insert((PsyIntervationRecordDO) obj);
            }else{
                isSuccess = psyRecordMapper.update((PsyIntervationRecordDO) obj, new QueryWrapper<PsyIntervationRecordDO>().eq("id",obj.getId()));
            }
            if(isSuccess < 1 || checkInsertResult(obj) < 0){
                return ServiceErrorCode.WRONGACTION.getCode();
            }

            if(((PsyIntervationRecordDO) obj).getRecordType().equals(IntervationRecordType.CRISIS.getVal())){
                file.setPsyIntervationCrisisId(((PsyIntervationRecordDO) obj).getId());
                isSuccess = fileMapper.update(file,new QueryWrapper<PsyIntervationFileDO>().eq("id",file.getId()));
                if(isSuccess < 1 || checkInsertResult(file) < 0){
                    return ServiceErrorCode.WRONGACTION.getCode();
                }


            }else{
                file.setPsyIntervationLastRecordId(((PsyIntervationRecordDO) obj).getId());
                isSuccess = fileMapper.update(file,new QueryWrapper<PsyIntervationFileDO>().eq("id",file.getId()));
                if(isSuccess < 1 || checkInsertResult(file) < 0){
                    return ServiceErrorCode.WRONGACTION.getCode();
                }
            }
        }else if(obj instanceof PsyIntervationPlanDO){
            if(isInsert) {
                isSuccess = psyPlanMapper.insert((PsyIntervationPlanDO) obj);
            }else{
                isSuccess = psyPlanMapper.update((PsyIntervationPlanDO) obj, new QueryWrapper<PsyIntervationPlanDO>().eq("id", obj.getId()));
            }
            if(isSuccess < 1 || checkInsertResult(file) < 0){
                return ServiceErrorCode.WRONGACTION.getCode();
            }

            file.setPsyIntervationLastPlanId(((PsyIntervationPlanDO) obj).getId());
            isSuccess = fileMapper.update(file,new QueryWrapper<PsyIntervationFileDO>().eq("id",file.getId()));
            if(isSuccess < 1 || checkInsertResult(file) < 0){
                return ServiceErrorCode.WRONGACTION.getCode();
            }

        }





        return Math.toIntExact(obj.getId());
    }


    /**
     * 将list 按照分页来截取，返回src总数结果
     * @param src
     * @param start
     * @param size
     * @return
     */
    public Integer splitListToPage(List src,Integer pgNo, Integer pgSize){
        pgNo = pgNo > 0 ? pgNo : 1;
        Integer total = src.size();
        Integer end = total > pgNo*pgSize ? pgNo*pgSize:total;
        Integer startFrom = (pgNo-1)*pgSize < total ? (pgNo-1)*pgSize : total;
        src = src.subList(startFrom, end);
        return total;
    }


    public List getSplitListToPage(List src,Integer pgNo, Integer pgSize){
        pgNo = pgNo > 0 ? pgNo : 1;
        Integer total = src.size();
        Integer end = total > pgNo*pgSize ? pgNo*pgSize:total;
        Integer startFrom = (pgNo-1)*pgSize < total ? (pgNo-1)*pgSize : total;
        src = src.subList(startFrom, end);
        return src;
    }



///////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 学生第一次入库，创建唯一的入库fileItem
     * @param fileDO
     * @return
     */
    @Override
    public Integer createIntervationSchemeItem(PsyIntervationFileDO fileDO) {

        int checkRes = checkParams(fileDO);
        if( checkRes < 0 ){
            return checkRes;
        }

        Long stuId = fileDO.getStudentId();
        Long userId = fileDO.getUserId();
        Long taskId = fileDO.getTaskId();
        checkRes = checkParams(stuId,userId,taskId);
        if( checkRes < 0 ){
            return checkRes;
        }

        Integer stuCounts = stuMapper.selectCount(new QueryWrapper<PsyIntervationStuDO>()
                .eq("id",stuId)
                .eq("deleted",0));
        Integer uCounts = userMapper.selectCount(new QueryWrapper<PsyIntervationUserDO>()
                .eq("id",userId)
                .eq("deleted",0));
        Integer taskCounts = taskMapper.selectCount(new QueryWrapper<Task>()
                .eq("id",taskId)
                .eq("deleted",0));

        if(stuCounts == 0 || uCounts ==0 || taskCounts == 0){
            //未从数据库找到该记录
            return ServiceErrorCode.FINDNULL.getCode();
        }

        //已经在预警库里
        List<PsyIntervationFileDO> files= fileMapper.selectList(
                new QueryWrapper<PsyIntervationFileDO>()
                        .eq("student_id",stuId)
                        .eq("task_id",fileDO.getTaskId()));



        PsyIntervationFileDO file = entityFactory.getBaseFileDO(taskId,stuId,userId);
        int nums = 0;
        //尝试重新创建
        for(int i = 0; nums == 0 && i < 2; ++i) {
            if(files.size() > 0 ){
                fileMapper.update(files.get(0),
                        new QueryWrapper<PsyIntervationFileDO>()
                                .eq("id",files.get(0).getId()));
                //return ServiceErrorCode.ISSUBMIT.getCode();
            }else {
                fileMapper.insert(file);
            }
            nums = fileMapper.selectCount(new QueryWrapper<PsyIntervationFileDO>().eq("student_id", stuId));

        }

        return nums == 1 || files.size() > 0 ? Math.toIntExact(file.getId()) : ServiceErrorCode.INSERTFAILE.getCode();
    }

    public Wrapper<Integer> updateRecordOrCrisisById(PsyIntervationRecordDO recordDO) throws Exception {
        PsyIntervationRecordDO record = psyRecordMapper.selectById(recordDO.getId());
        if(record == null){
            return WrapMapper.wrap(ServiceErrorCode.FINDNULL.getCode(),ServiceErrorCode.FINDNULL.getVal(),ServiceErrorCode.WRONGACTION.getCode());
        }
        if(record.getRecordType().equals(recordDO.getRecordType()) == false){
            return WrapMapper.wrap(ServiceErrorCode.PARAMAERROR.getCode(),ServiceErrorCode.PARAMAERROR.getVal(),ServiceErrorCode.PARAMAERROR.getCode());
        }

        if(record.getIsSubmit() == false) {
            //不允许修改其他用户的未提交结果
            if( record.getUserId().equals(recordDO.getUserId()) == false){
                return WrapMapper.wrap(ServiceErrorCode.WRONGAUTHORITY.getCode(),
                        ServiceErrorCode.WRONGAUTHORITY.getVal(),
                        ServiceErrorCode.WRONGAUTHORITY.getCode());
            }
            PsyIntervationFileDO file = fileMapper.selectById(recordDO.getFileId());
            file.setPsyIntervationLastRecordId(recordDO.getId());

            file.setUserId(recordDO.getUserId());

            Integer res = updataFileItem(recordDO, file, false);
            if(res < 0){
                return WrapMapper.wrap(res,ServiceErrorCode.getErrorMsgById(res,"updateRecordOrCrisisById"),res);
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        }


        return WrapMapper.wrap(ServiceErrorCode.ISSUBMIT.getCode(),ServiceErrorCode.ISSUBMIT.getVal(),ServiceErrorCode.ISSUBMIT.getCode());
    }

    public PsyIntervationRecordDO selectUnSubmitRecordOrCrisis(PsyIntervationRecordDO recordDO){
        PsyIntervationRecordDO recordOld = psyRecordMapper.selectOne(
                new QueryWrapper<PsyIntervationRecordDO>()
                        .eq("is_submit",false)
                        .eq("file_id",recordDO.getFileId())
                        .eq("record_type",recordDO.getRecordType())
                        .eq("user_id",recordDO.getUserId())
                        .last("limit 1"));

        return recordOld;
    }




    @Transactional
    public Integer addIntervationRecordOrCrisis(Long userId, Long fileId, PsyIntervationRecordDO recordDO) throws Exception {
        try {
            IdBaseEntity f = fileMapper.selectById(fileId);

            int checkRes = 0;
            if ((checkParams(f, recordDO)) < 0) {
                return checkRes;
            }
            ;
            if (!(f instanceof PsyIntervationFileDO)) {
                log.info("file 不是 PsyIntervationFileDO类型实体，请检查数据库");
                return ServiceErrorCode.PARAMAERROR.getCode();
            }

            /**
             * 如果上一个未提交，则返回错误
             */
            PsyIntervationFileDO file = (PsyIntervationFileDO) f;


            recordDO.setUserId(userId);
            recordDO.setFileId(file.getId());
            file.setUserId(userId);

            PsyIntervationRecordDO recordOld = psyRecordMapper.selectOne(
                    new QueryWrapper<PsyIntervationRecordDO>()
                            .eq("file_id", file.getId())
                            .eq("is_submit", false)
                            .eq("user_id", file.getUserId())
                            .last("limit 1"));

            if (recordOld != null && recordDO.getRecordType().equals(IntervationRecordType.RECORD.getVal())) {
                //存在未提交的planDO,禁止新建
                log.info("存在未提交的record,禁止新建");
                return ServiceErrorCode.UNSAVELAST.getCode();
            }


            if (recordOld == null && file.getPsyIntervationCrisisId() == null && recordDO.getRecordType().equals(IntervationRecordType.CRISIS.getVal())) {
                log.info("====================> 1 {} {} ", recordOld, file);
                return updataFileItem(recordDO, file, true);
            }

            log.info("====================> 1 {} {} ", recordOld, file);
            if (recordDO.getRecordType().equals(IntervationRecordType.RECORD.getVal())) {
                PsyIntervationPlanDO planDOOld = psyPlanMapper.selectOne(
                        new QueryWrapper<PsyIntervationPlanDO>()
                                .eq("file_id", file.getId())
                                .eq("is_submit", false)
                                .eq("user_id", file.getUserId())
                                .last("limit 1"));

                //存在未提交的planDO
                if (file.getPsyIntervationCrisisId() == null || planDOOld != null) {
                    //必须先插入crisis类型的record和plan,plan 必须提交
                    log.info("确认危机或者plan未创建或者未提交");
                    return ServiceErrorCode.WRONGADDORDER.getCode();
                }

                return updataFileItem(recordDO, file, true);

            }
            return ServiceErrorCode.WRONGACTION.getCode();
        }catch (Exception e){
            e.printStackTrace();
            log.error("error {}", e.getMessage());
            return ServiceErrorCode.WRONGACTION.getCode();
        }
    }

    public void updataHotWord(List<String> words){
        try {
            if(words ==null || words.size() == 0){
                return ;
            }

            List<HotWord> hotWords = hotWordMapper.selectList(new QueryWrapper<HotWord>().in("name",words));
            for( HotWord word : hotWords){

                if(word == null ){
                    continue;
                }

                Long gapTime = word.getGapTime();
                Date updateTime = word.getUpdateTime();
                Long newClickNums = word.getNewClickNums()+1;
                Long clickNums = word.getClickNums();
                //每两天更新一次累计点击
                if (System.currentTimeMillis() - updateTime.getTime() > gapTime) {
                    word.setClickNums(clickNums + newClickNums);
                    word.setNewClickNums(0L);
                    word.setUpdateTime(new Date(System.currentTimeMillis()));
                } else {
                    word.setNewClickNums(newClickNums);
                }
                hotWordMapper.update(word, new QueryWrapper<HotWord>().eq("id", word.getId()));
            }
        }catch (Exception e){
            log.info("热词更新失败"+e.getMessage());
        }

    }

    /**
     * 获取词频
     * @return
     */
    public List<HotWordVO> getHotWordVO( ){
        List<HotWord> hotWords = getAllHotWords();
        List<HotWordVO> hotWordVOS = hotWords.stream().map(
                e->{
                    if(e == null) return null;
                    HotWordVO hotWordVO = new HotWordVO();
                    hotWordVO.setName(e.getName());
                    hotWordVO.setCount(e.getClickNums()+e.getNewClickNums());
                    return hotWordVO;

                }).collect(Collectors.toList());
        hotWordVOS.sort((a,b)->{
            return a.getCount() >= b.getCount() ? -1 : 1;
        });
        return hotWordVOS;

    }


    public Integer addIntervationPlan(Long userId, Long fileId,PsyIntervationPlanDO planDO) throws Exception {

        /**
         * 首先 file 和 record 必须已存在
         * 其次更新file 和 plan
         * 若file已存在一个plan了,需要创建干预记录才可以新增干预方案
         */
        IdBaseEntity f = fileMapper.selectById(fileId);
        int checkRes = 0;
        if ((checkRes = checkParams(f, planDO)) < 0) {
            return checkRes;
        }
        ;
        if (!(f instanceof PsyIntervationFileDO)) {
            log.info("file 不是 PsyIntervationFileDO类型实体，请检查数据库");
            return checkRes = ServiceErrorCode.PARAMAERROR.getCode();
        }


        PsyIntervationFileDO file = (PsyIntervationFileDO) f;

        if (file.getPsyIntervationCrisisId() == null) {
            //尚未填写危机确认，不可以操作
            return ServiceErrorCode.WRONGACTION.getCode();
        }

        planDO.setUserId(userId);
        planDO.setFileId(file.getId());

        file.setUserId(userId);

        PsyIntervationPlanDO planOld = psyPlanMapper.selectOne(
                new QueryWrapper<PsyIntervationPlanDO>()
                        .eq("file_id", file.getId())
                        .eq("is_submit", false)
                        .last("limit 1"));

        /**
         *  尚未创建plan
         *  尚未提交plan,禁止创建新的plan
         */

        if (planOld == null) {
            return updataFileItem(planDO, file, true);
        } else if (planOld.getIsSubmit() == false) {
            return ServiceErrorCode.UNSAVELAST.getCode();
        }
        return 0;

    }
    public Integer updateIntervationPlanById(PsyIntervationPlanDO planDO) {
        try {
            if (planDO.getId() == null) return ServiceErrorCode.PARAMAERROR.getCode();
            PsyIntervationPlanDO plan = psyPlanMapper.selectById(planDO.getId());
            if (plan == null) {
                return ServiceErrorCode.FINDNULL.getCode();
            }
            if (plan.getIsSubmit() == false) {
                PsyIntervationFileDO file = fileMapper.selectById(planDO.getFileId());
                file.setPsyIntervationLastRecordId(planDO.getId());

                file.setUserId(planDO.getUserId());

                return updataFileItem(planDO, file, false);
            }

            return ServiceErrorCode.ISSUBMIT.getCode();
        }catch (Exception e){
            log.error("ERROR ====> {}", e);
            return ServiceErrorCode.UPDATAFAILE.getCode();
        }
    }

    public PsyIntervationPlanDO selectUnSubmitPlan(Long fileId){
        PsyIntervationPlanDO planDO = psyPlanMapper.selectOne(
                new QueryWrapper<PsyIntervationPlanDO>()
                        .eq("is_submit", false)
                        .eq("file_id",fileId)
                        .last("limit 1"));

        return planDO;
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IPage<PsyIntervationRecordDO> getAllRecordByStuId(Long stuId,Integer start, Integer size){

        List fileIds = fileMapper.selectObjs(new QueryWrapper<PsyIntervationFileDO>().eq("student_id",stuId));
        //List a = psyRecordMapper.selectList(new QueryWrapper<PsyIntervationRecordDO>().in("id",ids));
        IPage<PsyIntervationRecordDO> res =  psyRecordMapper.selectPage(new Page<>(start,size),new QueryWrapper<PsyIntervationRecordDO>().eq("is_submit",true).in("file_id",fileIds));
        Integer total = psyRecordMapper.selectCount(new QueryWrapper<PsyIntervationRecordDO>().eq("is_submit",true).in("file_id",fileIds));
        res.setTotal(total);
        return res;
    }




    public IPage<PsyIntervationPlanDO> getAllPlanByStuId(Long stuId,Integer start, Integer size){
        List fileIds = fileMapper.selectObjs(new QueryWrapper<PsyIntervationFileDO>().eq("student_id",stuId));
        IPage<PsyIntervationPlanDO> res = psyPlanMapper.selectPage(new Page<>(start,size),new QueryWrapper<PsyIntervationPlanDO>().eq("is_submit",true).in("file_id",fileIds));
        Integer total = psyPlanMapper.selectCount(new QueryWrapper<PsyIntervationPlanDO>().eq("is_submit",true).in("file_id",fileIds));
        res.setTotal(total);
        return res;
    }



    @Override
    public List<BaseEntity> getSchemePageData(int start,int size) {
        return null;
    }




    @Override
    public  IPage<BaseEntity> getSchemePageData(IntervationSchemePageVO pageVO) {

        return null;

    }

    @Override
    public List<BaseEntity> getSchemeItemByFileList(List<PsyIntervationFileDO> res) {
        return null;
    }


    /**
     * 获取学生的详细干预页面
     * 单个学生的干预记录一般较少，在内存中处理分页
     *
     * @param fileIdAndStuIdVO
     * @return
     */
    public Wrapper<IPage<RecordAndPlanVO>> getIntervationContentByFileId(FileIdAndStuIdVO fileIdAndStuIdVO,Boolean isByStuId){

        try {
            List<PsyIntervationFileDO> fileDOS = new ArrayList<>();
            List<PsyIntervationPlanDO> planDOS = new ArrayList<>();
            List<PsyIntervationRecordDO> recordDOS = new ArrayList<>();
            List<PsyIntervationRecordDO> crisisDOS = new ArrayList<>();
            List<Object> fileIds = new ArrayList<>();

            if(isByStuId) {
                PsyIntervationStuDO s = stuMapper.selectById(fileIdAndStuIdVO.getStuId());
                List<PsyIntervationStuDO> sList = stuMapper.selectList(new QueryWrapper<PsyIntervationStuDO>()
                        .eq("username", s.getUsername()));
                List<Long> sIds = sList.stream().map(PsyIntervationStuDO::getId).collect(Collectors.toList());
                sIds.add(-1L);

                fileIds = fileMapper.selectObjs(new QueryWrapper<PsyIntervationFileDO>().in("student_id", sIds));
                fileDOS = fileMapper.selectList(new QueryWrapper<PsyIntervationFileDO>().in("student_id", sIds));
            }else {
                fileIds = fileMapper.selectObjs(new QueryWrapper<PsyIntervationFileDO>().eq("id", fileIdAndStuIdVO.getFileId()));
                fileDOS = fileMapper.selectList(new QueryWrapper<PsyIntervationFileDO>().eq("id", fileIdAndStuIdVO.getFileId()));
            }
            fileIds.add(-1L);

            List planIds = psyPlanMapper.selectObjs(new QueryWrapper<PsyIntervationPlanDO>().in("file_id", fileIds));
            List recordIds = psyRecordMapper.selectObjs(new QueryWrapper<PsyIntervationRecordDO>().in("file_id", fileIds).eq("record_type", IntervationRecordType.RECORD.getVal()));
            List crisisIds = psyRecordMapper.selectObjs(new QueryWrapper<PsyIntervationRecordDO>().in("file_id", fileIds).eq("record_type", IntervationRecordType.CRISIS.getVal()));

            planIds.add(-1L);
            recordIds.add(-1L);
            crisisIds.add(-1L);

            crisisDOS = psyRecordMapper.selectList(
                    new QueryWrapper<PsyIntervationRecordDO>()
                            .in("id", crisisIds)
                            .and(w->w.eq("is_submit",true))
                            .or(w->w.in("id", crisisIds)
                                    .eq("is_submit",false)
                                    .eq("user_id",fileIdAndStuIdVO.getUserId()))
            );

            planDOS = psyPlanMapper.selectList(new QueryWrapper<PsyIntervationPlanDO>().in("id", planIds));

            recordDOS = psyRecordMapper.selectList(
                    new QueryWrapper<PsyIntervationRecordDO>()
                            .in("id", recordIds)
                            .and(w->w.eq("is_submit",true))
                            .or(w->w.in("id", recordIds)
                                    .eq("is_submit",false)
                                    .eq("user_id",fileIdAndStuIdVO.getUserId()))
            );

            List<PsyIntervationContentVO> contentRes = schemeService.getIntervationContentVOs(crisisDOS, recordDOS, planDOS, fileDOS);
            if(contentRes.size() == 0){
                return WrapMapper.wrap(ServiceErrorCode.FINDNULL.getCode(),
                        ServiceErrorCode.FINDNULL.getVal(),
                        new Page<>(1,10));
            }


            List uids = new ArrayList();
            for(PsyIntervationFileDO fileDO:fileDOS){
                uids.add(fileDO.getUserId());
            }
            uids.add(-1L);

            //user id 和 managerName
            HashMap<Long,String> uidAnduName = new HashMap<>();
            List<PsyIntervationUserDO> users = userMapper.selectList(
                    new QueryWrapper<PsyIntervationUserDO>()
                            .in("id",uids)
                            .eq("deleted",0));

            for(PsyIntervationUserDO userDO:users){
                uidAnduName.put(userDO.getId(), userDO.getUsername());
            }

            //获取教师名称和username关系
            List uNames = Arrays.stream(uidAnduName.values().toArray()).collect(Collectors.toList());
            uNames.add("/");
            List<PsyIntervationManagerDO> managers = managerMapper.selectList(
                    new QueryWrapper<PsyIntervationManagerDO>()
                            .eq("deleted",0)
                            .in("username",uNames)
            );
            HashMap<String,String> userNameAndManagerName = new HashMap<>();
            for(PsyIntervationManagerDO managerDO:managers){
                userNameAndManagerName.put(managerDO.getUsername(),managerDO.getName());
            }



            List<RecordAndPlanVO> res = new ArrayList<>();

            for(PsyIntervationContentVO contentVO: contentRes){
                List<PsyIntervationPlanDO> planDOList = contentVO.getPsyIntervationPlanDO();
                List<PsyIntervationRecordDO> recordDOList = contentVO.getPsyIntervationRecordDO();
                List<PsyIntervationRecordDO> crisisList = contentVO.getPsyIntervationCrisisDO();

                for(PsyIntervationPlanDO planDO: planDOList){
                    String teacherName = "";
                    Long key = planDO.getUserId();
                    if(uidAnduName.containsKey(key)
                            &&( teacherName = uidAnduName.get(key)) != ""
                            && userNameAndManagerName.containsKey(teacherName)
                            &&(teacherName = userNameAndManagerName.get(teacherName))!= ""){

                    }else{
                        teacherName = planDO.getUserId().toString();
                    }
                    res.add( RecordAndPlanVO
                            .builder()
                            .id(planDO.getId())
                            .content(planDO.getIntervationPlanContent())
                            .methods("")
                            .date(planDO.getCreateTime())
                            .moudle("干预方案")
                            .isSubmit(planDO.getIsSubmit())
                            .psyTeacher(teacherName)
                            .build());
                }


                for(PsyIntervationRecordDO recordDO: recordDOList){
                    String teacherName = "";
                    Long key = recordDO.getUserId();
                    if(uidAnduName.containsKey(key)
                            &&( teacherName = uidAnduName.get(key)) != ""
                            && userNameAndManagerName.containsKey(teacherName)
                            &&(teacherName = userNameAndManagerName.get(teacherName))!= ""){

                    }else{
                        teacherName = recordDO.getUserId().toString();
                    }
                    res.add( RecordAndPlanVO
                            .builder()
                            .date(recordDO.getIntervationTime())
                            .moudle("干预记录")
                            .id(recordDO.getId())
                            .content(recordDO.getIntervationRecord())
                            .methods(recordDO.getIntervationWay())
                            .psyTeacher(teacherName)
                            .crisisLevel(recordDO.getCrisisLevel())
                            .isSubmit(recordDO.getIsSubmit())
                            .build());
                }


                for(PsyIntervationRecordDO crisisDO: crisisList){
                    String teacherName = "";
                    Long key = crisisDO.getUserId();
                    if(uidAnduName.containsKey(key)
                            &&( teacherName = uidAnduName.get(key)) != ""
                            && userNameAndManagerName.containsKey(teacherName)
                            &&(teacherName = userNameAndManagerName.get(teacherName))!= ""){

                    }else{
                        teacherName = crisisDO.getUserId().toString();
                    }
                    res.add( RecordAndPlanVO
                            .builder()
                            .date(crisisDO.getIntervationTime())
                            .moudle("危机确认")
                            .id(crisisDO.getId())
                            .content(crisisDO.getIntervationRecord())
                            .methods(crisisDO.getIntervationWay())
                            .psyTeacher(teacherName)
                            .crisisLevel(crisisDO.getCrisisLevel())
                            .isSubmit(crisisDO.getIsSubmit())
                            .build());
                }



            }

            // 加入预警建议 start
            List<RecordAndPlanVO> advices = psyPlanMapper.selectAdvices(fileIdAndStuIdVO.getFileId());
            res.addAll(advices);
            // 加入预警建议 end

            Integer start = fileIdAndStuIdVO.getPageNum();
            Integer size = fileIdAndStuIdVO.getPageNum();
            //Integer total = splitListToPage(res,start,size);
            IPage<RecordAndPlanVO> pageRes = new Page<>(start,size);
            List sortedList = res.stream().sorted(new Comparator<RecordAndPlanVO>() {
                @Override
                public int compare(RecordAndPlanVO o1, RecordAndPlanVO o2) {
                    if(o1.getDate().getTime() > o2.getDate().getTime()){
                        return -1;
                    }else if(o1.getDate().getTime() < o2.getDate().getTime()){
                        return 1;
                    }else {
                        return 0;
                    }
                }
            }).collect(Collectors.toList());

            pageRes.setTotal(recordDOS.size()+planDOS.size()+crisisDOS.size());
            pageRes.setRecords(sortedList);

            return WrapMapper.wrap(Wrapper.SUCCESS_CODE,
                    Wrapper.SUCCESS_MESSAGE,
                    pageRes);

        }catch (Exception e){
            log.error("ERROR ====> {}", e);
            return WrapMapper.wrap(Wrapper.ERROR_CODE,
                    Wrapper.ERROR_MESSAGE,
                    new Page<>(1,10));
        }

    }
    public BaseEntity getPlanDetailById(FileIdAndStuIdVO fileIdAndStuIdVO){
        return psyPlanMapper.selectById(fileIdAndStuIdVO.getPlanId());
    }

    public BaseEntity getRcordDetailById(FileIdAndStuIdVO fileIdAndStuIdVO){
        return psyRecordMapper.selectById(fileIdAndStuIdVO.getRecordId());
    }

    /**
     * 获得超时提醒的预警学生人数
     * @return
     */
    public List<Long> getExpiringStudent(IntervationStatisticVO statisticVO){
        try {
            Date date = new Date(System.currentTimeMillis() - 2592000000L);

            List schoolIds = schoolMapper.selectObjs(new QueryWrapper<School>()
                    .eq(statisticVO.getProvince() == null ? false : true, "province", statisticVO.getProvince())
                    .eq(statisticVO.getCity() == null ? false : true, "city", statisticVO.getCity())
                    .eq(statisticVO.getCounty() == null ? false : true, "county", statisticVO.getCounty())
                    .eq(statisticVO.getSchoolName() == null ? false : true, "name", statisticVO.getSchoolName())
                    .eq(statisticVO.getSchoolId() == null ? false : true, "id", statisticVO.getSchoolId()));
            //防止集合为空
            schoolIds.add(-1);
            List<Long> res = recordMapper.selectExpireStudentIds(schoolIds);

            return res;
        }catch (Exception e){
            log.error("ERROR ====> {}", e);
            return new ArrayList<>();
        }

    }


    /**
     *
     * @param statisticVO
     * @return
     */
    public HashMap<String,Integer> getIntervationStudentInfo(IntervationStatisticVO statisticVO){
//        List<PRIntervationStatisticDO> statisticDOS = prStatisticMapper.selectStatisticRes(statisticVO.getProvince(),
//                statisticVO.getCity(),
//                statisticVO.getCounty(),
//                statisticVO.getSchoolName(),
//                statisticVO.getSchoolId(),
//                null,
//                null,
//                null);
        List<School> schools = schoolMapper.selectList(new QueryWrapper<School>()
                .eq(statisticVO.getProvince() == null ? false : true, "province", statisticVO.getProvince())
                .eq(statisticVO.getCity() == null ? false : true, "city", statisticVO.getCity())
                .eq(statisticVO.getCounty() == null ? false : true, "county", statisticVO.getCounty())
                .eq(statisticVO.getSchoolName() == null ? false : true, "name", statisticVO.getSchoolName())
                .eq(statisticVO.getSchoolId() == null ? false : true, "id", statisticVO.getSchoolId())
                .select("id","province","city","county","name"));


        //获取schoolId和school实体关系
        HashMap<Long,School> schoolIdAndSchool = new HashMap<>();
        schools.stream().forEach((e)->{
            if(e!=null && e.getId() != null){
                schoolIdAndSchool.put(e.getId(),e);
            }
        });
        schoolIdAndSchool.put(-1L,null);

        //获取预警库学生
        List<PsyIntervationFileDO> fileDOS = fileMapper.selectList(
                new QueryWrapper<PsyIntervationFileDO>()
                        .select("student_id","psy_intervation_crisis_id","psy_intervation_last_record_id"));

        HashMap<Long,PsyIntervationFileDO> fileDOHashMap = new HashMap<>();
        fileDOS.stream().forEach((e)->{
            if(e!=null && e.getId() != null){
                fileDOHashMap.put(e.getId(),e);
            }
        });

        List<Long> stuIdsInFiles = fileDOS.stream().map(
                e->{
                    if( e == null || e.getStudentId() == null) return -1L;
                    return e.getStudentId();
                }
        ).collect(Collectors.toList());
        stuIdsInFiles.add(-1L);

        //获取预警的学生学校信息
        List<PsyIntervationStuDO>  stuDOS = stuMapper.selectList(
                new QueryWrapper<PsyIntervationStuDO>()
                        .in("id",stuIdsInFiles)
        );

        //根据学校条件过滤干预库内的学生，获取总人数
        List<PsyIntervationStuDO> stuInQuery =  stuDOS.stream().filter(e->{
            if(e == null || e.getSchoolId() == null || schoolIdAndSchool.containsKey(e.getSchoolId()) == false){
                return false;
            }else{
                return true;
            }
        }).collect(Collectors.toList());


        HashSet<Long> stuIdSet = new HashSet<>();
        stuInQuery.stream().forEach(e->{
            if(e != null){
                stuIdSet.add(e.getId());
            }
        });

        //获取对应recordIds
        List recordIds = fileDOS.stream().map(
                e->{
                    if( e == null || e.getStudentId() == null || e.getPsyIntervationCrisisId() == null ) return -1L;
                    if(stuIdSet.contains(e.getStudentId())){
                        return e.getPsyIntervationLastRecordId() == null ?
                                e.getPsyIntervationCrisisId() : e.getPsyIntervationLastRecordId();
                    }
                    return -1L;
                }
        ).collect(Collectors.toList());
        recordIds.add(-1L);


        //获取对应的record记录
        List<PsyIntervationRecordDO> recordDOS = recordMapper.selectList(
                new QueryWrapper<PsyIntervationRecordDO>()
                        .in("id",recordIds)
        );

        HashMap<String,Integer> res = new HashMap<>();
        HashMap<String,Integer> tempRes = new HashMap<>();
//
//        res.put("总人数",stuInQuery.size());
        res.put("一级预警人数",0);
        res.put("二级预警人数",0);
        res.put("三级预警人数",0);
        res.put("解除预警总人数", 0);
        res.put("当月解除人数", 0);
        Long startMonthTime = getMonthStartTime();
        for(PsyIntervationRecordDO v : recordDOS){
            if(v.getCrisisLevel().contains(CrisisLevel.ALEAVEL.getVal())) {
                res.put("一级预警人数", res.get("一级预警人数") + 1);
            }else if(v.getCrisisLevel().contains(CrisisLevel.BLEAVEL.getVal())) {
                res.put("二级预警人数", res.get("二级预警人数") + 1);
            }else if(v.getCrisisLevel().contains(CrisisLevel.CLEAVEL.getVal())) {
                res.put("三级预警人数", res.get("三级预警人数") + 1);
            }else if(v.getCrisisLevel().contains(CrisisLevel.DLEAVEL.getVal())){
                res.put("解除预警总人数",res.get("解除预警总人数")+1);
                if( v.getIntervationTime().getTime() - startMonthTime > 0 ){
                    res.put("当月解除人数", res.get("当月解除人数")+1);
                }

            }
        }

        //HashMap res2 = alerRelease(statisticVO);
        //res.putAll(res2);
        res.put("总人数", res.get("一级预警人数") +
                res.get("二级预警人数") +
                res.get("三级预警人数"));
        return res;

    }

    /**
     * 解除预警人数
     * @param baseLevelVO
     * @return
     */
    public HashMap<String,Integer> alerRelease(BaseLevelVO baseLevelVO){

        List schoolIds = schoolMapper.selectObjs(new QueryWrapper<School>()
                .eq(baseLevelVO.getProvince() == null ? false: true,"province",baseLevelVO.getProvince())
                .eq(baseLevelVO.getCity() == null ? false: true,"city",baseLevelVO.getCity())
                .eq(baseLevelVO.getCounty() == null ? false : true,"county",baseLevelVO.getCounty())
                .eq(baseLevelVO.getSchoolName() == null ? false : true,"name",baseLevelVO.getSchoolName())
                .eq(baseLevelVO.getSchoolId() == null ? false : true,"id",baseLevelVO.getSchoolId()));
        schoolIds.add(-1L);
        List stuIds = stuMapper.selectObjs(
                new QueryWrapper<PsyIntervationStuDO>()
                        .in("school_id",schoolIds)
                        .eq("deleted",0));
        stuIds.add(-1L);
        List<PsyIntervationFileDO> records = fileMapper.selectList(
                new QueryWrapper<PsyIntervationFileDO>()
                        .select("psy_intervation_last_record_id")
                        .in("student_id",stuIds));

        List<Long> recordIds = records.stream().map(e->{if(e == null) return -1L; return e.getPsyIntervationLastRecordId();}).collect(Collectors.toList());
        recordIds.add(-1L);
        List unReleasedIds = psyRecordMapper.selectObjs(new QueryWrapper<PsyIntervationRecordDO>().in("id",recordIds).gt("crisis_level",2));

        Long startMonthTime = getMonthStartTime();

        List unReleasedCurMonthIds = psyRecordMapper.selectObjs(
                new QueryWrapper<PsyIntervationRecordDO>()
                        .in("id",recordIds)
                        .gt("crisis_level",2)
                        .gt("create_time",startMonthTime));

        HashMap<String,Integer> res = new HashMap<>();
        res.put("解除预警总人数", unReleasedIds.size());
        res.put("当月解除人数", unReleasedCurMonthIds.size());

        return res;

    }

    public List<HotWord> getAllHotWords(){
        return  hotWordMapper.selectList(new QueryWrapper<HotWord>().orderByDesc("click_nums"));
    }
    public List<HotWord> getHotWordByName(HotWord hotWord){
        if(hotWord ==null || hotWord.getName()==null ) return new ArrayList<>();

        return  hotWordMapper.selectList(new QueryWrapper<HotWord>().like("name",hotWord.getName()));
    }
    public Integer addHotWord(String word){
        try {
            if (word == null || word == "") {
                return ServiceErrorCode.PARAMAERROR.getCode();
            }
            HotWord w = HotWord.builder().name(word).clickNums(1L).updateTime(new Date(System.currentTimeMillis())).build();
            if (hotWordMapper.insert(w) <= 0) {
                return ServiceErrorCode.INSERTFAILE.getCode();
            }
            return 0;
        }catch (Exception e){
            log.error("ERROR ====> {}", e);
            return ServiceErrorCode.INSERTFAILE.getCode();
        }



    }

}
