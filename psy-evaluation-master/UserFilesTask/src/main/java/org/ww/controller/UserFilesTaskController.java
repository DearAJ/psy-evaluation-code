package org.ww.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.ww.EntityDO.valueObject.MsgLevel;
import org.ww.EntityDO.valueObject.ServiceErrorCode;
import org.ww.EntityVO.*;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.impl.TaskServiceImpl;

/**
 * @Author lqk
 * @Date 2022/2/10 22:40
 * @Version 1.0
 */

@Slf4j
@RestController
@RequestMapping( "/psyUserFilesTask")
public class UserFilesTaskController {
    private TaskServiceImpl taskService;
    @Autowired
    public UserFilesTaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }



    private Integer checkParams(Object obj){
        if(obj instanceof BaseLevelVO){
            BaseLevelVO userVO = (BaseLevelVO)obj;
            if( userVO.getMsgLevel() == null || userVO.getMsgLevel() == ""){
                return ServiceErrorCode.NULLPARAM.getCode();
            }

            if(userVO.getMsgLevel().equals(MsgLevel.PROVINCE.getVal())){
                if(userVO.getProvince() == null || userVO.getProvince() ==""){
                    return  ServiceErrorCode.NULLPARAM.getCode();
                }
                return 0;
            }else if( userVO.getMsgLevel().equals(MsgLevel.CITY.getVal())){
                if(userVO.getCity() == null || userVO.getCity() ==""){
                    return  ServiceErrorCode.NULLPARAM.getCode();
                }else if(userVO.getProvince() == null || userVO.getProvince() ==""){
                    return ServiceErrorCode.PARAMAERROR.getCode();
                }
                return 0;
            }else if(userVO.getMsgLevel().equals(MsgLevel.COUNTY.getVal())){
                if((userVO.getProvince() == null || userVO.getProvince()=="")
                        ||(userVO.getCounty()==null || userVO.getCounty() == "")
                        ||(userVO.getCity() == null || userVO.getCity()=="")){
                    return ServiceErrorCode.PARAMAERROR.getCode();
                }
                return 0;
            }else if(userVO.getMsgLevel().equals(MsgLevel.SCHOOL.getVal())){
                if((userVO.getSchoolName() == null || userVO.getSchoolName()=="")
                        &&(userVO.getCounty()==null || userVO.getCounty() == "")
                        &&(userVO.getCity() == null || userVO.getCity()=="")
                        ||(userVO.getProvince() == null || userVO.getProvince() == "")){
                    return ServiceErrorCode.PARAMAERROR.getCode();
                }
                return 0;
            }
        }

        return 0;
    }

    @RequestMapping(value = "getAllTeacherInfo")
    public Wrapper<IPage<UserVO>> getAllTeacherInfo(@RequestBody UserVO userVO){
        if( checkParams(userVO) < 0 ){
            return WrapMapper.wrap(Wrapper.BAD_REQUEST, Wrapper.ILLEGAL_ARGUMENT_MESSAGE,null);
        }

        if(userVO.getPageNum() == null || userVO.getPageSize() == null){
            userVO.setPageNum(1);
            userVO.setPageSize(10);
        }

        Wrapper<IPage<UserVO>> res =  taskService.getAllTeacherInfo(userVO);
        return res;
    }

    @RequestMapping(value = "getAllStudentInfo")
    public Wrapper<IPage<UserVO>> getAllStudentInfo(@RequestBody UserVO userVO){
        if( checkParams(userVO) < 0 ){
            return WrapMapper.wrap(Wrapper.BAD_REQUEST, Wrapper.ILLEGAL_ARGUMENT_MESSAGE,null);
        }

        if(userVO.getPageNum() == null || userVO.getPageSize() == null){
            userVO.setPageNum(1);
            userVO.setPageSize(10);
        }

        Wrapper<IPage<UserVO>> res =  taskService.getAllStudentInfo(userVO);
        return res;
    }


    @RequestMapping(value = "getUserDetailInfoById")
    public Wrapper<UserVO> getUserDetailInfoById(@RequestBody UserVO userVO){

        if(userVO.getIdentity().equals("student")){
            userVO.setId(userVO.getStudentId());
        }
        if(userVO.getIdentity().equals("teacher")){
            userVO.setId(userVO.getTeacherId());
        }

        if(  userVO.getId() == null || userVO.getId()<=0 ){
            return WrapMapper.wrap(Wrapper.BAD_REQUEST, Wrapper.ILLEGAL_ARGUMENT_MESSAGE,null);
        }

        if(userVO.getPageNum() == null || userVO.getPageSize() == null){
            userVO.setPageNum(1);
            userVO.setPageSize(10);
        }

        UserDetailVO res =  taskService.getUserDetailInfoById(userVO);

        if(res == null){
            return WrapMapper.wrap(Wrapper.ILLEGAL_ARGUMENT_CODE_, Wrapper.ILLEGAL_ARGUMENT_MESSAGE,res);
        }

        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,res);
    }

}
