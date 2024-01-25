package org.ww.EntityDO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.base.BaseEntity;

import java.io.Serializable;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseUserDO extends BaseEntity implements Serializable {
    private Long userId;
    private Long userName;

}
