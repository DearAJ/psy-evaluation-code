package org.ww.EntityDO.valueObject;


import com.baomidou.mybatisplus.core.enums.IEnum;

public enum IntervationRecordType implements IEnum<IntervationRecordType> {
    RECORD("干预记录"),
    CRISIS("危机确认");

    private String val;
    public String getVal(){ return this.val; }
    public IntervationRecordType getValue(){ return this;}
    IntervationRecordType(String val){
        this.val = val;
    }
}
