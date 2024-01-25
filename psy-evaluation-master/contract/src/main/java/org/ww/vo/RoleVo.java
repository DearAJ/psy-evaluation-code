package org.ww.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RoleVo implements Serializable {
    private Long id;
    private String username;
    private String role;
    private Integer level;
    private Date createTime;
    private Date updateTime;
    private Integer deleted;
}
