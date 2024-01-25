package org.ww.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.domain.TelCdrInfo;
import org.ww.domain.TelContact;
import org.ww.domain.TelNote;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CallInfoDto {

    private Long telCdrId;
    private String sessionId;
    private String caller;
    private String callee;
    private Integer direction;
    private Integer blocked;

    private int totalCalled;
    private int succeedCalled;

    private TelContact contact;
    private TelNote note;
    private TelCdrInfo cdr;


}
