package org.ww.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ww.domain.*;
import org.ww.dto.PrivateMessageDto;
import org.ww.mapper.*;
import org.ww.service.PrivateMessageService;
import org.ww.vo.RoleVo;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PrivateMessageServiceImpl implements PrivateMessageService {
    private static  final String TITLE_NAME_SEPARATOR = " - ";
    private static final String[] EMPTY_INFOS= new String[]{"", ""};

    @Resource
    private PrivateMessageMapper privateMessageMapper;
    @Resource
    private PrivateMessageDeliverMapper privateMessageDeliverMapper;
    @Resource
    private UserMessageProfileMapper userMessageProfileMapper;

    @Resource
    private SchoolMapper schoolMapper;

    @Resource
    private ManagerMapper managerMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserMapper userMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessages(String username, JSONArray jsonArray) {

        Set<String> toUsers = new HashSet<>();

        String myTitle = "";
        String myName = "";
        List<RoleVo> roles = roleMapper.getRoleByUserId(username);
        if(roles != null && roles.size() > 0)
        {
            myTitle = roles.get(0).getRole();
            int level = roles.get(0).getLevel();
            if(level > 1) // 去manager表里找
            {
                Manager manager = managerMapper.selectOne(new QueryWrapper<Manager>()
                        .eq("username", username)
                        .eq("deleted", 0)
                        .orderByDesc("id")
                        .last("limit 1")
                );
                if(manager != null)
                {
                    myName = manager.getName();
                }
            }else {
                // TODO: 暂时只能区管理员发信
            }

        }

        for (int i = 0; i < jsonArray.size(); i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            PrivateMessage in = jsonObject.toJavaObject(PrivateMessage.class);
            if(in == null)
            {
                continue;
            }
            if(1 == in.getType()) // taskId=1;任务催办
            {
                //
                // refId 学校ID
                // refId2 任务ID
                String taskId = jsonObject.getString("taskId");
                in.setRefId2(taskId);
            }

            in.setSenderTitle(myTitle + TITLE_NAME_SEPARATOR + myName);

            toUsers.addAll(sendMessage(username, in));

        }

        for(String toUser: toUsers)
        {
            obtainNewMessage(toUser);
        }

    }

    private Set<String> sendMessage(String username, PrivateMessage in)
    {
        Set<String> usernames = new HashSet<>();
        // TODO: 需要增加发信权限和逻辑检查，比如自己发自己，下级发上级
        String schoolId = in.getRefId();
        // 1: 任务催办
        // 2: 预警催办
        // 3: 预警指导建议
        if(2 == in.getType() || 3 == in.getType()) //
        {
            // refId 是fileId
            // refId2 是心理老师username;
            Map<String, Object> file = privateMessageMapper.selectPsyintervationInfoByFileId(in.getRefId());
            schoolId = file.get("school_id").toString();
            Long teacherId = (Long)file.get("user_id");
            User u = userMapper.selectById(teacherId);
            if(u != null) {
                in.setRefId2(u.getUsername());
                usernames.add(u.getUsername());
            }
        }

        // 根据schoolId找到对应的负责人
        School school = schoolMapper.selectById(schoolId);
        if(school != null)
        {
            in.setTarget(school.getSchoolLogin());
            in.setTargetTitle(school.getName());

            usernames.add(in.getTarget());
        }

        in.setSenderUsername(username);
        in.setDeleted(0);
        privateMessageMapper.insert(in);

        return usernames;
    }

    @Override
    public IPage<PrivateMessageDto> fetchInMessages(String username, int pageNo, int pageSize) {
        //
//        obtainNewMessage(username);

        pageNo = Math.max(pageNo, 1);
        pageSize = Math.max(pageSize, 1);
        List<PrivateMessageDto> privateMessageDtos = privateMessageMapper.selectInMessages(username, (pageNo - 1) * pageSize, pageSize);
        for(PrivateMessageDto dto : privateMessageDtos)
        {
            String[] userInfos = getSenderDetailInfo(dto.getSenderTitle());
            dto.setSenderTitle(userInfos[0]);
            dto.setSenderName(userInfos[1]);
        }

        Integer total = privateMessageDeliverMapper.selectCount(
                new QueryWrapper<PrivateMessageDeliver>()
                        .eq("target_username", username)
                        .eq("deleted", 0)
        );

        IPage<PrivateMessageDto> result = new Page<>(pageNo, pageSize, total);
        result.setRecords(privateMessageDtos);
        return result;
    }

    @Override
    public IPage<PrivateMessageDto> fetchOutMessages(String username, int pageNo, int pageSize) {
        pageNo = Math.max(pageNo, 1);
        pageSize = Math.max(pageSize, 1);

        IPage<PrivateMessage> pages = privateMessageMapper.selectPage(new Page<>(pageNo, pageSize),
                new QueryWrapper<PrivateMessage>()
                        .eq("sender_username", username)
                        .eq("deleted", 0)
                        .orderByDesc("create_time")
        );

        IPage<PrivateMessageDto> result = new Page<>(pageNo, pageSize, pages.getTotal());

        if(pages.getRecords() != null)
        {
            List<PrivateMessageDto> dtos = pages.getRecords().stream()
                    .map(msg -> new PrivateMessageDto(msg.getId(), msg.getType(), msg.getContent(),
                            msg.getRefId(), msg.getCreateTime(), null,
                            msg.getSenderUsername(), getSenderDetailInfo(msg.getSenderTitle())[0], getSenderDetailInfo(msg.getSenderTitle())[1],
                            msg.getTarget(), msg.getTargetTitle()))
                    .collect(Collectors.toList());
            result.setRecords(dtos);
        }

        return result;
    }

    @Override
    public Integer unreadMessageCount(String username) {
//        obtainNewMessage(username);

        return privateMessageDeliverMapper.selectCount(
                new QueryWrapper<PrivateMessageDeliver>()
                        .eq("status", 0)
                        .eq("deleted", 0)
                        .eq("target_username", username)
        );
    }

    @Override
    public PrivateMessageDto readMessage(String username, Long msgId) {
        PrivateMessageDeliver entity = new PrivateMessageDeliver();
        entity.setStatus(1);
        privateMessageDeliverMapper.update(entity,
                new UpdateWrapper<PrivateMessageDeliver>()
                        .eq("target_username", username)
                        .eq("private_message_id", msgId));
        PrivateMessage privateMessage = privateMessageMapper.selectById(msgId);
        if(privateMessage != null)
        {
            String[] userInfos = getSenderDetailInfo(privateMessage.getSenderTitle());
            return new PrivateMessageDto(
                    privateMessage.getId(),
                    privateMessage.getType(),
                    privateMessage.getContent(),
                    privateMessage.getRefId(),
                    privateMessage.getCreateTime(),
                    1,
                    privateMessage.getSenderUsername(),
                    userInfos[0],
                    userInfos[1],
                    privateMessage.getTarget(),
                    privateMessage.getTargetTitle()
                    );
        }

        return null;
    }

    /**
     * 把合并的用户title分开
     * @param all
     * @return
     */
    private String[] getSenderDetailInfo(String all)
    {
        if(StringUtils.isBlank(all))
        {
            return EMPTY_INFOS;
        }
        String[] ret = all.split(TITLE_NAME_SEPARATOR);
        if(ret.length == 1)
        {
            return new String[]{ret[0], ""};
        }

        return ret;
    }

    public void obtainNewMessage(String username)
    {
        if(StringUtils.isBlank(username))
        {
            return;
        }
        // 获取处理过的最大ID
        UserMessageProfile current = userMessageProfileMapper.selectOne(
                new QueryWrapper<UserMessageProfile>()
                        .eq("username", username)
                        .orderByDesc("id")
                        .last("limit 1")
        );
        Long currentId = 0l;
        if(current != null)
        {
            currentId = current.getCurrentPrivateMessageId();
        }

        // 查询未连接的消息
        List<Long> newMsgIds = privateMessageMapper.selectUndeliveredMessageIds(username, currentId);
        if(newMsgIds.size() > 0)
        {
            // 插入新数据;
            List<PrivateMessageDeliver> privateMessageDelivers =
                    newMsgIds.stream()
                            .map(msgId -> new PrivateMessageDeliver(null, msgId, username, 0, 0))
                            .collect(Collectors.toList());
            privateMessageDeliverMapper.insertBatch(privateMessageDelivers);

        }

        // 更新profile表
        PrivateMessage maxMessage =  privateMessageMapper.selectOne(
                new QueryWrapper<PrivateMessage>()
                        .orderByDesc("id").last("limit 1")
        );

        Long maxId = 0l;
        if(maxMessage != null)
        {
            maxId = maxMessage.getId();
        }
        if(current == null)
        {
            current = new UserMessageProfile();
            current.setUsername(username);
            current.setCurrentPrivateMessageId(maxId);
            userMessageProfileMapper.insert(current);
        }else if(!maxId.equals(current.getCurrentPrivateMessageId())){
            current.setCurrentPrivateMessageId(maxId);
            userMessageProfileMapper.updateById(current);
        }
    }
}
