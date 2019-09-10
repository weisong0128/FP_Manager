package com.fiberhome.fp.pojo;


import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * 日志分析实体
 */
public class LogAnalze {
    //主键
    private String uuid;
    //项目名称
    private String projectName;
    //安装地区
    private String address;
    //任务开始时间
    private Date startTime;
    //检测状态(0:完成，1：异常)
    private String parsingState;
    //分析进度
    private String progress;
    //操作结果（0：成功，1失败）
    private String result;
    //创建时间
    private Long createTime;
    //修改时间
    private Date updateTime;

    private String starTime;

    private String endTime;

    private String timeTag;

    private String sort;

    private List<String> projectNameList;

    private List<String> addressList;

    public LogAnalze(String uuid, String projectName, String address, Date startTime, String parsingState, String progress, String result, Long createTime, Date updateTime, String starTime, String endTime, String timeTag, String sort, List<String> projectNameList, List<String> addressList) {
        this.uuid = uuid;
        this.projectName = projectName;
        this.address = address;
        this.startTime = startTime;
        this.parsingState = parsingState;
        this.progress = progress;
        this.result = result;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.starTime = starTime;
        this.endTime = endTime;
        this.timeTag = timeTag;
        this.sort = sort;
        this.projectNameList = projectNameList;
        this.addressList = addressList;
    }

    public LogAnalze(String projectName, String address, Long createTime) {
        this.projectName = projectName;
        this.address = address;
        this.createTime = createTime;
    }

    public LogAnalze() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getStarTime() {
        return starTime;
    }

    public void setStarTime(String starTime) {
        this.starTime = starTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getStartTime() {
        return startTime;
    }

    public List<String> getProjectNameList() {
        return projectNameList;
    }

    public void setProjectNameList(List<String> projectNameList) {
        this.projectNameList = projectNameList;
    }

    public List<String> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<String> addressList) {
        this.addressList = addressList;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getParsingState() {
        return parsingState;
    }

    public void setParsingState(String parsingState) {
        this.parsingState = parsingState;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public static String getAllColumn() {
        return
                " uuid ," +
                        " project_name ," +
                        " address ," +
                        " start_time ," +
                        " parsing_state ," +
                        " progress ," +
                        " result ," +
                        " create_time ," +
                        " update_time ";
    }

    //获取文件存储路径
    public String getDiskPath(String uploadLogPath) {
        return uploadLogPath + File.separator + getAddress() + File.separator + getProjectName() + File.separator + getCreateTime();
    }

    public String getTimeTag() {
        return timeTag;
    }

    public void setTimeTag(String timeTag) {
        this.timeTag = timeTag;
    }
}
