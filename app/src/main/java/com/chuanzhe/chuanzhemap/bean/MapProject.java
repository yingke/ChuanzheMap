package com.chuanzhe.chuanzhemap.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by  yingke on 2018-06-10.
 * yingke.github.io
 */
public class MapProject extends BmobObject {
    private String Projectname;
    private String Projectmiaoshu;
    private MyUser myUser;


    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }

    public MapProject(String projectname, String projectmiaoshu) {
        Projectname = projectname;
        Projectmiaoshu = projectmiaoshu;
    }

    public MapProject() {
    }

    public MapProject(String projectname, String projectmiaoshu, MyUser myUser) {
        Projectname = projectname;
        Projectmiaoshu = projectmiaoshu;
        this.myUser = myUser;
    }

    public String getProjectname() {
        return Projectname;
    }

    public void setProjectname(String projectname) {
        Projectname = projectname;
    }

    public String getProjectmiaoshu() {
        return Projectmiaoshu;
    }

    public void setProjectmiaoshu(String projectmiaoshu) {
        Projectmiaoshu = projectmiaoshu;
    }
}
