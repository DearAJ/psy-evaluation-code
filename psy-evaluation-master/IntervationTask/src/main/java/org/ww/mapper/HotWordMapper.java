package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ww.EntityDO.HotWord;
import org.ww.domain.School;

@Mapper
public interface HotWordMapper extends BaseMapper<HotWord> {
}
