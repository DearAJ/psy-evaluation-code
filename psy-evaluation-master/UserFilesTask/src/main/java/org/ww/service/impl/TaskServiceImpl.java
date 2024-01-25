package org.ww.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.ww.Adapter.DOToUserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ww.EntityDO.valueObject.ServiceErrorCode;
import org.ww.EntityVO.UserDetailVO;
import org.ww.EntityVO.UserVO;
import org.ww.domain.School;
import org.ww.domain.Students;
import org.ww.domain.Teachers;
import org.ww.domain.User;
import org.ww.mapper.*;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.TaskService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    StudentsMapper studentsMapper;
    @Autowired
    TeachersMapper teachersMapper;
    @Autowired
    SchoolMapper schoolMapper;

    @Autowired
    DOToUserVO doToUserVO;

    @Autowired
    UserMapper userMapper;


    @Override
    public Wrapper<IPage<UserVO>> getAllStudentInfo(UserVO userVO) {

        //获取school信息
        HashMap<Long,School> schoolIdAndSchool = getSchoolIdAndChool(userVO);
        if(schoolIdAndSchool.size() == 0) {
            return WrapMapper.wrap(ServiceErrorCode.FINDNULL.getCode(),
                    ServiceErrorCode.FINDNULL.getVal(),
                    new Page<>(1,10));
        }
        List schoolIds = Arrays.asList(schoolIdAndSchool.keySet().toArray());

        QueryWrapper<Students> query = new QueryWrapper<Students>()
                .in("school_id",schoolIds)
                .eq(userVO.getPeriod() == null ? false : true, "period",userVO.getPeriod())
                .eq(userVO.getGrade() == null ? false : true,"grade",userVO.getGrade())
                .eq(userVO.getName() == null ? false : true,"name",userVO.getName())
                .eq(userVO.getClasses() == null ? false : true,"classes",userVO.getClasses())
                .eq(userVO.getIDCard() == null ? false : true,"id_number",userVO.getIDCard())
                .eq("deleted",0);
        if(StringUtils.isBlank(userVO.getIDCard()) && StringUtils.isBlank(userVO.getName()))
        {
            query.eq("archived", 0);
        }else {
            query.and(q -> q.eq("archived", 0).or().eq("graduated", 1));
        }

        IPage<Students> studentsIPage = studentsMapper.selectPage(new Page<>(userVO.getPageNum(),userVO.getPageSize()),
                query);
        long total = studentsIPage.getTotal();



        //获取学生的 userVOS
        List<UserVO> userVOS = doToUserVO.studentToUserVO(studentsIPage.getRecords());

        //获取sid和student实体的关系
        HashMap<Long,Students> sidAndStudent = new HashMap<>();
        for(Students s: studentsIPage.getRecords()){
            sidAndStudent.put(s.getId(),s);
        }


        List<String> stuUserNames = studentsIPage.getRecords().stream().map(
                (e)->{
                    if( e == null) return " ";
                    return e.getUsername();
                }
        ).collect(Collectors.toList());
        stuUserNames.add(" ");

        List<User> users = userMapper.selectList(new QueryWrapper<User>()
                .in("username",stuUserNames)
                .select("id","username"));

        //组装username和userId
        HashMap<String,Long> unameAndUid = new HashMap<>();
        for( User u : users){
            unameAndUid.put(u.getUsername(),u.getId());
        }

        //增加userId和学校信息
        for(UserVO user : userVOS){
            Long key = -1L;
            //如果user 包含学生，并且学生的username在unameAndUid中存在
            if(( key = user.getStudentId() ) > 0
                    && sidAndStudent.containsKey(key) == true
                    && unameAndUid.containsKey(sidAndStudent.get(key).getUsername()) == true){
                user.setUserId(unameAndUid.get( sidAndStudent.get(key).getUsername() ));
            }
            Long schoolId = Long.valueOf(user.getSchoolId());
            if(schoolIdAndSchool.containsKey(schoolId)){
                School tempSchool= schoolIdAndSchool.get(schoolId);
                user.setProvince(tempSchool.getProvince());
                user.setCity(tempSchool.getCity());
                user.setCounty(tempSchool.getCounty());
                user.setSchoolName(tempSchool.getName());
            }
        }


        IPage<UserVO> resPage = new Page<>(studentsIPage.getCurrent(),studentsIPage.getSize());
        resPage.setRecords( userVOS );
        resPage.setTotal(total);

        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,resPage);
    }

    @Override
    public Wrapper<IPage<UserVO>> getAllTeacherInfo(UserVO userVO) {
        try {

            HashMap<Long,School> schoolIdAndSchool = getSchoolIdAndChool(userVO);
            if(schoolIdAndSchool.size() == 0) {
                return WrapMapper.wrap(ServiceErrorCode.FINDNULL.getCode(),
                        ServiceErrorCode.FINDNULL.getVal(),
                        new Page<>(1,10));
            }
            List schoolIds = Arrays.asList(schoolIdAndSchool.keySet().toArray());

            IPage<Teachers> teachersIPage = teachersMapper.selectPage(new Page<>(userVO.getPageNum(), userVO.getPageSize()),
                    new QueryWrapper<Teachers>().
                            in("school_id", schoolIds)
                            .eq(userVO.getName() == null ? false : true, "name", userVO.getName())
                            .eq(userVO.getPhoneNumber() == null ? false : true, "phone", userVO.getPhoneNumber())
                            .eq(userVO.getIDCard() == null ? false : true, "id_number", userVO.getIDCard())
                            .eq("deleted",0));

            Integer total = teachersMapper.selectCount(
                    new QueryWrapper<Teachers>().
                            in("school_id", schoolIds)
                            .eq(userVO.getName() == null ? false : true, "name", userVO.getName())
                            .eq(userVO.getPhoneNumber() == null ? false : true, "phone", userVO.getPhoneNumber())
                            .eq(userVO.getIDCard() == null ? false : true, "id_number", userVO.getIDCard())
                            .eq("deleted",0));



            IPage<UserVO> resPage = new Page<>(teachersIPage.getCurrent(), teachersIPage.getSize());
            //获取老师de userVOS
            List<UserVO> userVOS = doToUserVO.teacherToUserVO(teachersIPage.getRecords());

            //根据老师id信息获取对应的userId
            List<String> teacherNames = teachersIPage.getRecords().stream().map(
                    (e)->{
                        if( e == null) return " ";
                        return e.getUsername();
                    }
            ).collect(Collectors.toList());

            teacherNames.add(" ");

            List<User> users = userMapper.selectList(new QueryWrapper<User>()
                    .in("username",teacherNames)
                    .select("id","username"));

            //获取tid和Teacher实体的关系
            HashMap<Long,Teachers> tidAndTeacher = new HashMap<>();
            for(Teachers t: teachersIPage.getRecords()){
                tidAndTeacher.put(t.getId(),t);
            }


            //组装username和userId
            HashMap<String,Long> unameAndUid = new HashMap<>();
            for( User u : users){
                unameAndUid.put(u.getUsername(),u.getId());
            }

            //增加学校信息和user信息
            for(UserVO user : userVOS){
                Long key = -1L;
                //如果user 包含教师，并且教师的username在unameAndUid中存在
                if(( key = user.getTeacherId() ) > 0
                        && tidAndTeacher.containsKey(key) == true
                        && unameAndUid.containsKey(tidAndTeacher.get(key).getUsername()) == true){
                    user.setUserId(unameAndUid.get( tidAndTeacher.get(key).getUsername() ));
                }
                Long schoolId = Long.valueOf(user.getSchoolId());
                if(schoolIdAndSchool.containsKey(schoolId)){
                    School tempSchool= schoolIdAndSchool.get(schoolId);
                    user.setProvince(tempSchool.getProvince());
                    user.setCity(tempSchool.getCity());
                    user.setCounty(tempSchool.getCounty());
                    user.setSchoolName(tempSchool.getName());
                }
            }




            resPage.setRecords(userVOS);
            resPage.setTotal(total);

            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,resPage);

        }catch (Exception e){
            log.error("ERROR ====> {}", e);
            return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE,new Page(1,10));
        }

    }

    @Override
    public UserVO getTeacherInfoById(Long id, Integer start, Integer size) {
        return doToUserVO.teacherToUserVO(teachersMapper.selectById(id));
    }

    @Override
    public UserVO getStudentInfoById(Long stuId, Integer start, Integer size) {
        return doToUserVO.studentToUserVO(studentsMapper.selectById(stuId));
    }


    public UserDetailVO getUserDetailInfoById(UserVO userVO){
        if(userVO.getIdentity().equals("student")){
            return doToUserVO.teacherToUserDetailVO( studentsMapper.selectById(userVO.getId()));
        }else if( userVO.getIdentity().equals("teacher")){
            return doToUserVO.teacherToUserDetailVO( teachersMapper.selectById(userVO.getId()));
        }
        return null;
    }

    public HashMap<Long,School> getSchoolIdAndChool(UserVO userVO){
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
