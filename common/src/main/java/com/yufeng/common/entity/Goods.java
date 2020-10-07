package com.yufeng.common.entity;

import java.io.Serializable;

/**
 * @Description
 * @Author yangwu
 * CreateTime        202009301815
 */
public class Goods implements Serializable {


    private static final long serialVersionUID = -5445149713707105433L;

    //商品id
    private String id;

    //商品名
    private String name;

    //库存
    private  int store;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStore() {
        return store;
    }

    public void setStore(int store) {
        this.store = store;
    }

    public Goods(String name, int store) {
        this.name = name;
        this.store = store;
    }

    public Goods() {
    }
}
