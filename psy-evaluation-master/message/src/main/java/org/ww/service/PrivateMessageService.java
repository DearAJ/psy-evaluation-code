package org.ww.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.ww.dto.PrivateMessageDto;

public interface PrivateMessageService {

    void sendMessages(String username, JSONArray jsonMessageArray);

    IPage<PrivateMessageDto> fetchInMessages(String username, int pageNo, int pageSize);

    IPage<PrivateMessageDto> fetchOutMessages(String username, int pageNo, int pageSize);

    Integer unreadMessageCount(String username);

    PrivateMessageDto readMessage(String username, Long msgId);
}
