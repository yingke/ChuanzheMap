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
    private Goods goods;//商品

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

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Integer getGoodsquantity() {
        return goodsquantity;
    }

    public void setGoodsquantity(Integer goodsquantity) {
        this.goodsquantity = goodsquantity;
    }

    private Integer goodsquantity; //数量

    public Qiandao() {
    }


}
