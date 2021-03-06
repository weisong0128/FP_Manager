package com.fiberhome.fp.pojo;

/**
 * 业务分析详情实体
 */
public class BusinessDetails {
    private String tableName;
    private String cnt;
    private String pjlocation;
    private String date;
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

    public BusinessDetails(String tableName, String cnt, String pjlocation, String date) {
        this.tableName = tableName;
        this.cnt = cnt;
        this.pjlocation = pjlocation;
        this.date = date;
    }

    public BusinessDetails() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public String getPjlocation() {
        return pjlocation;
    }

    public void setPjlocation(String pjlocation) {
        this.pjlocation = pjlocation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
