package org.ww.mapper;

import java.util.HashMap;
import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.ww.domain.Options;

/**
 * @author YWSnow
 * @date 2022/3/4 16:44
 * @Description:
 */
@Mapper
public interface OptionsMapper extends BaseMapper<Options> {
    List<Options> selectOptionsByQuestionIds(@Param("questionIds") List<String> questionIds);

    HashMap<String, String> selectOptionPairsByQuestionIds(@Param("questionIds") List<String> questionIds);
}
