package org.ww.EntityDO.valueObject;


import com.baomidou.mybatisplus.core.enums.IEnum;

public enum IntervationWay implements IEnum<IntervationWay> {
    FACE("面询"),
    GROUP("团辅"),
    CALLBACK("回访"),
    OTHER("其它");

    private String val;
    public String getVal(){ return this.val; }
    public IntervationWay getValue(){ return this;}
    IntervationWay(String val){
        this.val = val;
    }
}
