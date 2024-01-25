package org.ww.manageSchedule;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ww.domain.School;
import org.ww.domain.SchoolClasses;
import org.ww.domain.Students;
import org.ww.enums.GradeEnum;
import org.ww.mapper.SchoolClassesMapper;
import org.ww.mapper.SchoolMapper;
import org.ww.mapper.StudentsMapper;
import org.ww.mapper.UserMapper;
import org.ww.provider.UserProvider;
import org.ww.utils.SchoolYearUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author 13096
 * @Date 2022/6/20 15:45
 * @Version 1.0
 */
@Component
@Slf4j
public class UpgradeStudent {

    @Resource
    private SchoolMapper schoolMapper;

    @Resource
    private StudentsMapper studentsMapper;


    @Resource
    private UserMapper userMapper;

    @Resource
    private SchoolClassesMapper schoolClassesMapper;

    @Value("${job.UpgradeStudent.enabled:true}")
    private Boolean jobEnabled; // 集群状态下，需确保有且仅有一个节点启用此任务

//    @Scheduled(cron = "0 0 10 21 6 *")          // 测试
    @Scheduled(cron = "0 0 2 1 8 *")          // 每年8月30日2点触发
    public void upgrade() {
        if(!jobEnabled)
        {
            return;
        }

        String currentSchoolYear = "" + SchoolYearUtils.currentSchoolYear();

        // 查询每一个学校
        QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
        schoolQueryWrapper.select("id", "period");
        List<School> schoolList = schoolMapper.selectList(schoolQueryWrapper);

        // 遍历每个学校
        for (School school : schoolList) {
            // 获取该学校所有的班级
            List<SchoolClasses> classes = schoolClassesMapper.selectList(new QueryWrapper<SchoolClasses>()
                    .eq("archived", 0)
            );

            for(SchoolClasses sc : classes)
            {
                if(!canUpgrade(sc.getPeriod()))
                {
                    continue;
                }

                if(currentSchoolYear.equals(sc.getSchoolYear()))
                {
                    // 还在本学年，不升级
                    continue;
                }

                sc.setArchived(1);
                schoolClassesMapper.updateById(sc);

                String nextGrade = nextGrade(sc.getPeriod(), sc.getGrade());
                if(nextGrade != null)
                {
                    SchoolClasses newClasses = new SchoolClasses();
                    BeanUtils.copyProperties(sc, newClasses);
                    newClasses.setArchived(0);
                    newClasses.setSchoolYear(currentSchoolYear);
                    newClasses.setGrade(nextGrade);
                    schoolClassesMapper.insert(newClasses);
                }

                log.info("{} class graduated", sc.getId());
            }

            // 查询学校中未存档的学生
            QueryWrapper<Students> studentsQueryWrapper = new QueryWrapper<>();
            studentsQueryWrapper
                    .eq("school_id", school.getId())
                    .eq("archived", 0);
            List<Students> studentsList = studentsMapper.selectList(studentsQueryWrapper);

            List<String> usernameToDeleted = new ArrayList<>();

            // 为每个学生升级，并修改班级人数
            for (Students student : studentsList) {
                if(!canUpgrade(student.getPeriod()))
                {
                    // 如果既不是小学，也不是初中，高中，不要升班
                    continue;
                }

                if(currentSchoolYear.equals(student.getSchoolYear()))
                {
                    // 还在本学年，不升级
                    continue;
                }


                // 计算学生的新年级  学段保持不变，对于毕业的学生，将graduated置为1
                String nextGrade = nextGrade(student.getPeriod(), student.getGrade());

                // 将毕业学生的graduated置为1
                if (nextGrade == null) {
                    studentsMapper.updateStudentGraduated(student.getId());


                    usernameToDeleted.add(student.getUsername());

                    log.info("{} student graduated", student.getId());
                    continue;
                }

                student.setArchived(1);
                studentsMapper.updateById(student);

                Students newStudent = new Students();
                BeanUtils.copyProperties(student, newStudent);
                newStudent.setArchived(0);
                newStudent.setSchoolYear(currentSchoolYear);
                newStudent.setGrade(nextGrade);

                studentsMapper.insert(newStudent);

                log.info("{} student not graduated, upgrade", student.getId());

            }

            if(usernameToDeleted.size() > 0)
            {
//                userMapper.deleteUsersByUsernames(usernameToDeleted);
                schoolMapper.reduceBatchStudentNumbers(school.getId().intValue(), usernameToDeleted.size());
            }
        }
        // 删除class_numbers等于0的班级
        schoolClassesMapper.deleteClasses();
    }

    private boolean canUpgrade(String period)
    {
        if(!period.equals("小学") && !period.equals("初中") && !period.equals("高中") )
        {
            // 如果既不是小学，也不是初中，高中，不要升班
            return false;
        }

        return true;
    }

    private String nextGrade(String period, String oldGrade)
    {
        // 计算学生的新年级  学段保持不变，对于毕业的学生，将graduated置为1
        if (period.equals("小学") && oldGrade.equals("六年级")) {  // 小学升初中
            return null;
        } else if (period.equals("初中") && oldGrade.equals("三年级")){   // 初中升高中
            return null;
        } else if (period.equals("高中") && oldGrade.equals("三年级")){   // 高中毕业
            return null;
        } else {
            Integer gKey = GradeEnum.getKey(StringUtils.trim(oldGrade));
            if(gKey == null)
            {
                log.warn("unknown grade: {}.", oldGrade);
                return null;
            }
            return GradeEnum.getValue( gKey + 1);
        }
    }
}
