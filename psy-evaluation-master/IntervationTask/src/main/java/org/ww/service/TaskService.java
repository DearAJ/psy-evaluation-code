package org.ww.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.ww.EntityDO.IdBaseEntity;
import org.ww.EntityDO.PsyIntervationFileDO;
import org.ww.EntityDO.PsyIntervationPlanDO;
import org.ww.EntityDO.PsyIntervationRecordDO;
import org.ww.EntityVO.IntervationSchemePageVO;
import org.ww.base.BaseEntity;

import java.util.List;

public interface TaskService {
    public List<BaseEntity> readPsyFilesByStuId(Long stuId);

    /**
     * 获取该生全部干预记录
     * @param stuId
     * @param start
     * @param size
     * @return
     */
    public IPage<PsyIntervationRecordDO> getAllRecordByStuId(Long stuId,Integer start, Integer size);
    /**
     * 获得该生全部干预方案
     * @param stuId
     * @param start
     * @param size
     * @return
     */
    public IPage<PsyIntervationPlanDO> getAllPlanByStuId(Long stuId, Integer start, Integer size);
    /**
     * 默认排序的页面
     * 无条件筛选
     * @param start 起始页号(从1开始)
     * @param size  页面大小
     * @return
     */
    public List<BaseEntity> getSchemePageData(int start,int size);
    /**
     * 根据传入的files对象列表来获取Item list
     * 具有条件筛选功能
     * @param
     * @return
     */
    public IPage<BaseEntity> getSchemePageData(IntervationSchemePageVO pageVO);
    public List<BaseEntity> getSchemeItemByFileList(List<PsyIntervationFileDO> res);
    /**
     * 添加一条新的干预记录
     * @param fileId 点击干预时展示的记录的id
     * @param recordDO
     * @return
     * @throws Exception
     */
    public Integer addIntervationRecordOrCrisis(Long userId, Long fileId, PsyIntervationRecordDO recordDO) throws Exception;
    /**
     * 增加plan，但是需要record已经被填写过了
     * @param userId
     * @param fileId
     * @param
     * @return
     * @throws Exception
     */
    public Integer addIntervationPlan(Long userId, Long fileId,PsyIntervationPlanDO planDO) throws Exception;

    /**
     * 学生第一次进入预警库时产生
     * @param
     * @return
     */
    public Integer createIntervationSchemeItem(PsyIntervationFileDO fileDO);


}
