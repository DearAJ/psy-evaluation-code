package org.ww.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.ww.EntityDO.IdBaseEntity;
import org.ww.EntityDO.PsyIntervationFileDO;
import org.ww.EntityDO.PsyIntervationPlanDO;
import org.ww.EntityDO.PsyIntervationRecordDO;
import org.ww.EntityVO.UserVO;
import org.ww.base.BaseEntity;
import org.ww.domain.Students;
import org.ww.domain.Teachers;
import org.ww.result.Wrapper;

import java.util.List;

public interface TaskService {

    /**
     * 获取全部老师信息
     * @param userVO
     * @return
     */

    public Wrapper<IPage<UserVO>> getAllTeacherInfo(UserVO userVO);

    public Wrapper<IPage<UserVO>> getAllStudentInfo(UserVO userVO);
    /**
     * 查询单个老师信息
     * @param
     * @param start
     * @param size
     * @return
     */
    public UserVO getTeacherInfoById(Long id, Integer start, Integer size);
    /**
     * 查找单个学生信息
     * @param stuId
     * @param start
     * @param size
     * @return
     */
    public UserVO getStudentInfoById(Long stuId, Integer start, Integer size);


}
