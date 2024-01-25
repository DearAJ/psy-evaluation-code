package org.ww.service.ProvinceService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.util.ArrayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ww.EntityDO.ProvinceEntityDO.PRIntervationStatisticDO;
import org.ww.EntityDO.*;
import org.ww.EntityDO.entityFactory.EntityFactory;
import org.ww.EntityDO.valueObject.CrisisLevel;
import org.ww.EntityDO.valueObject.ServiceErrorCode;
import org.ww.EntityVO.IntervationSchemePageVO;
import org.ww.EntityVO.IntervationStatisticVO;
import org.ww.EntityVO.ProvinceEntityVO.PRIntervationSchemePageVO;
import org.ww.base.BaseEntity;
import org.ww.domain.School;
import org.ww.domain.SchoolRole;
import org.ww.domain.Task;
import org.ww.mapper.*;
import org.ww.mapper.ProvicesMapper.PRIntervationStatisticMapper;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service

public class PRService {

    @Autowired
    PsyIntervationFileMapper fileMapper;
    @Autowired
    PsyIntervationPlanMapper psyPlanMapper;
    @Autowired
    PsyIntervationStuMapper stuMapper;
    @Autowired
    PsyIntervationUserMapper userMapper;
    @Autowired
    SchoolRoleMapper schoolRoleMapper;

    @Autowired
    PsyIntervationManagerMapper managerMapper;

    @Autowired
    IntervationStatisticMapper statisticMapper;
    @Autowired
    SchoolMapper schoolMapper;
    @Autowired
    PsyIntervationRecordMapper recordMapper;

    @Autowired
    PRIntervationStatisticMapper prStatisticMapper;

    @Autowired
    TaskMapper taskMapper;

//////////////////////////////////////////////////////////////////////////

    @Autowired
    PRIntervationSchemeServiceImpl prSchemeService;

