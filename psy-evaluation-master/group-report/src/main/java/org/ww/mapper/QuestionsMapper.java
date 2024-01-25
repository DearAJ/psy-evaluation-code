package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.Questions;

import java.util.Map;

/**
 * @author YWSnow
 * @date 2022/3/4 16:44
 * @Description:
 */
@Mapper
public interface QuestionsMapper extends BaseMapper<Questions> {
}
