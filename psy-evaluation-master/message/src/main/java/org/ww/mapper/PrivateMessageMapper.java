package org.ww.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.PrivateMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.ww.dto.PrivateMessageDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 站内信 Mapper 接口
 * </p>
 *
 * @author zq
 * @since 2023-04-21
 */
@Mapper
public interface PrivateMessageMapper extends BaseMapper<PrivateMessage> {

    List<Long> selectUndeliveredMessageIds(String username, Long currentId);

    List<PrivateMessageDto> selectInMessages(String username, int start, int size);

    Map<String, Object> selectPsyintervationInfoByFileId(String fileId);
}
