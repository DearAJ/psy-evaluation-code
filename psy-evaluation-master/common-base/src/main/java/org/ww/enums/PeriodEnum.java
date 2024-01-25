package org.ww.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @Author 13096
 * @Date 2022/6/20 16:19
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum PeriodEnum {
    PERIOD_PRIMARY(0, "小学"),

    PERIOD_JUNIOR_HIGH(1, "初中"),

    PERIOD_HIGH(2, "高中")

    ;

    private final Integer key;

    private final String value;

    public static String getValue(Integer key) {
        for (PeriodEnum period : PeriodEnum.values()) {
            if (Objects.equals(key, period.getKey())) {
                return period.getValue();
            }
        }
        return null;
    }

    public static Integer getKey(String value) {
        for (PeriodEnum period : PeriodEnum.values()) {
            if (Objects.equals(value, period.getValue())) {
                return period.getKey();
            }
        }
        return null;
    }
}
