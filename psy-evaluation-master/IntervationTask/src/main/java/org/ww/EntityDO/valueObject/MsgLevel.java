package org.ww.EntityDO.valueObject;

public enum MsgLevel {
    PROVINCE("province"),
    CITY("city"),
    COUNTY("county"),
    SCHOOL("school");
    private String val;
    public String getVal(){ return this.val; }
    MsgLevel(String val){
        this.val = val;
    }
}
