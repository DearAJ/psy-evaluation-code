package org.ww.mapper;

import java.util.HashMap;
import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.ww.domain.Questions;

/**
 * @author YWSnow
 * @date 2022/3/4 16:44
 * @Description:
 */
@Mapper
public interface QuestionsMapper extends BaseMapper<Questions> {
    List<Questions> selectQuestionsByTaskIds(@Param("taskIds") List<String> taskIds);

    HashMap<String, String> selectQuestionPairsByTaskIds(@Param("taskIds") List<String> taskIds);
}
