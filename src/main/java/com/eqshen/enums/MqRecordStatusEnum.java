package com.eqshen.enums;

/**
 * @Auther: eqshen
 * @Description
 * @Date: 2020/3/11 17:48
 */
public enum  MqRecordStatusEnum {
    SEND(1),
    SEND_SUCCESS(2),
    CONSUMED(3);
    private int code;

    MqRecordStatusEnum(int code){
        this.code = code;
    }
    public int getCode(){
        return code;
    }
}