    @Autowired
    EntityFactory entityFactory;

//////////////////////////////////////////////////////////////////////////
public Wrapper<IPage<IntervationSchemePageVO>> getPrePRSchemePageData(PRIntervationSchemePageVO pageVO) {
    try {
        int start = pageVO.getPageNum();
        int size = pageVO.getPageSize();
        Page<IntervationSchemePageVO> page = new Page<>(start, size);
        Page<IntervationSchemePageVO> results = new Page<>(start, size);
        if (start < 0 || size >= Integer.MAX_VALUE) {
            return WrapMapper.wrap(ServiceErrorCode.ERROEPAGEINFO.getCode(), ServiceErrorCode.ERROEPAGEINFO.getVal(), new Page<>(1,10));
        }

        Boolean isTransfer = pageVO.getIsTransfer();
        String stuState = pageVO.getStuState();
        String stuGrade = pageVO.getStuGrade();
        String stuPeriod = pageVO.getStuPeriod();
        String stuClass = pageVO.getStuClass();
        String crisisLevel = pageVO.getCrisisLevel();
        String stuName = pageVO.getStuName();
        String postProcessing = pageVO.getPostProcessing();
        String semester = pageVO.getSemester();

        //查找school的id，进行过滤，尝试通过schoolName查找，
        // 如果传入了schoolId，那么直接通过schoolId进行查找
//            TODO filter schoolIds by userid
        List schoolIds = new ArrayList<Long>();
        if(pageVO.getSchoolId() == null) {
            schoolIds = schoolMapper.selectObjs(new QueryWrapper<School>()
                    .eq(pageVO.getProvince() == null ? false : true, "province", pageVO.getProvince())
                    .eq(pageVO.getCity() == null ? false : true, "city", pageVO.getCity())
                    .eq(pageVO.getCounty() == null ? false : true, "county", pageVO.getCounty())
                    .eq(pageVO.getSchoolName() == null ? false : true, "name", pageVO.getSchoolName()));
        } else{
            schoolIds.add(pageVO.getSchoolId());
        }
//        预警一次或者判断无危机的，进入初筛库
        results = recordMapper.selectNewWarnIntervationSchemePageVO(page,
                schoolIds,
                isTransfer,
                stuName,
                stuPeriod,
                stuGrade,
                stuClass,
                postProcessing,
                stuState,
                crisisLevel,
                semester,
                null,
                1);
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE,
                Wrapper.SUCCESS_MESSAGE,
                results);

    }catch (Exception e){
        log.error(e.getMessage());
        return WrapMapper.wrap(Wrapper.ERROR_CODE,
                Wrapper.ERROR_MESSAGE,
                new Page<>(1,10));
    }

}

    public Wrapper<IPage<IntervationSchemePageVO>> getWarnPRSchemePageData(PRIntervationSchemePageVO pageVO) {
        try {
            int start = pageVO.getPageNum();
            int size = pageVO.getPageSize();
            Page<IntervationSchemePageVO> page = new Page<>(start, size);
            Page<IntervationSchemePageVO> results = new Page<>(start, size);
            if (start < 0 || size >= Integer.MAX_VALUE) {
                return WrapMapper.wrap(ServiceErrorCode.ERROEPAGEINFO.getCode(), ServiceErrorCode.ERROEPAGEINFO.getVal(), new Page<>(1,10));
            }

            Boolean isTransfer = pageVO.getIsTransfer();
            String stuState = pageVO.getStuState();
            String stuGrade = pageVO.getStuGrade();
            String stuPeriod = pageVO.getStuPeriod();
            String stuClass = pageVO.getStuClass();
            String crisisLevel = pageVO.getCrisisLevel();
            String stuName = pageVO.getStuName();
            String postProcessing = pageVO.getPostProcessing();
            String semester = pageVO.getSemester();

            // feature:超时提醒
            Date expiredTime = null;
            if("1".equals("" + pageVO.getExpired()))
            {
                expiredTime = new Date();
            }

            //查找school的id，进行过滤，尝试通过schoolName查找，
            // 如果传入了schoolId，那么直接通过schoolId进行查找
//            TODO filter schoolIds by userid
            List schoolIds = new ArrayList<Long>();
            if(pageVO.getSchoolId() == null) {
                schoolIds = schoolMapper.selectObjs(new QueryWrapper<School>()
                        .eq(pageVO.getProvince() == null ? false : true, "province", pageVO.getProvince())
                        .eq(pageVO.getCity() == null ? false : true, "city", pageVO.getCity())
                        .eq(pageVO.getCounty() == null ? false : true, "county", pageVO.getCounty())
                        .eq(pageVO.getSchoolName() == null ? false : true, "name", pageVO.getSchoolName()));
            } else{
                schoolIds.add(pageVO.getSchoolId());
            }
            results = recordMapper.selectNewWarnIntervationSchemePageVO(page,
                    schoolIds,
                    isTransfer,
                    stuName,
                    stuPeriod,
                    stuGrade,
                    stuClass,
                    postProcessing,
                    stuState,
                    crisisLevel,
                    semester,
                    expiredTime,
            2);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE,
                    Wrapper.SUCCESS_MESSAGE,
                    results);

        }catch (Exception e){
            log.error(e.getMessage());
            return WrapMapper.wrap(Wrapper.ERROR_CODE,
                    Wrapper.ERROR_MESSAGE,
                    new Page<>(1,10));
        }

    }

    public Wrapper<IPage<IntervationSchemePageVO>> getPRSchemePageData(PRIntervationSchemePageVO pageVO) {
        try {
            int start = pageVO.getPageNum();
            int size = pageVO.getPageSize();
            Page<IntervationSchemePageVO> page = new Page<>(start, size);
            Page<IntervationSchemePageVO> results = new Page<>(start, size);
            if (start < 0 || size >= Integer.MAX_VALUE) {
                return WrapMapper.wrap(ServiceErrorCode.ERROEPAGEINFO.getCode(), ServiceErrorCode.ERROEPAGEINFO.getVal(), new Page<>(1,10));
            }

            Boolean isTransfer = pageVO.getIsTransfer();
            String stuState = pageVO.getStuState();
            String stuGrade = pageVO.getStuGrade();
            String stuPeriod = pageVO.getStuPeriod();
            String stuClass = pageVO.getStuClass();
            String crisisLevel = pageVO.getCrisisLevel();
            String stuName = pageVO.getStuName();
            String postProcessing = pageVO.getPostProcessing();

            //查找school的id，进行过滤，尝试通过schoolName查找，
            // 如果传入了schoolId，那么直接通过schoolId进行查找
//            TODO filter schoolIds by userid
            List schoolIds = new ArrayList<Long>();
            if(pageVO.getSchoolId() == null) {
                schoolIds = schoolMapper.selectObjs(new QueryWrapper<School>()
                        .eq(pageVO.getProvince() == null ? false : true, "province", pageVO.getProvince())
                        .eq(pageVO.getCity() == null ? false : true, "city", pageVO.getCity())
                        .eq(pageVO.getCounty() == null ? false : true, "county", pageVO.getCounty())
                        .eq(pageVO.getSchoolName() == null ? false : true, "name", pageVO.getSchoolName()));
            } else{
                schoolIds.add(pageVO.getSchoolId());
            }
            results = recordMapper.selectIntervationSchemePageVO(page, schoolIds,
                    isTransfer,
                    stuName,
                    stuPeriod,
                    stuGrade,
                    stuClass,
                    postProcessing,
                    stuState,
                    crisisLevel);
//
//            //获取user权限
//            PsyIntervationUserDO user = userMapper.selectById(pageVO.getUserId());
//            if(user == null || user.getUsername() == null){
//                return WrapMapper.wrap(ServiceErrorCode.WRONGUSERINFO.getCode(),
//                        ServiceErrorCode.WRONGUSERINFO.getVal(),
//                        new Page<>(1,10));
//            }
//
//            SchoolRole schoolRole = schoolRoleMapper.selectOne(
//                    new QueryWrapper<SchoolRole>()
//                            .eq("username",user.getUsername()));
//
//            //查找符合条件的item
//            List<PsyIntervationFileDO> fileDOS = fileMapper.selectList(
//                    new QueryWrapper<PsyIntervationFileDO>()
//                            .orderByDesc("create_time"));
//
//            if(fileDOS == null || fileDOS.size() == 0){
//                results.setRecords(new ArrayList<>());
//                return WrapMapper.wrap(ServiceErrorCode.FINDNULL.getCode(),
//                        ServiceErrorCode.FINDNULL.getVal(),
//                        results);
//            }
//
//
//            //获取危机确认信息
//            //过滤掉不存在危机确认信息的用户
//            fileDOS = fileDOS.stream().filter(e->e.getPsyIntervationCrisisId() == null).collect(Collectors.toList());
//
//            List<Long> fIds = fileDOS.stream().map(e->{
//                if(Objects.isNull(e) || Objects.isNull(e.getId())) return -1L;
//                return e.getId();
//            }).collect(Collectors.toList());
//            fIds.add(-1L);
//
//            //查找与fileItem相关的干预记录
//            List<PsyIntervationRecordDO> recordDOS = recordMapper.selectList(new QueryWrapper<PsyIntervationRecordDO>()
//                    .in("file_id",fIds)
//                    .eq(pageVO.getCrisisLevel() == null ? false : true,"crisis_level",pageVO.getCrisisLevel())
//                    .eq(pageVO.getIsTransfer() == null ? false : true,"is_transfer",pageVO.getIsTransfer())
//                    .eq(pageVO.getPostProcessing() == null ? false : true,"post_processing",pageVO.getPostProcessing())
//                    .eq(pageVO.getStuState() == null ? false : true,"stu_state",pageVO.getStuState())
//                    .and(w->w.eq("is_submit",true))
//                    .or(ww->ww.in("file_id",fIds)
//                            .eq("is_submit",false)
//                            .eq("user_id",pageVO.getUserId()))
//                    .orderByDesc("file_id","id")
//                    .last("limit 1")
//            );
//
//            if(recordDOS == null || recordDOS.size() == 0) {
//                return WrapMapper.wrap(ServiceErrorCode.FINDNULL.getCode(),
//                        ServiceErrorCode.FINDNULL.getVal(),
//                        results);
//            }
//
//            //要求预警库内的预警学生按照最旧的预警信息来显示
//            recordDOS.sort((a,b)->{
//                if(a.getCreateTime().getTime() < b.getCreateTime().getTime() && a.getFileId() == b.getFileId()){
//                    return -1;
//                }else if(a.getCreateTime().getTime() > b.getCreateTime().getTime() && a.getFileId() == b.getFileId()){
//                    return 1;
//                }
//                return 0;
//            });
//
//            //去重,无危机学生，如果有危机，则显示最老的；如果无危机，不显示干预截至时间
//            Long lastFileId = -1L;
//            Boolean isCrisris = false;
//            List<PsyIntervationRecordDO> records = new ArrayList<>();
//            for(PsyIntervationRecordDO recordDO: recordDOS){
//                if(recordDO.getFileId().equals(lastFileId) == false){
//                    if(recordDO.getCrisisLevel().equals(CrisisLevel.DLEAVEL.getVal())){
//                        recordDO.setIntervationExpireTime(null);
//                    }
//                    records.add(recordDO);
//                    lastFileId = recordDO.getFileId();
//                    isCrisris = true;
//                }else if(records.size() >0
//                        &&records.get(records.size()-1).getCrisisLevel().equals(CrisisLevel.DLEAVEL.getVal())
//                        && recordDO.getCrisisLevel().equals(CrisisLevel.DLEAVEL.getVal()) == false
//                        && isCrisris == true){
//                    records.remove(records.size()-1);
//                    records.add(recordDO);
//                    isCrisris = false;
//                }
//            }
//
//
//
//            List<PsyIntervationStuDO> students = null;
//            //通过传入的条件查找对应学校的符合条件的未删除学生
//            //@ TODO: 2022/6/21  待增加毕业字段检查
//
//            List<Long> filesStudIds = fileDOS.stream().map(e->{
//                if(Objects.isNull(e) || Objects.isNull(e.getStudentId())) return -1L;
//                return e.getStudentId();
//            }).collect(Collectors.toList());
//            filesStudIds.add(-1L);
//
//
//            if(pageVO.getMsgLevel().equals("province") || pageVO.getMsgLevel().equals("city") || pageVO.getMsgLevel().equals("county") ) {
//                IPage<PsyIntervationStuDO> studentsPage = stuMapper.selectPage(new Page<PsyIntervationStuDO>(start, size),
//                        new QueryWrapper<PsyIntervationStuDO>()
//                                .eq(pageVO.getStuName() == null ? false : true, "name", pageVO.getStuName())
//                                .eq(pageVO.getStuPeriod() == null ? false : true, "period", pageVO.getStuPeriod())
//                                .eq(pageVO.getStuGrade() == null ? false : true, "grade", pageVO.getStuGrade())
//                                .eq(pageVO.getStuClass() == null ? false : true, "classes", pageVO.getStuClass())
//                                .select("id", "classes")
//                                .in(schoolIds.size() == 0 ? false : true, "school_id", schoolIds)
//                                .in("id",filesStudIds)
//                                .eq("deleted", 0)
//                );
//
//                students = studentsPage.getRecords();
//            }else{
//                students = stuMapper.selectList(
//                        new QueryWrapper<PsyIntervationStuDO>()
//                                .eq(pageVO.getStuName() == null ? false : true, "name", pageVO.getStuName())
//                                .eq(pageVO.getStuPeriod() == null ? false : true, "period", pageVO.getStuPeriod())
//                                .eq(pageVO.getStuGrade() == null ? false : true, "grade", pageVO.getStuGrade())
//                                .eq(pageVO.getStuClass() == null ? false : true, "classes", pageVO.getStuClass())
//                                .select("id", "classes")
//                                .in(schoolIds.size() == 0 ? false : true, "school_id", schoolIds)
//                                .in("id",filesStudIds)
//                                .eq("deleted", 0)
//                );
//            }
//
//            if(students == null || students.size() == 0){
//                return WrapMapper.wrap(ServiceErrorCode.FINDNULL.getCode(),
//                        ServiceErrorCode.FINDNULL.getVal(),
//                        results);
//            }
//
//
//            //如果是学校级别的查询，根据classes过滤学生id
//            //市县级别的在查询学校的时候就过滤了
//            //校权限在登陆的时候就过滤了
//            List<PsyIntervationStuDO> studentFilterd = new ArrayList<>();
//            if( pageVO.getMsgLevel().equals("school")) {
//                if(schoolRole.getClasses() == null){
//                    return WrapMapper.wrap(ServiceErrorCode.WRONGUSERINFO.getCode(),
//                            ServiceErrorCode.WRONGUSERINFO.getVal(),
//                            new Page<>(1,10));
//                }
//                schoolRole.setClasses(schoolRole.getClasses()+",");
//                String[] classes = schoolRole.getClasses().split(",");
//                HashSet<String> classesSet = new HashSet(Arrays.asList(classes));
//                studentFilterd = students.stream()
//                        .filter(e -> classesSet.contains(e.getClasses()))
//                        .collect(Collectors.toList());
//            }else{
//                studentFilterd = students;
//            }
//
//            List stuIds = studentFilterd.stream()
//                    .map(e->{ return e == null ? -1 :e.getId();})
//                    .collect(Collectors.toList());
//
//            //没有查到对应条件的学生
//            if(stuIds == null || stuIds.size() == 0){
//                results.setRecords(new ArrayList<>());
//                return WrapMapper.wrap(ServiceErrorCode.FINDNULL.getCode(),
//                        ServiceErrorCode.FINDNULL.getVal(),
//                        results);
//
//            }
//
//            List<IntervationSchemePageVO> res = getPRSchemeItemByFileList(fileDOS,records,studentFilterd);
//            if(res == null) {
//                return WrapMapper.wrap(ServiceErrorCode.FINDNULL.getCode(),
//                        ServiceErrorCode.FINDNULL.getVal(),
//                        results);
//            }
//
//
//            //获取schoolid和学校关系
//            HashMap<Long,School> schoolIdAndSchool = getSchoolIdAndChool(pageVO);
//            if(schoolIdAndSchool.size() == 0) {
//                return WrapMapper.wrap(ServiceErrorCode.FINDNULL.getCode(),
//                        ServiceErrorCode.FINDNULL.getVal(),
//                        new Page<>(1,10));
//            }
//
//
//            //添加学校信息和task名称
//            for(BaseEntity obj : res){
//                if(obj instanceof IntervationSchemePageVO){
//                    IntervationSchemePageVO p = (IntervationSchemePageVO) obj;
//                    Long schID = p.getSchoolId();
//                    if(schoolIdAndSchool.containsKey(schID)){
//                        School school = schoolIdAndSchool.get(schID);
//                        p.setProvince(school.getProvince());
//                        p.setCity(school.getCity());
//                        p.setCounty(school.getCounty());
//                        p.setSchoolName(school.getName());
//                    }else{
//                        p.setSchoolName(" ");
//                    }
//
//                }
//            }
//
//
//            res.sort((o1, o2) -> {
//                if(o1.getCreateTime()==null && o2.getCreateTime() != null){
//                    return 1;
//                }
//                if(o1.getCreateTime()!=null && o2.getCreateTime() == null){
//                    return -1;
//                }
//                if(o1.getCreateTime().getTime() > o2.getCreateTime().getTime()){
//                    return -1;
//                }else if(o1.getCreateTime().getTime() < o2.getCreateTime().getTime()){
//                    return 1;
//                }else {
//                    return 0;
//                }
//            });
//
//            if(pageVO.getIsTransfer() != null && "".equals(pageVO.getIsTransfer()) == false){
//               res =  res.stream().filter(
//                       e->{
//                           if(Objects.isNull(e.getIsTransfer()) || "".equals(pageVO.getIsTransfer()) ){
//                               return false;
//                           }
//                           return e.getIsTransfer().equals(pageVO.getIsTransfer());
//                       }
//               ).collect(Collectors.toList());
//            }
//            if(pageVO.getPostProcessing() != null&& "".equals(pageVO.getPostProcessing()) == false){
//                res =  res.stream().filter(
//                        e->{
//                            if(Objects.isNull(e.getPostProcessing()) || "".equals(pageVO.getPostProcessing()) ){
//                                return false;
//                            }
//                            return e.getPostProcessing().equals(pageVO.getPostProcessing());
//                        }
//                ).collect(Collectors.toList());
//            }
//            if(pageVO.getCrisisLevel() != null && "".equals(pageVO.getCrisisLevel()) == false){
//                res =  res.stream().filter(
//                        e->{
//                            if(Objects.isNull(e.getCrisisLevel()) || "".equals(pageVO.getCrisisLevel()) ){
//                                return false;
//                            }
//                            return e.getCrisisLevel().equals(pageVO.getCrisisLevel());
//                        }
//                ).collect(Collectors.toList());
//            }
//            if(pageVO.getStuState() != null && "".equals(pageVO.getStuState()) == false){
//                res =  res.stream().filter(
//                        e->{
//                            if(Objects.isNull(e.getStuState()) || "".equals(pageVO.getStuState()) ){
//                                return false;
//                            }
//                            return e.getStuState().equals(pageVO.getStuState());
//                        }
//                ).collect(Collectors.toList());
//            }
//
//            //总数
//            Integer total = res.size();
//            results.setRecords(res.subList((start*size - size) < 0 ? 0 :(start*size - size) , start * size > res.size() ? res.size():start * size ));
//            results.setTotal(total);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE,
                    Wrapper.SUCCESS_MESSAGE,
                    results);

        }catch (Exception e){
            log.error(e.getMessage());
            return WrapMapper.wrap(Wrapper.ERROR_CODE,
                    Wrapper.ERROR_MESSAGE,
                    new Page<>(1,10));
        }

    }

    public List<IntervationSchemePageVO> getPRSchemeItemByFileList(List<PsyIntervationFileDO> fileDOS,
                                                                   List<PsyIntervationRecordDO> recordDOS,
                                                                   List<PsyIntervationStuDO> stuDOS){
        //uid 和学生实体对应关系
        HashMap<Long,PsyIntervationStuDO> sidAndStu = new HashMap<>();
        for(PsyIntervationStuDO stuDO : stuDOS){
            sidAndStu.put(stuDO.getId(),stuDO);
        }



        //生成fileId和记录的map
        HashMap<Long,PsyIntervationRecordDO> fidAndRecord = new HashMap<>();
        for(PsyIntervationRecordDO recordDO : recordDOS){
            fidAndRecord.put(recordDO.getFileId(),recordDO);
        }

        //获取uid和managerName的关系
        List<Long> uids = recordDOS.stream().map(
                (e)->{
                    if(e == null) return -1L;
                    return e.getUserId();
                }
        ).collect(Collectors.toList());
        uids.add(-1L);

        List<PsyIntervationUserDO> users = uids.size() == 0 ? new ArrayList<>() : userMapper.selectList(new QueryWrapper<PsyIntervationUserDO>()
                .in("id",uids)
                .eq("deleted",0));

        //根据userName 获取 manager
        List unames = users.stream().map(
                (e)->{
                    if(e == null) return "";
                    return e.getUsername();
                }
        ).collect(Collectors.toList());
        unames.add( " ");

        List managers = uids.size() == 0 ? new ArrayList<>() : managerMapper.selectList(new QueryWrapper<PsyIntervationManagerDO>()
                .in("username",unames)
                .eq("deleted",0));

        HashMap<Long,String> uidAndManagerName = uids.size() == 0 ? new HashMap<>() : getUserIdAndManagerName(users,managers);

        //获取taskId 和task名字关系
        HashMap<Long,Task> taskIdAndTask = new HashMap<>();
        List taskIds = fileDOS.stream().map(
                e->{
                    if(e == null) return -1L;
                    return e.getTaskId();
                }
        ).collect(Collectors.toList());
        taskIds.add(-1L);
        List<Task> tasks = taskMapper.selectList(new QueryWrapper<Task>()
                .select("id","name")
                .in("id",taskIds));
        for(Task task:tasks){
            taskIdAndTask.put(task.getId(),task);
        }

        //根据sid和学生的map、uid和managerName的关系、fid和record的关系，组装展示记录
        List<IntervationSchemePageVO> results =getAllPRIntervationSchemePageVO(
                sidAndStu,
                uidAndManagerName,
                fidAndRecord,
                taskIdAndTask,
                fileDOS);

        return results;

    }

    /**
     * 获取uid和manager的名字对应map
     * @param
     * @param managers
     * @return
     */
    public HashMap<Long,String> getUserIdAndManagerName(List<PsyIntervationUserDO> users,
                                                        List<PsyIntervationManagerDO> managers){

        HashMap<Long,String> uidAndName = new HashMap<>();
        for(PsyIntervationUserDO u : users){
            uidAndName.put(u.getId(),u.getUsername());
        }

        HashMap<String,String> unameAndName = new HashMap<>();
        for(PsyIntervationManagerDO m: managers){
            unameAndName.put(m.getUsername(),m.getName());
        }

        HashMap<Long,String> uidAndMName = new HashMap<>();
        for( Long k: uidAndName.keySet()){
            String key = uidAndName.get(k);
            //如果不包含将返回userId的 id值
            uidAndMName.put(k, unameAndName.containsKey(key) ? unameAndName.get(key):k.toString());
        }

        return uidAndMName;
    }

