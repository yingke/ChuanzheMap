package com.chuanzhe.chuanzhemap.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by  yingke on 2019-08-20.
 * yingke.github.io
 */
public class Goods extends BmobObject {
    private String GoodsName;
    private Double GoodsPrice;
    private MyUser myUser;



    public Goods() {
    }



    public String getGoodsName() {
        return GoodsName;
    }

    public void setGoodsName(String goodsName) {
        GoodsName = goodsName;
    }

    public Double getGoodsPrice() {
        return GoodsPrice;
    }

    public void setGoodsPrice(Double goodsPrice) {
        GoodsPrice = goodsPrice;
    }

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }
}
