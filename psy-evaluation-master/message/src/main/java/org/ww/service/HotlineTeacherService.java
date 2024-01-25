package org.ww.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.ww.domain.TelTeacher;

public interface HotlineTeacherService {
    IPage<TelTeacher> list(Long teacherId, Long telGroupId, Integer pageNum, Integer pageSize);
    IPage<TelTeacher> listDetails(Long teacherId, Long telGroupId, Integer pageNum, Integer pageSize);

    TelTeacher add(TelTeacher teacher);

    int remove(Long telGroupId, Long id);

    TelTeacher update(Long telGroupId, TelTeacher teacher);
}
