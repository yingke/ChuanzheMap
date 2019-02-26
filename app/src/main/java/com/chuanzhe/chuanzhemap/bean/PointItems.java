package com.chuanzhe.chuanzhemap.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by  yingke on 2018-08-15.
 * yingke.github.io
 */
public class PointItems extends BmobObject {
    private String unm ;
    private  String shopname;
    private String kehu ;
    private String kehuphone ;
    private String dizhi;
    private Double latitude;
    private Double longitude;
    private MapProject mapProject;
    private MyUser amyuser;
    private String imgurl;
    private  String qiandaotime;
   private Integer cunhuoliang;
   private Integer buhuoliang;
   private String zhouqi;
   private Integer isDelete;
   private Integer isfavorite;
    private Integer buhuozhouqi;

    private Integer positive;
    private Integer normal;
    private Integer lazy;

    public Integer getPositive() {
        return positive;
    }

    public void setPositive(Integer positive) {
        this.positive = positive;
    }

    public Integer getNormal() {
        return normal;
    }

    public void setNormal(Integer normal) {
        this.normal = normal;
    }

    public Integer getLazy() {
        return lazy;
    }

    public void setLazy(Integer lazy) {
        this.lazy = lazy;
    }




    public Integer getBuhuozhouqi() {
        return buhuozhouqi;
    }

    public void setBuhuozhouqi(Integer buhuozhouqi) {
        this.buhuozhouqi = buhuozhouqi;
    }

    public String getZhouqi() {
        return zhouqi;
    }

    public void setZhouqi(String zhouqi) {
        this.zhouqi = zhouqi;
    }

    private String beizhu;

    public PointItems() {

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

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsfavorite() {
        return isfavorite;
    }

    public void setIsfavorite(Integer isfavorite) {
        this.isfavorite = isfavorite;
    }

    public String getUnm() {
        return unm;
    }

    public void setUnm(String unm) {
        this.unm = unm;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getKehu() {
        return kehu;
    }

    public void setKehu(String kehu) {
        this.kehu = kehu;
    }

    public String getKehuphone() {
        return kehuphone;
    }

    public void setKehuphone(String kehuphone) {
        this.kehuphone = kehuphone;
    }

    public String getDizhi() {
        return dizhi;
    }

    public void setDizhi(String dizhi) {
        this.dizhi = dizhi;
    }


    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public MapProject getMapProject() {
        return mapProject;
    }

    public void setMapProject(MapProject mapProject) {
        this.mapProject = mapProject;
    }

    public MyUser getAmyuser() {
        return amyuser;
    }

    public void setAmyuser(MyUser amyuser) {
        this.amyuser = amyuser;
    }


    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getQiandaotime() {
        return qiandaotime;
    }

    public void setQiandaotime(String qiandaotime) {
        this.qiandaotime = qiandaotime;
    }

    @Override
    public String toString() {
        return "PointItems{" +
                "unm='" + unm + '\'' +
                ", shopname='" + shopname + '\'' +
                ", kehu='" + kehu + '\'' +
                ", kehuphone='" + kehuphone + '\'' +
                ", dizhi='" + dizhi + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", mapProject=" + mapProject +
                ", amyuser=" + amyuser +
                ", imgurl='" + imgurl + '\'' +
                ", qiandaotime='" + qiandaotime + '\'' +
                ", cunhuoliang=" + cunhuoliang +
                ", buhuoliang=" + buhuoliang +
                ", zhouqi='" + zhouqi + '\'' +
                ", isDelete=" + isDelete +
                ", isfavorite=" + isfavorite +
                ", beizhu='" + beizhu + '\'' +
                '}';
    }
}
