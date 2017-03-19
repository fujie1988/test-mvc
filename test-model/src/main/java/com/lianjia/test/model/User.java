package com.lianjia.test.model;

/**
 * Created by helloworld on 2017/3/16.
 */
public class User {

    private int id;

    private String name;

    private String password;

    public int getUserId(int id){
        return this.id;
    }

    public String getUserName(String name){
        return this.name;
    }

    public String getPassword(String password){
        return this.password;
    }

    public void setUserId(int id){
        this.id = id;
    }

    public void setUserName(String name){
        this.name = name;
    }

    public void setPassword(String password){
        this.password = password;
    }
}
