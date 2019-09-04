package com.fiberhome.fp.pojo;

import com.fiberhome.fp.util.TimeUtil;

import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/2
 */
public class FpOperationTable {

    private int date;

    private String errcode;

    private String errInfo;

    private String pjName;

    private String pjLocation;

    private Long captureTime;
    //#############################

    private List<String> pjNameList;

    private List<String> pjLocationList;

    private String timeTag;

    private Integer count;

    //日期格式化显示字段
    private String dateStr;
    //错误级别
    private  String errLevel;

    //查询结束时间
    private String endTime;
    // 查询开始时间
    private String startTime;

    //是否去重查询  0 不去重  1 去重
    private Integer isDistinct;

    private String logLeave;

    private List<String> logLeaveList;

    private String keyWord;


    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getErrLevel() {
        return errLevel;
    }

    public void setErrLevel(String errLevel) {
        this.errLevel = errLevel;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
        this.dateStr = TimeUtil.long2String(date,"yyyy-MM-dd HH:mm:ss");
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrInfo() {
        return errInfo;
    }

    public void setErrInfo(String errInfo) {
        this.errInfo = errInfo;
    }

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

    public Long getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(Long captureTime) {
        this.captureTime = captureTime;
    }

    public List<String> getPjNameList() {
        return pjNameList;
    }

    public void setPjNameList(List<String> pjNameList) {
        this.pjNameList = pjNameList;
    }

    public List<String> getPjLocationList() {
        return pjLocationList;
    }

    public void setPjLocationList(List<String> pjLocationList) {
        this.pjLocationList = pjLocationList;
    }

    public String getTimeTag() {
        return timeTag;
    }

    public void setTimeTag(String timeTag) {
        this.timeTag = timeTag;
    }

    public Integer getIsDistinct() {
        return isDistinct;
    }

    public void setIsDistinct(Integer isDistinct) {
        this.isDistinct = isDistinct;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getLogLeave() {
        return logLeave;
    }

    public void setLogLeave(String logLeave) {
        this.logLeave = logLeave;
    }

    public List<String> getLogLeaveList() {
        return logLeaveList;
    }

    public void setLogLeaveList(List<String> logLeaveList) {
        this.logLeaveList = logLeaveList;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
}
