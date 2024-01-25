package org.ww.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.TelDoc;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zq
 * @since 2023-05-11
 */
@Mapper
public interface TelDocMapper extends BaseMapper<TelDoc> {

    Integer queryCount(Long telGroupId, String keywords);

    List<TelDoc> query(Long telGroupId, String keywords, int start, Integer size);
}
