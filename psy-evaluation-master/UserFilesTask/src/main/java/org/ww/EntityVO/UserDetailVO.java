package org.ww.EntityVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.domain.User;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserDetailVO extends UserVO implements Serializable {

    private String nation;
    private String idType;


}
