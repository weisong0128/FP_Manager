package com.fiberhome.fp.pojo;

import io.swagger.annotations.ApiModelProperty;

/***
 * 授权管理实体
 */
public class AuthManage {
    //主键
    @ApiModelProperty(value = "主键（新增不填，自动生成，修改必填）",name = "uuid",dataType ="String")
    private String uuid;
    //项目名称
    @ApiModelProperty(value = "项目名称",name = "projectName",dataType ="String")
    private String projectName;
    //环境负责人
    @ApiModelProperty(value = "环境负责人",name = "envirHead",dataType ="String")
    private String envirHead;
    //电话
    @ApiModelProperty(value = "电话",name = "phone",dataType ="String")
    private String phone;
    //安装省份
    @ApiModelProperty(value = "安装省份",name = "provinces",dataType ="String")
    private String provinces;
    //安装地市
    @ApiModelProperty(value = "安装地市",name = "cities",dataType ="String")
    private String cities;
    //安装地址
    @ApiModelProperty(value = "安装地址",name = "address",dataType ="String")
    private String address;
    @ApiModelProperty(value = "mac",name = "mac",dataType ="String")
    private String mac;
    //主节点ip
    @ApiModelProperty(value = "主节点ip",name = "masterIp",dataType ="String")
    private String masterIp;
    //证书下载日期
    @ApiModelProperty(value = "证书下载日期",name = "downloadTime",dataType ="String")
    private String downloadTime;
    //备注（线上生产环境，研发测试环境）
    @ApiModelProperty(value = "备注（线上生产环境，研发测试环境）",name = "envirNote",dataType ="String")
    private String envirNote;
    //对应的sn文件
    @ApiModelProperty(value = "对应的sn文件",name = "snFile",dataType ="String")
    private String snFile;
    //授权反馈情况（0：反馈，1：未反馈）
    @ApiModelProperty(value = "授权反馈情况",name = "feedback",dataType ="String")
    private String feedback;
    //备注
    @ApiModelProperty(value = "备注",name = "note",dataType ="String")
    private String note;
    //创建时间
    @ApiModelProperty(value = "创建时间（不填，自动生成）",name = "createTime",dataType ="String")
    private String createTime;
    //修改时间
    @ApiModelProperty(value = "修改时间（不填，自动生成）",name = "updateTime",dataType ="String"  )
    private String updateTime;
    //是否删除（0：可用,1：删除）
    @ApiModelProperty(value = "是否删除（不填，默认可用）",name = "isAvailable",dataType ="String" )
    private String isAvailable;
    //查询关键字
    @ApiModelProperty(value = "查询关键字",name = "keyWord",dataType ="String" )
    private  String keyWord;

    //排序关键字
    @ApiModelProperty(value = "排序关键字",name = "sortField",dataType ="String" )
    private  String sortField;

    //查询开始时间
    @ApiModelProperty(value = "查询开始时间",name = "keyWord",dataType ="String" )
    private  String startTime;

    //查询结束时间
    @ApiModelProperty(value = "排序关键字",name = "keyWord",dataType ="String" )
    private  String endTime;


    public AuthManage(String uuid, String projectName, String envirHead, String phone, String provinces, String cities, String address, String mac, String masterIp, String downloadTime, String envirNote, String snFile, String feedback, String note, String createTime, String updateTime, String isAvailable) {
        this.uuid = uuid;
        this.projectName = projectName;
        this.envirHead = envirHead;
        this.phone = phone;
        this.provinces = provinces;
        this.cities = cities;
        this.address = address;
        this.mac = mac;
        this.masterIp = masterIp;
        this.downloadTime = downloadTime;
        this.envirNote = envirNote;
        this.snFile = snFile;
        this.feedback = feedback;
        this.note = note;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.isAvailable = isAvailable;
    }

    public AuthManage(String projectName, String cities, String envirNote, String feedback, String keyWord, String sortField, String startTime, String endTime) {
        this.projectName = projectName;
        this.cities = cities;
        this.envirNote = envirNote;
        this.feedback = feedback;
        this.keyWord = keyWord;
        this.sortField = sortField;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public AuthManage() {
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

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getEnvirHead() {
        return envirHead;
    }

    public void setEnvirHead(String envirHead) {
        this.envirHead = envirHead;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvinces() {
        return provinces;
    }

    public void setProvinces(String provinces) {
        this.provinces = provinces;
    }

    public String getCities() {
        return cities;
    }

    public void setCities(String cities) {
        this.cities = cities;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMasterIp() {
        return masterIp;
    }

    public void setMasterIp(String masterIp) {
        this.masterIp = masterIp;
    }

    public String getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(String downloadTime) {
        this.downloadTime = downloadTime;
    }

    public String getEnvirNote() {
        return envirNote;
    }

    public void setEnvirNote(String envirNote) {
        this.envirNote = envirNote;
    }

    public String getSnFile() {
        return snFile;
    }

    public void setSnFile(String snFile) {
        this.snFile = snFile;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }
}
