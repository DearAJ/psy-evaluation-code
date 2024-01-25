package org.ww.EntityVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.base.BaseEntity;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserVO extends BaseLevelVO implements Serializable {
    private String identity;//学生还是老师
    private String name;
    private String sex;
    private String phoneNumber;
    private Long studentId;
    private Long teacherId;
    private String IDCard;
    private String period;
    private String grade;
    private String classes;
    private Long userId;
    private Integer isGraduate;
    private Integer schoolId;

    private Integer archived;


}
