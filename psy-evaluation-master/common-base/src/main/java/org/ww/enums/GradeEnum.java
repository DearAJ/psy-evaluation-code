package org.ww.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @Author 13096
 * @Date 2022/6/20 16:22
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum GradeEnum {
    GRADE_ONE(1, "一年级"),

    GRADE_TWO(2, "二年级"),

    GRADE_THREE(3, "三年级"),

    GRADE_FOUR(4, "四年级"),

    GRADE_FIVE(5, "五年级"),

    GRADE_SIX(6, "六年级")

    ;

    private final Integer key;

    private final String value;

    public static String getValue(Integer key) {
        for (GradeEnum grade : GradeEnum.values()) {
            if (Objects.equals(key, grade.getKey())) {
                return grade.getValue();
            }
        }
        return null;
    }

    public static Integer getKey(String value) {
        for (GradeEnum grade : GradeEnum.values()) {
            if (Objects.equals(value, grade.getValue())) {
                return grade.getKey();
            }
        }
        return null;
    }
}
