package org.ww.providerImp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.ww.domain.TaskUser;
import org.ww.exception.BusinessException;
import org.ww.mapper.TaskUserMapper;
import org.ww.provider.TaskProvider;

import javax.annotation.Resource;

/**
 * @Author 13096
 * @Date 2022/6/14 9:55
 * @Version 1.0
 */
@Slf4j
@DubboService
public class TaskProviderImpl implements TaskProvider {

    @Resource
    private TaskUserMapper taskUserMapper;

    @Override
    public Integer hasTask(Long userId) {
        try {
            QueryWrapper<TaskUser> taskUserQueryWrapper = new QueryWrapper<>();
            taskUserQueryWrapper.eq("user_id", userId);
            Integer count = taskUserMapper.selectCount(taskUserQueryWrapper);
            if (count == 0) {
                return 0;
            }
            return 1;
        } catch (BusinessException e) {
            return 2;
        } catch (Exception e) {
            log.info(e.getMessage());
            return 2;
        }
    }
}
