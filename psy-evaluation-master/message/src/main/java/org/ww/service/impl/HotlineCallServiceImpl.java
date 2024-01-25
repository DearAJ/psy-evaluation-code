package org.ww.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ww.domain.*;
import org.ww.dto.CallInfoDto;
import org.ww.dto.StatisticDto;
import org.ww.dto.TelCdrDto;
import org.ww.mapper.*;
import org.ww.service.HotlineCallService;
import org.ww.service.HotlineContactService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HotlineCallServiceImpl implements HotlineCallService {

    @Resource
    private ManagerMapper managerMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private TelGroupMapper telGroupMapper;

    @Resource
    private TelNoteMapper telNoteMapper;

    @Resource
    private TelCdrInfoMapper telCdrInfoMapper;

    @Autowired
    private HotlineContactService hotlineContactService;

    private String recordPrefix = "/callrecord/";

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String generateAccount(TelGroup telGroup) {
        String username = "H" + telGroup.getAdmin();
        if(userMapper.findByUsername(username) != null)
        {
            throw new RuntimeException("用户已存在");
        }

        telGroup.setUsername(username);

        Role sourceRole = roleMapper.selectOne(new QueryWrapper<Role>()
                .eq("username", telGroup.getAdmin())
                .eq("deleted", 0)
                .orderByDesc("id")
                .last("limit 1"));

        Manager sourceManager = managerMapper.selectOne(new QueryWrapper<Manager>()
                .eq("username", telGroup.getAdmin())
                .eq("deleted", 0)
                .orderByDesc("id")
                .last("limit 1"));
        if(sourceRole == null || sourceManager == null)
        {
            throw new RuntimeException("操作不合法");
        }

        String password = new BCryptPasswordEncoder().encode("123456");
        User user = new User(0L, username, password, 0, new Date(), new Date(), "", 0);
        userMapper.insert(user);

        Role role = new Role(0L, username, "热线", -1, new Date(), new Date(), 0);
        roleMapper.insert(role);

        Manager manager = new Manager(0L, username, "热线账号", "", "", "",
                sourceManager.getProvince(), sourceManager.getCity(), sourceManager.getCounty(), sourceManager.getSchool(), new Date(), new Date(), 0);
        managerMapper.insert(manager);

        telGroup.setType(sourceRole.getLevel());
        telGroup.setProvince(sourceManager.getProvince());
        telGroup.setCity(sourceManager.getCity());
        telGroup.setCounty(sourceManager.getCounty());
        telGroup.setSchool(sourceManager.getSchool());
        telGroup.setCreateTime(new Date());
        telGroup.setDeleted(0);

        telGroupMapper.insert(telGroup);

        return telGroup.getUsername();
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public TelCdrInfo checkToAddCdr(TelCdrInfo telCdrInfo) {
        TelCdrInfo info = telCdrInfoMapper.selectOne(new QueryWrapper<TelCdrInfo>()
                .eq("session_id", telCdrInfo.getSessionId()));
        if(info == null)
        {
            telCdrInfoMapper.insert(telCdrInfo);
            return telCdrInfo;
        }else if(info.getProcessStatus() < telCdrInfo.getProcessStatus())
        {
            telCdrInfo.setId(info.getId());
            telCdrInfoMapper.updateById(telCdrInfo);
        }
        return info;
    }

    @Override
    public CallInfoDto getLastCallInfo(TelCdrInfo telCdrInfo) {
        if(telCdrInfo.getDirection() == 1)
        {
            // TODO: 呼出
            return null;
        }
        CallInfoDto callInfoDto = new CallInfoDto();

        // 上次保存的表单
        TelNote note = telNoteMapper.selectOne(new QueryWrapper<TelNote>()
                .eq("phone", telCdrInfo.getCaller())
                .eq("tel_group_id", telCdrInfo.getTelGroupId())
                .eq("deleted", 0)
                .orderByDesc("create_time")
                .last("limit 1"));
        callInfoDto.setNote(note);

        // 联系人
        TelContact telContact = hotlineContactService.findByPhone(telCdrInfo.getTelGroupId(), telCdrInfo.getCaller());
        callInfoDto.setContact(telContact);

        // 上次成功的通话记录
        TelCdrInfo telCdr = telCdrInfoMapper.selectOne(new QueryWrapper<TelCdrInfo>()
                .eq("direction", 0)
                .eq("caller", telCdrInfo.getCaller())
                .eq("end_status",1)
                .eq("tel_group_id", telCdrInfo.getTelGroupId())
                .ne("session_id", telCdrInfo.getSessionId())
                .orderByDesc("time")
                .last("limit 1"));
        callInfoDto.setCdr(telCdr);

        // 用户总拨打数
        Integer total = telCdrInfoMapper.selectCount(new QueryWrapper<TelCdrInfo>()
                .eq("direction", 0)
                .eq("caller", telCdrInfo.getCaller())
                .eq("tel_group_id", telCdrInfo.getTelGroupId())
                .ne("session_id", telCdrInfo.getSessionId()));
        callInfoDto.setTotalCalled(total);

        // 用户拨打接通数
        Integer succeed = telCdrInfoMapper.selectCount(new QueryWrapper<TelCdrInfo>()
                .eq("direction", 0)
                .eq("caller", telCdrInfo.getCaller())
                .eq("tel_group_id", telCdrInfo.getTelGroupId())
                .eq("end_status",1)
                .ne("session_id", telCdrInfo.getSessionId()));
        callInfoDto.setSucceedCalled(succeed);

        return callInfoDto;


    }

    @Override
    public TelGroup findOldestCdrProceed() {
        TelGroup last = telGroupMapper.selectOne(new QueryWrapper<TelGroup>()
                .eq("deleted", 0)
                .ne("sdk_app_id", "0")
                .orderByAsc("cdr_last_fetch_timestamp")
                .last("limit 1"));
        return last;
    }

    @Override
    public void updateTelGroupCdrTimestamp(Long id, Date now) {
        TelGroup telGroup = new TelGroup();
        telGroup.setId(id);
        telGroup.setCdrLastFetchTimestamp(now);
        telGroupMapper.updateById(telGroup);
    }

    @Override
    public void saveTelNoteBySessionId(TelNote telNote, String sessionId) {
        // 获取对应tel cdr ID
        TelCdrInfo cdr = telCdrInfoMapper.selectOne(new QueryWrapper<TelCdrInfo>()
                .eq("session_id", sessionId));
        if(cdr == null)
        {
            throw new RuntimeException("no session:" + sessionId);
        }

        telNote.setTelGroupId(cdr.getTelGroupId());
        telNote.setTelCdrId(cdr.getId());

        TelNote existed = telNoteMapper.selectOne(new QueryWrapper<TelNote>()
                .eq("tel_cdr_id", cdr.getId())
                .eq("deleted", 0));
        if(existed == null)
        {
            // 新增
            telNote.setCreateTime(new Date());
            telNote.setDeleted(0);
            telNoteMapper.insert(telNote);
        }else {
            // 修改
            telNote.setId(existed.getId());
            telNote.setUpdateTime(new Date());
            telNoteMapper.updateById(telNote);
        }
    }

    @Override
    public IPage<TelCdrDto> listCallIn(Long telGroupId, String caller, Integer isSucceed, Integer pageNum, Integer pageSize) {
        Long total = telCdrInfoMapper.selectCallInCount(telGroupId, caller, isSucceed);

        IPage<TelCdrDto> result = new Page<>(pageNum, pageSize, total);

        List<TelCdrDto> telCdrDtoList = telCdrInfoMapper.selectCallIn(telGroupId, caller, isSucceed, (pageNum - 1) * pageSize, pageSize);

        telCdrDtoList.forEach(cdr -> {
            if(!StringUtils.isBlank(cdr.getRecord()))
            {
                cdr.setRecord(recordPrefix + cdr.getRecord());
            }
        });

        result.setRecords(telCdrDtoList);

        return result;
    }

    private void fillRecordTotalCount(List<TelCdrDto> telCdrDtoList, List<Map<String, Object>> statistics) {
        telCdrDtoList.stream().forEach(dto -> {
            for(Map<String, Object> s : statistics)
            {
                if(dto.getCaller().equals(s.get("caller")))
                {
                    dto.setTotalCount((Long)s.get("c"));
                }
            }
        });
    }

    @Override
    public TelGroup findTelGroupByUsername(String username) {
        return telGroupMapper.selectOne(new QueryWrapper<TelGroup>()
                .eq("username", username)
                .eq("deleted", 0));
    }

    @Override
    public TelGroup findTelGroupByUsernameOrAdmin(String username) {
        return telGroupMapper.selectOne(new QueryWrapper<TelGroup>()
                .eq("deleted", 0)
                .and(w ->
                    {
                        w.eq("admin", username).or().eq("username", username);
                    })

                );
    }

    @Override
    public TelGroup findTelGroupByAdmin(String username) {
        return telGroupMapper.selectOne(new QueryWrapper<TelGroup>()
                .eq("admin", username)
                .eq("deleted", 0));
    }

    @Override
    public TelCdrInfo getTelRecordToDownload() {
        return telCdrInfoMapper.selectOne(new QueryWrapper<TelCdrInfo>()
                .isNotNull("record_url")
                .isNull("record_local_path")
                .orderByDesc("id")
                .last("limit 1")
                );
    }

    @Override
    public void updateTelCdrLocalPath(Long id, String s) {
        TelCdrInfo update = new TelCdrInfo();
        update.setId(id);
        update.setRecordLocalPath(s);
        telCdrInfoMapper.updateById(update);
    }

    @Override
    public void updateTelCdrCallerCount(Long telGroupId) {
        // 找出所有需要更新的cdr信息
        List<Map<String, Object>> newIds = telCdrInfoMapper.selectMaps(new QueryWrapper<TelCdrInfo>()
                .select("id", "caller")
                .eq("tel_group_id", telGroupId)
                .eq("direction", 0)
                .isNotNull("time")
                .isNotNull("end_status")
                .isNull("call_in_count")
                .orderByAsc("time"));
        if(newIds.size() > 0)
        {
            // 找出各个 caller 里 count 现在的最大值
            List<String> callers = newIds.stream().map(n -> (String) n.get("caller")).distinct().collect(Collectors.toList());
            List<Map<String, Object>> maxCallList = telCdrInfoMapper.selectMaps(new QueryWrapper<TelCdrInfo>()
                    .select("caller", "max(call_in_count) as max")
                    .eq("tel_group_id", telGroupId)
                    .eq("direction", 0)
                    .isNotNull("time")
                    .isNotNull("end_status")
                    .isNotNull("call_in_count")
                    .in("caller", callers)
                    .groupBy("caller"));
            // 把List<Map<String, Object>>转成dict，便于计算，key -> caller, value -> max;
            Map<String, Long> dict = new HashMap<>();
            for(Map<String, Object> maxInfo : maxCallList)
            {
                if(maxInfo.get("max") == null)
                {
                    dict.put("" + maxInfo.get("caller"), 0L);
                }else {
                    dict.put("" + maxInfo.get("caller"), (Long)maxInfo.get("max"));
                }
            }

            // 更新callInCount字段
            for(Map<String, Object> newId: newIds)
            {
                String caller = (String) newId.get("caller");
                TelCdrInfo telCdrInfo = new TelCdrInfo();
                telCdrInfo.setId((Long)newId.get("id"));
                if(!dict.containsKey(caller)) dict.put(caller, 0L);
                dict.put(caller, dict.get(caller) + 1);

                telCdrInfo.setCallInCount(dict.get(caller));

                telCdrInfoMapper.updateById(telCdrInfo);

            }
        }
    }

    @Override
    public TelGroup findTelGroupById(Long id) {
        return telGroupMapper.selectById(id);
    }

    @Override
    public List<StatisticDto> cdrStatisticsByContinuousDate(Long telGroupId, Date startDate, Integer size) {
        List<StatisticDto> statisticDtos = telCdrInfoMapper.selectContinuousDateStatistics(telGroupId, startDate, size);

        Map<String, List<StatisticDto>> dateMap = statisticDtos.stream().collect(Collectors.groupingBy(StatisticDto::getLabel));
        List<StatisticDto>  result = dateMap.entrySet().stream().map(e ->
                        new StatisticDto(e.getKey(), null, 0, 0, 0, 0L, 0L, 0L, 0L, e.getValue()))
                .collect(Collectors.toList());
        result.forEach(statisticDto -> {
            statisticDto.setFailedCount(statisticDto.getRoles().stream().mapToInt(StatisticDto::getFailedCount).sum());
            statisticDto.setSuccessCount(statisticDto.getRoles().stream().mapToInt(StatisticDto::getSuccessCount).sum());
            statisticDto.setTotalCount(statisticDto.getRoles().stream().mapToInt(StatisticDto::getTotalCount).sum());
        });

        result.sort(Comparator.comparing(StatisticDto::getLabel).reversed());

        return result;

    };

    @Override
    public List<StatisticDto> noteStatisticsByType(Long telGroupId) {
        return null;
    }

    @Override
    public List<StatisticDto> noteStatisticsByLevel(Long telGroupId) {
        return null;
    }

    @Override
    public IPage<StatisticDto> cdrStatisticsByDate(Long telGroupId, Date selectedDate, Integer pageNum, Integer pageSize) {

        Long total = telCdrInfoMapper.selectDateStatisticsCount(telGroupId, selectedDate);

        IPage<StatisticDto> result = new Page<>(pageNum, pageSize, total);

        List<StatisticDto> records = telCdrInfoMapper.selectDateStatistics(telGroupId, selectedDate, (pageNum - 1) * pageSize, pageSize);

        result.setRecords(records);

        return result;
    }

}
