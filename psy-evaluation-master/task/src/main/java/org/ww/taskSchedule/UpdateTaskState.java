package org.ww.taskSchedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ww.mapper.TaskMapper;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;

/**
 * @Author 13096
 * @Date 2022/7/1 19:01
 * @Version 1.0
 */
@Component
@Slf4j
public class UpdateTaskState {

    @Resource
    private TaskMapper taskMapper;

    private final SimpleDateFormat simpleDateFormat_YMDHMS = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private final SimpleDateFormat simpleDateFormat_YMD = new SimpleDateFormat("yyyy-MM-dd");

//    @Scheduled(cron = "0 1 0 * * ?")          // 每天0点1分触发
//    public void updateState() {
//        try {
//            QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
//            taskQueryWrapper.select("id", "start_date", "end_date", "state").and(taskQueryWrapper1 -> taskQueryWrapper1.eq("state", 0).or().eq("state", 1));
//            List<Task> taskList = taskMapper.selectList(taskQueryWrapper);
//            for (Task task : taskList) {
//                Date nowDate = new Date();
//                Date startDate = simpleDateFormat_YMDHMS.parse(simpleDateFormat_YMD.format(task.getStartDate()) + " 00:00:00");
//                Date endDate = simpleDateFormat_YMDHMS.parse(simpleDateFormat_YMD.format(task.getEndDate()) + " 23:59:59");
//                if (task.getState() == 0 && nowDate.after(startDate) && nowDate.before(endDate)) {
//                    taskMapper.updateTaskState(task.getId(), 1);        // 改为进行中
//                    log.info("task id:{} change state to running", task.getId());
//                }
//                if (nowDate.after(endDate)) {
//                    taskMapper.updateTaskState(task.getId(), 2);        // 改为已结束
//                    log.info("task id:{} change state to end", task.getId());
//                }
//            }
//        }  catch (ParseException e) {
//            e.printStackTrace();
//            log.error(e.getMessage());
//        }
//    }
}
