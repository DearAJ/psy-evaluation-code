package org.ww.mapper;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.ww.EntityDO.PsyIntervationRecordDO;
import org.ww.EntityVO.IntervationSchemePageVO;
import org.ww.base.BaseEntity;

/**
 * @author
 * @date 2022/3/10 17:39
 * @Description: 获取详细干预记录
 */
@Mapper
public interface PsyIntervationRecordMapper extends BaseMapper<PsyIntervationRecordDO> {
    List<BaseEntity> selectByCrisisLevel(@Param("cLevel") String cLevel);
    List<Long> selectExpireStudentIds(@Param("schoolIds") List<Long> schoolIds);

    List<BaseEntity> selectLatestByGroup(@Param("cLevel") String cLevel,
                                         @Param("postProcessiong") String postProcessing,
                                         @Param("stuState") String stuState,
                                         @Param("isTransfer")Boolean isTransfer,
                                         @Param("fileIds")List<Long> fileIds);
    Page<IntervationSchemePageVO> selectIntervationSchemePageVO(@Param("page") IPage page,
                                                                   @Param("schoolIds") List<String> schoolIds, @Param("isTransfer") Boolean isTransfer,
                                                                   @Param("stuName") String stuName,
                                                                   @Param("stuPeriod") String stuPeriod,
                                                                   @Param("stuGrade") String stuGrade,
                                                                   @Param("stuClass") String stuClass,
                                                                   @Param("postProcessing") String postProcessing,
                                                                   @Param("stuState") String stuState,
                                                                   @Param("crisisLevel") String crisisLevel);
    Page<IntervationSchemePageVO> selectPreIntervationSchemePageVO(@Param("page") IPage page,
            @Param("schoolIds") List<String> schoolIds, @Param("isTransfer") Boolean isTransfer,
                                                                @Param("stuName") String stuName,
                                                                @Param("stuPeriod") String stuPeriod,
                                                                @Param("stuGrade") String stuGrade,
                                                                @Param("stuClass") String stuClass,
                                                                @Param("postProcessing") String postProcessing,
                                                                @Param("stuState") String stuState,
                                                                @Param("crisisLevel") String crisisLevel,
                                                                @Param("semester") String semester);

    Page<IntervationSchemePageVO> selectNewWarnIntervationSchemePageVO(@Param("page") IPage page,
                                                                   @Param("schoolIds") List<String> schoolIds, @Param("isTransfer") Boolean isTransfer,
                                                                   @Param("stuName") String stuName,
                                                                   @Param("stuPeriod") String stuPeriod,
                                                                   @Param("stuGrade") String stuGrade,
                                                                   @Param("stuClass") String stuClass,
                                                                   @Param("postProcessing") String postProcessing,
                                                                   @Param("stuState") String stuState,
                                                                   @Param("crisisLevel") String crisisLevel,
                                                                   @Param("semester") String semester,
                                                                   @Param("expiredTime") Date expiredTime,
                                                                   @Param("counter") Integer counter);

    Page<IntervationSchemePageVO> selectWarnIntervationSchemePageVO(@Param("page") IPage page,
                                                                @Param("schoolIds") List<String> schoolIds, @Param("isTransfer") Boolean isTransfer,
                                                                @Param("stuName") String stuName,
                                                                @Param("stuPeriod") String stuPeriod,
                                                                @Param("stuGrade") String stuGrade,
                                                                @Param("stuClass") String stuClass,
                                                                @Param("postProcessing") String postProcessing,
                                                                @Param("stuState") String stuState,
                                                                @Param("crisisLevel") String crisisLevel,
                                                                @Param("semester") String semester,
                                                                @Param("expiredTime") Date expiredTime);

}
