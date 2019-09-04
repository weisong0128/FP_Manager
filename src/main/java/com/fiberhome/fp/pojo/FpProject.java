package com.fiberhome.fp.pojo;

import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */
public class FpProject {

    private String pjName;

    private String pjLocation;

    //#########################

    private List<String> pjLocationList;

    private String path;

//    public FpProject(String pjName,String pjLocation){
//        this.pjName = pjName;
//        this.pjLocation = pjLocation;
//    }
//
//    public FpProject(String pjName){
//        this.pjName = pjName;
//    }


    public String getPjName() {
        return pjName;
    }

    public void setPjName(String pjName) {
        this.pjName = pjName;
    }

    public String getPjLocation() {
        return pjLocation;
    }

    public void setPjLocation(String pjLocation) {
        this.pjLocation = pjLocation;
    }

    public List<String> getPjLocationList() {
        return pjLocationList;
    }

    public void setPjLocationList(List<String> pjLocationList) {
        this.pjLocationList = pjLocationList;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
