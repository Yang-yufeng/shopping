package com.yufeng.common.result;


/**
 * @Description
 * @Author yangwu
 * CreateTime        202009262239
 */
public enum ResultEnum {


    /**通用错误**/
    SUCCESS(10000,"操作成功"),

    FAIL(-1,"服务器内部错误"),

    /**订单**/
    SALE_OUT(20001,"商品已抢光!");


    public int code;

    public String  msg;

    ResultEnum(int code,String msg) {
        this.code = code;
        this.msg = msg;
    }
}
