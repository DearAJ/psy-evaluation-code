package org.ww.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ww.domain.TelTeacher;
import org.ww.mapper.TelCommonCodeMapper;
import org.ww.mapper.TelTeacherMapper;
import org.ww.service.HotlineTeacherService;

import javax.annotation.Resource;

@Slf4j
@Service
public class HotlineTeacherServiceImpl implements HotlineTeacherService {

    @Resource
    private TelTeacherMapper telTeacherMapper;

//    @Resource
//    private TelTeacherCertificateMapper telTeacherCertificateMapper;

    @Resource
    private TelCommonCodeMapper telCommonCodeMapper;

    @Override
    public IPage<TelTeacher> list(Long teacherId, Long telGroupId, Integer pageNum, Integer pageSize) {

        IPage<TelTeacher> page = new Page<>(pageNum, pageSize);
        IPage<TelTeacher> teacherIPage = telTeacherMapper.selectPage(page, new QueryWrapper<TelTeacher>()
                .eq("tel_group_id", telGroupId)
                .eq("deleted", 0)
                .eq(teacherId != null, "id", teacherId)
                .orderByDesc("id"));
        return teacherIPage;
    }

    @Override
    public IPage<TelTeacher> listDetails(Long teacherId, Long telGroupId, Integer pageNum, Integer pageSize) {
        IPage<TelTeacher> teacherIPage = list(teacherId, telGroupId, pageNum, pageSize);
//        for(TelTeacher teacher : teacherIPage.getRecords())
//        {
//            teacher.setCertificates(telTeacherMapper.selectCertificatesByTeacher(teacher.getId()));
//        }

        return teacherIPage;
    }

    @Override
    public TelTeacher add(TelTeacher teacher) {
        telTeacherMapper.insert(teacher);

//        teacher.setCertificates(updateTeacherCertificate(teacher));

        return teacher;
    }

    // 处理证书<->老师多对多关系
//    private List<TelCommonCode> updateTeacherCertificate(TelTeacher teacher)
//    {
//        // 删除原有关系
//        telTeacherCertificateMapper.delete(new QueryWrapper<TelTeacherCertificate>()
//                .eq("tel_teacher_id", teacher.getId()));
//
//        // 创建新的关系
//        if(teacher.getCertificates() != null)
//        {
//            List<TelCommonCode> result = new ArrayList<>();
//            int idx = teacher.getCertificates().size() + 1;
//            List<Long> ids = new ArrayList<>(); // 避免传入的id重复
//            for(TelCommonCode c: teacher.getCertificates())
//            {
//                if(c.getId() != null)
//                {
//                    if(ids.contains(c.getId())) continue;
//
//                    ids.add(c.getId());
//
//                    TelCommonCode cert = telCommonCodeMapper.selectById(c.getId());
//                    if(cert == null) continue;
//
//                    result.add(cert);
//
//                    TelTeacherCertificate relation = new TelTeacherCertificate();
//                    relation.setTelTeacherId(teacher.getId());
//                    relation.setPriority(idx--);
//                    relation.setCreateTime(new Date());
//                    relation.setCertificateId(c.getId());
//                    telTeacherCertificateMapper.insert(relation);
//                }
//            }
//
//            return result;
//        }
//
//        return null;
//    }

    @Override
    public int remove(Long telGroupId, Long id) {
        TelTeacher teacher = telTeacherMapper.selectById(id);
        if(teacher == null || !teacher.getTelGroupId().equals(telGroupId))
        {
            return 0;
        }

        return telTeacherMapper.deleteById(id);
    }

    @Override
    public TelTeacher update(Long telGroupId, TelTeacher teacher) {
        TelTeacher existed = telTeacherMapper.selectById(teacher.getId());
        if(existed == null || !existed.getTelGroupId().equals(telGroupId))
        {
            return null;
        }
        telTeacherMapper.updateById(teacher);
//        teacher.setCertificates(updateTeacherCertificate(teacher));

        return teacher;
    }
}
