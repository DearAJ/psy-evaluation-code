package org.ww.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.domain.TelContact;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistDto extends TelContact {
    private String remark;
}
