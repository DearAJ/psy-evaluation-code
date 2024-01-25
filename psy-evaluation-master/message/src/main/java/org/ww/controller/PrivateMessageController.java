package org.ww.controller;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.ww.dto.PrivateMessageDto;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.PrivateMessageService;

@Slf4j
@RestController
@RequestMapping("/privateMessage")
public class PrivateMessageController extends BaseController {

    private PrivateMessageService privateMessageService;

    public PrivateMessageController(PrivateMessageService privateMessageService)
    {
        this.privateMessageService = privateMessageService;
    }



    @PostMapping("/send")
    public Wrapper<Object> send(@RequestBody JSONArray jsonArray) {

//        List<PrivateMessage> messages = new ArrayList<>();
        String username = currentUser();
        if(jsonArray != null)
        {
            privateMessageService.sendMessages(username, jsonArray);
        }

        return WrapMapper.ok();
    }


    @GetMapping("/fetchInMessages")
    public Wrapper<Object> fetchInMessages(Integer pageNum, Integer pageSize) {
        String username = currentUser();
        pageNum = (pageNum == null ? 1 : pageNum);
        pageSize = (pageSize == null ? Integer.MAX_VALUE : pageSize);
        IPage<PrivateMessageDto> messages = privateMessageService.fetchInMessages(username, pageNum, pageSize);
        if(messages != null)
        {
            return WrapMapper.ok(messages);
        }
        return WrapMapper.error();
    }

    @GetMapping("/fetchOutMessages")
    public Wrapper<Object> fetchOutMessages(Integer pageNum, Integer pageSize) {
        String username = currentUser();
        pageNum = (pageNum == null ? 1 : pageNum);
        pageSize = (pageSize == null ? Integer.MAX_VALUE : pageSize);
        IPage<PrivateMessageDto> result = privateMessageService.fetchOutMessages(username, pageNum, pageSize);
        if(result != null)
        {
            return WrapMapper.ok(result);
        }
        return WrapMapper.error();
    }

    @GetMapping("/unreadCount")
    public Wrapper<Object> unreadCount() {
        String username = currentUser();

        Integer count = privateMessageService.unreadMessageCount(username);
        if(count != null)
        {
            return WrapMapper.ok(count);
        }
        return WrapMapper.error();
    }

    @RequestMapping("/read")
    public Wrapper<Object> read(Long msgId) {
        String username = currentUser();

        return WrapMapper.ok(privateMessageService.readMessage(username, msgId));
    }

}
