package org.ww.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.PrivateMessageDeliver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 站内信发送状态 Mapper 接口
 * </p>
 *
 * @author zq
 * @since 2023-04-21
 */
@Mapper
public interface PrivateMessageDeliverMapper extends BaseMapper<PrivateMessageDeliver> {

    Integer insertBatch(List<PrivateMessageDeliver> delivers);
}
