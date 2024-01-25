package org.ww.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.TelCommonCode;
import org.ww.domain.TelTeacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 心理热线值班老师表 Mapper 接口
 * </p>
 *
 * @author zq
 * @since 2023-05-10
 */
@Mapper
public interface TelTeacherMapper extends BaseMapper<TelTeacher> {

//    List<TelCommonCode> selectCertificatesByTeacher(Long teacherId);
}
