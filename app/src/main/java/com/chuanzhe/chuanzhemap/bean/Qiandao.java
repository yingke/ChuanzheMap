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
    private String goodsname;//名称
    private  Float goodsPrice;//单价
    private Integer goodsquantity; //数量

    public Qiandao() {
    }



    public Integer getCunhuoliang() {
        return cunhuoliang;
    }

    public void setCunhuoliang(Integer cunhuoliang) {
        this.cunhuoliang = cunhuoliang;
    }

    public Integer getBuhuoliang() {
        return buhuoliang;
    }

    public void setBuhuoliang(Integer buhuoliang) {
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

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public Float getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Float goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public Integer getGoodsquantity() {
        return goodsquantity;
    }

    public void setGoodsquantity(Integer goodsquantity) {
        this.goodsquantity = goodsquantity;
    }
}
