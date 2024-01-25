package org.ww.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @Author 13096
 * @Date 2022/7/1 12:56
 * @Version 1.0
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RoleInfo extends BaseEntity implements Serializable {
    private String role;

    private Integer level;

    private String RangeClass;
}
