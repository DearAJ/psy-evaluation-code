package org.ww.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.TelBlacklist;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.ww.dto.BlacklistDto;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zq
 * @since 2023-05-07
 */
@Mapper
public interface TelBlacklistMapper extends BaseMapper<TelBlacklist> {

    List<BlacklistDto> selectBlackAndUserInfo(Long telGroupId, String phone, int start, int size);
}
