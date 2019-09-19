package com.fiberhome.fp.pojo;

import com.fiberhome.fp.util.TimeUtil;

import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */
public class ErrorResult {

    private int date;

    private String tag;

    private String alterTag;

    private String sqlResult;

    private String pjName;

    private String pjLocation;


    private Long captureTime;



    //##########################

    private String startTime;

    private String endTime;

    private List<String> tagList;

    private List<String> pjNameList;

    private List<String> pjLocationList;

    private String timeTag = "all";

    private Integer count;


    //排序值
    private String sort;
    //排序字段名
    private String sortName;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    //日期格式化显示字段
    private String dateStr;
    //关键字查询
    private String keyWord;
    //是否去重查询  0 不去重  1 去重
    private Integer isDistinct;

    private  String errorSqlType;

    public String getErrorSqlType() {
        return errorSqlType;
    }

    public void setErrorSqlType(String errorSqlType) {
        this.errorSqlType = errorSqlType;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
        this.dateStr = TimeUtil.long2String(date,"yyyy-MM-dd HH:mm:ss");
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAlterTag() {
        return alterTag;
    }

    public void setAlterTag(String alterTag) {
        this.alterTag = alterTag;
    }

    public String getSqlResult() {
        return sqlResult;
    }

    public void setSqlResult(String sqlResult) {
        this.sqlResult = sqlResult;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<String> getPjNameList() {
        return pjNameList;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public Integer getIsDistinct() {
        return isDistinct;
    }

    public void setIsDistinct(Integer isDistinct) {
        this.isDistinct = isDistinct;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public void setPjNameList(List<String> pjNameList) {
        this.pjNameList = pjNameList;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
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

    public static String getAllColumn() {
        return " date,tag,alter_tag,sql_result,pjname,pjlocation ";
    }

    public Long getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(Long captureTime) {
        this.captureTime = captureTime;
    }
}
