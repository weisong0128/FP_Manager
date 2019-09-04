package com.fiberhome.fp.pojo;

/**
 * 业务分析详情实体
 */
public class BusinessDetails {
    //表名
    private String tableName;
    //表分区
    private String tablePartition;
    //项目安装地
    private String pjlocation;

    private String partition;
    //使用次数
    private String count;
    //创建时间
    private String createTime;

    public BusinessDetails(String tableName, String tablePartition, String pjlocation, String partition, String count, String createTime) {
        this.tableName = tableName;
        this.tablePartition = tablePartition;
        this.pjlocation = pjlocation;
        this.partition = partition;
        this.count = count;
        this.createTime = createTime;
    }

    public BusinessDetails() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTablePartition() {
        return tablePartition;
    }

    public void setTablePartition(String tablePartition) {
        this.tablePartition = tablePartition;
    }

    public String getPjlocation() {
        return pjlocation;
    }

    public void setPjlocation(String pjlocation) {
        this.pjlocation = pjlocation;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
