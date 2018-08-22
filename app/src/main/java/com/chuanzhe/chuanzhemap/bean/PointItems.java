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
    private int isvip;
    private int isfree;
    private String imgurl;

   private int cunhuoliang;
   private int buhuoliang;

    public PointItems() {

    }

    public PointItems(String tableName, String unm, String shopname, String kehu, String kehuphone, String dizhi, Double latitude, Double longitude, MapProject mapProject, MyUser amyuser, int isvip, int isfree, String imgurl, int cunhuoliang, int buhuoliang) {
        super(tableName);
        this.unm = unm;
        this.shopname = shopname;
        this.kehu = kehu;
        this.kehuphone = kehuphone;
        this.dizhi = dizhi;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mapProject = mapProject;
        this.amyuser = amyuser;
        this.isvip = isvip;
        this.isfree = isfree;
        this.imgurl = imgurl;
        this.cunhuoliang = cunhuoliang;
        this.buhuoliang = buhuoliang;
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

    public int getIsvip() {
        return isvip;
    }

    public void setIsvip(int isvip) {
        this.isvip = isvip;
    }

    public int getIsfree() {
        return isfree;
    }

    public void setIsfree(int isfree) {
        this.isfree = isfree;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
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
}
