package org.ww.EntityDO.valueObject;


import com.baomidou.mybatisplus.core.enums.IEnum;

public enum StuState implements IEnum<StuState> {

    INSCHOOL("在校"),
    INHOME("请假在家"),
    SUSPEND("休学");
    private String val;
    public String getVal(){ return val; }
    public StuState getValue(){ return this;}
    private StuState(String val){ this.val = val; }

}
