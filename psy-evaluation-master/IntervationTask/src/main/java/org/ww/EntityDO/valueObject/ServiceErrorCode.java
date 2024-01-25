package org.ww.EntityDO.valueObject;

public enum ServiceErrorCode {
    NULLPARAM("参数为空",-2),
    PARAMAERROR("参数非法",-4),
    ENTITYERROR("实体类型错误",-3),
    UNSAVELAST("上一条未保存",-5),
    FINDNULL("查询结果为空",-6),
    ISSUBMIT("已提交，不能修改",-7),
    WRONGACTION("操作错误",-8),
    INSERTFAILE("添加失败",-9),
    UPDATAFAILE("更新失败",-10),
    ERROEPAGEINFO("页码信息错误",-11),
    WRONGADDORDER("创建顺序错误",-12),
    WRONGAUTHORITY("不能修改其他用户未提交的结果",-13),
    WRONGUSERINFO("用户信息查询失败",-14);
    private String val;
    private Integer code;
    public String getVal(){
        return this.val;
    }
    public Integer getCode(){
        return this.code;
    }
    static public String getErrorMsgById(Integer code,String functionName){
        ServiceErrorCode[] errors = values();
        for(ServiceErrorCode e : errors){
            if(e.getCode() == code) return e.getVal();
        }
        return "未知错误:"+functionName;
    }

    private ServiceErrorCode(String val, Integer code){
        this.val = val;
        this.code = code;
    }

}
