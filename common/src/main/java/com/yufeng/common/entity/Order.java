package com.yufeng.common.entity;

import java.awt.*;
import java.io.Serializable;

/**
 * @Description
 * @Author yangwu
 * CreateTime        202009301811
 */
public class Order implements Serializable {


    private static final long serialVersionUID = 639161715060688065L;

    //订单id
    private String id;

    //用户id
    private String userId;

    //商品id
    private String goodsId;

    private Integer goodsNum;

    //商品数量
    private  Integer number;

    public Order() {
    }

    public Order(String userId, String goodsId, Integer number,Integer goodsNum) {
        this.userId = userId;
        this.goodsId = goodsId;
        this.number = number;
        this.goodsNum = goodsNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }
}
