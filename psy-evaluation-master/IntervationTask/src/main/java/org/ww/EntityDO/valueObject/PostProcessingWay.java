package org.ww.EntityDO.valueObject;

import com.baomidou.mybatisplus.core.enums.IEnum;
import org.apache.poi.POIDocument;

public enum PostProcessingWay implements IEnum<PostProcessingWay> {
    HOSPITAL("住院"),
    PILL("服药"),
    REMEDY("治疗"),
    SCHOOL("在校咨询"),
    OTHER("其它");
    private String val;
    public String getVal(){ return this.val; }
    public PostProcessingWay getValue(){ return this; }
    PostProcessingWay(String val){
        this.val = val;
    }
}
