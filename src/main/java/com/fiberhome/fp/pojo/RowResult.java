package com.fiberhome.fp.pojo;

/**
 * @author fengxiaochun
 * @date 2019/7/2
 */
public class RowResult {

    private int num;

    private String rowName;

    private int rowCount;

    private String percent;

    private String pjName;

    private String pjLocation;


    //###################################

    private Integer count;

    private int tag;

    private String partition;

    private String searchTime;


    private  String tableName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getRowName() {
        return rowName;
    }

    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(String searchTime) {
        this.searchTime = searchTime;
    }
}