//    public List<PsyPRIntervationSchemeDTO>  getAllPRIntervationSchemeDTO(
//            List<PsyIntervationRecordDO> recordDOS,
//            List<PsyIntervationStuDO> stuDOS,
//            List<PsyIntervationManagerDO> managerDOS,
//            List<PsyIntervationUserDO> userDOS,
//            List<PsyIntervationFileDO> fileDOS){
//
//        List<PsyPRIntervationSchemeDTO> res = new ArrayList<>();
//        HashMap<Long,PsyIntervationRecordDO> recordDOHashMap = new HashMap<>();
//        HashMap<Long,PsyIntervationStuDO> stuDOHashMap = new HashMap<>();
//        HashMap<Long,PsyIntervationUserDO> userDOHashMap = new HashMap<>();
//        HashMap<String,PsyIntervationManagerDO> managerDOHashMap = new HashMap<>();
//
//        for(PsyIntervationRecordDO r : recordDOS){
//            recordDOHashMap.put(r.getFileId(),r);
//        }
//        for(PsyIntervationStuDO s : stuDOS){
//            stuDOHashMap.put(s.getId(),s);
//        }
//
//        for(PsyIntervationManagerDO m : managerDOS){
//            managerDOHashMap.put(m.getUsername(),m);
//        }
//
//        for( PsyIntervationUserDO u : userDOS){
//            userDOHashMap.put(u.getId(),u);
//        }
//        for(PsyIntervationFileDO f: fileDOS){
//            res.add( PsyPRIntervationSchemeDTO.builder()
//                    .recordDO(recordDOHashMap.containsKey(f.getId()).recordDOHashMap.get(f.getId()))
//                    .studentDO(stuDOHashMap.get(f.getStudentId()))
//                    .teacherDO(
//                            managerDOHashMap.get(
//                                    userDOHashMap.get(
//                                            recordDOHashMap.get(f.getId()).getUserId())
//                                            .getUsername()))
//                    .build());
//        }
//        return res;
//
//    }

    /**
     * 根据sid和学生的map、uid和managerName的关系、fid和record的关系，组装展示记录
     * @param sidAstu
     * @param uidAManagerName
     * @param fidARecord
     * @param fileDOS
     * @return
     */
    List<IntervationSchemePageVO> getAllPRIntervationSchemePageVO(HashMap<Long,PsyIntervationStuDO> sidAstu,
                                                                  HashMap<Long,String> uidAManagerName,
                                                                  HashMap<Long,PsyIntervationRecordDO> fidARecord,
                                                                  HashMap<Long,Task> taskIdAndTask,
                                                                  List<PsyIntervationFileDO> fileDOS){

        List<IntervationSchemePageVO> res = new ArrayList<>();
        String info = "未获取到具体信息，id为：";
        for(PsyIntervationFileDO fileDO:fileDOS){

            IntervationSchemePageVO pageVO = new IntervationSchemePageVO();

            //设置itemId
            pageVO.setFileId(fileDO.getId());


            if(fileDO.getStudentId() != null) {
                //设置学生信息
                String stuPeriod = info + fileDO.getStudentId().toString();
                String stuClass = info + fileDO.getStudentId().toString();
                String stuGrade = info + fileDO.getStudentId().toString();
                String stuName = info + fileDO.getStudentId().toString();
                pageVO.setSchoolId(-1L);
                if (sidAstu.containsKey(fileDO.getStudentId()) == true) {
                    stuPeriod = sidAstu.get(fileDO.getStudentId()).getPeriod();
                    stuClass = sidAstu.get(fileDO.getStudentId()).getClasses();
                    stuGrade = sidAstu.get(fileDO.getStudentId()).getGrade();
                    stuName = sidAstu.get(fileDO.getStudentId()).getName();
                    pageVO.setSchoolId(sidAstu.get(fileDO.getStudentId()).getSchoolId());
                }
                pageVO.setStuId(fileDO.getStudentId());
                pageVO.setStuName(stuName);
                pageVO.setStuClass(stuClass);
                pageVO.setStuGrade(stuGrade);
                pageVO.setStuPeriod(stuPeriod);
            }else{
                log.error("学生信息为空，请检查数据库，fileItem id 为："+fileDO.getId());
            }




            //设置任务信息
            if(taskIdAndTask.containsKey(fileDO.getTaskId())) {
                pageVO.setTaskName(taskIdAndTask.get(fileDO.getTaskId()).getName());
            }else{
                pageVO.setTaskName(fileDO.getTaskId() == null ? "" : fileDO.getTaskId().toString());
            }

            if(fidARecord.containsKey(fileDO.getId()) == false){
                pageVO.setCreateTime(fileDO.getCreateTime());
                res.add(pageVO);
                continue;
            }

            PsyIntervationRecordDO recordDO = fidARecord.get(fileDO.getId());
            //设置心里老是信息
            String teacherName = uidAManagerName.containsKey(fileDO.getUserId()) ?
                    uidAManagerName.get(fileDO.getUserId()): info+fileDO.getUserId().toString();
            pageVO.setPsyTeacherName(teacherName);
            pageVO.setCreateTime(recordDO.getCreateTime() == null ?  recordDO.getIntervationTime() : recordDO.getCreateTime());
            pageVO.setStuState(recordDO.getStuState());
            pageVO.setExpireTime(recordDO.getIntervationExpireTime());
            pageVO.setCrisisLevel(recordDO.getCrisisLevel());
            pageVO.setIsTransfer(recordDO.getIsTransfer());
            pageVO.setUserId(fileDO.getUserId());
            pageVO.setIntervationContent(recordDO.getIntervationRecord());
            pageVO.setPostProcessing(recordDO.getPostProcessing());


            res.add(pageVO);
        }



        return res;
    }

    /**
     * 获取统计结果
     * @param statisticVO
     * @return
     */
    public IPage<BaseEntity> getPRIntervationStaRes(IPage<PRIntervationStatisticDO> page, IntervationStatisticVO statisticVO){

        try {
            IPage<PRIntervationStatisticDO> statisticDOS = prStatisticMapper.selectStatisticRes(page,
                    statisticVO.getProvince(),
                    statisticVO.getCity(),
                    statisticVO.getCounty(),
                    statisticVO.getSchoolName(),
                    statisticVO.getSchoolId(),
                    statisticVO.getPeriod(),
                    statisticVO.getGrade(),
                    statisticVO.getClasses()
                    );

            List statisticVOs = prSchemeService.getPRIntervationStatisticVOs(statisticDOS.getRecords());

            IPage<BaseEntity> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
            result.setRecords(statisticVOs);

            return result;
        }catch (Exception e){
            log.error("ERROR ====> {}", e);
            return new Page<BaseEntity>();
        }
    }

    public HashMap<Long,School> getSchoolIdAndChool(PRIntervationSchemePageVO userVO){
        if( userVO == null){
            return new HashMap<>();
        }

        List<School> schools = schoolMapper.selectList(new QueryWrapper<School>()
                .eq(userVO.getProvince() == null ? false : true, "province", userVO.getProvince())
                .eq(userVO.getCity() == null ? false : true, "city", userVO.getCity())
                .eq(userVO.getCounty() == null ? false : true, "county", userVO.getCounty())
                .eq(userVO.getSchoolName() == null ? false : true, "name", userVO.getSchoolName())
                .select("id","province","city","county","name"));

        //获取schoolId和school实体关系
        HashMap<Long,School> schoolIdAndSchool = new HashMap<>();
        schools.stream().forEach((e)->{
            if(e!=null && e.getId() != null){
                schoolIdAndSchool.put(e.getId(),e);
            }
        });


        return schoolIdAndSchool;
    }
}


