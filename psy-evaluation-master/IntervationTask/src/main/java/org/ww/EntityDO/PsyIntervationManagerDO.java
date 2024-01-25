package org.ww.EntityDO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Component
@TableName("manager")
public class PsyIntervationManagerDO extends IdBaseEntity implements Serializable {

    private Long id;
    private String name;
    private String username;

}
