package com.chuanzhe.chuanzhemap.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by  yingke on 2018-08-16.
 * yingke.github.io
 */
public class Qiandao extends BmobObject{
    private Integer cunhuoliang;
    private Integer buhuoliang;
    private String dingdanhao;
    private MyUser user;
    private PointItems items;

    public Qiandao() {
    }

    public Qiandao(int cunhuoliang, int buhuoliang, String dingdanhao, MyUser user, PointItems items) {
        this.cunhuoliang = cunhuoliang;
        this.buhuoliang = buhuoliang;
        this.dingdanhao = dingdanhao;
        this.user = user;
        this.items = items;
    }

    public int getCunhuoliang() {
        return cunhuoliang;
    }

    public void setCunhuoliang(int cunhuoliang) {
        this.cunhuoliang = cunhuoliang;
    }

    public int getBuhuoliang() {
        return buhuoliang;
    }

    public void setBuhuoliang(int buhuoliang) {
        this.buhuoliang = buhuoliang;
    }

    public String getDingdanhao() {
        return dingdanhao;
    }

    public void setDingdanhao(String dingdanhao) {
        this.dingdanhao = dingdanhao;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public PointItems getItems() {
        return items;
    }

    public void setItems(PointItems items) {
        this.items = items;
    }
}
