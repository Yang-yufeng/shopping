package com.yufeng.common.entity;

/**
 * @Description
 * @Author yangwu
 * CreateTime        202009301849
 */
public class User {

    //id
    private String id;


    //名字
    private String name;


    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public User() {
    }

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


}
