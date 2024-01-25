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
public class UserVo implements Serializable {

    private Integer id;
    private String username;
    private String password;
    private Integer enabled;
    private Date createTime;
    private Date updateTime;
    private String avatar;
    private Integer deleted;
}