package org.ww.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.base.RoleInfo;

import java.io.Serializable;

/**
 * @Author 13096
 * @Date 2022/2/27 13:04
 * @Version 1.0
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FindStudentDto extends RoleInfo implements Serializable {
    private Integer schoolId;

    private String entranceYear;

    private String period;

    private String grade;

    private String classes;

    private String sex;

    private Integer graduated;

    private String name;

    private String studentId;

    private String idNumber;


    private Integer archived;
}
