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
    private Integer mincycle;
    private Integer maxcycle;



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

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }

    public Integer getMincycle() {
        return mincycle;
    }

    public void setMincycle(Integer mincycle) {
        this.mincycle = mincycle;
    }

    public Integer getMaxcycle() {
        return maxcycle;
    }

    public void setMaxcycle(Integer maxcycle) {
        this.maxcycle = maxcycle;
    }
}
