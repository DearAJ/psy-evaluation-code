package org.ww.EntityVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.EntityDO.IdBaseEntity;
import org.ww.base.BaseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseLevelVO extends IdBaseEntity {
    private Long id;
    private String msgLevel;
    private String province;
    private String city;
    private String county;
    private String schoolName;

    @Override
    public Long getId() {
        return id;
    }
}
