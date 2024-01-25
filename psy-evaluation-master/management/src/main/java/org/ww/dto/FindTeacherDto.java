package org.ww.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.base.BaseEntity;

import java.io.Serializable;

/**
 * @Author 13096
 * @Date 2022/6/1 21:07
 * @Version 1.0
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FindTeacherDto extends BaseEntity implements Serializable {
    private String schoolId;

    private String name;

    private String sex;

    private String idNumber;

    private String phone;
}
