package org.ww.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.base.BaseEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author 13096
 * @Date 2022/6/23 15:51
 * @Version 1.0
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResetDto extends BaseEntity implements Serializable {
    List<Map<String, Object>> records;

    Integer isAll;

    Integer taskId;

    Integer schoolId;

    Integer hasFilter;

    String period;

    String grade;

    String classes;

    String name;

    String studentId;

    String warning;

    String valid;

    Integer total;
}
