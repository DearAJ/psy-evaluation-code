package org.ww.EntityDO.valueObject;


import com.baomidou.mybatisplus.core.enums.IEnum;

public enum CrisisLevel implements IEnum<CrisisLevel> {
    ALEAVEL("一级预警（一般心理危机）"),
    BLEAVEL("二级预警（严重心理危机）"),
    CLEAVEL("三级预警（重大心理危机）"),
    DLEAVEL("无危机"),
    ELEAVEL("未分类");
    private String val;
    public String getVal(){ return this.val; }
    public CrisisLevel getValue(){ return this;}
    CrisisLevel(String val){
        this.val = val;
    }
}
