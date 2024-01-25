package org.ww.EntityDO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import org.ww.base.BaseEntity;

@Data
@SuperBuilder
@AllArgsConstructor
public abstract class IdBaseEntity extends BaseEntity {
    public abstract Long getId();
}
