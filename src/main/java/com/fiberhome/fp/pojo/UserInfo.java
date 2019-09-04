package com.fiberhome.fp.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户信息
 */
@ApiModel(value = "用户信息类")
public class UserInfo {
    //唯一标识
    @ApiModelProperty(value ="唯一标识（不填，自动生成(修改则必传)）" ,name = "uuid",dataType = "String")
    private String uuid;
    //用户账户（手机号）
    @ApiModelProperty(value ="用户账户（手机号）" ,name = "userId",dataType = "String")
    private String userId;
    //用户名
    @ApiModelProperty(value ="用户名" ,name = "userName",dataType = "String")
    private String userName;
    //用户密码
    @ApiModelProperty(value ="用户密码" ,name = "passWord",dataType = "String")
    private String userPassword;
    //用户角色
    @ApiModelProperty(value ="用户角色" ,name = "role",dataType = "String")
    private  String userRole;
    //用户状态(0 启用   1 禁用   2  删除 ，默认：启用)
    @ApiModelProperty(value ="用户状态(0 启用   1 禁用   2  删除 )" ,name = "state",dataType = "String")
    private String userState;
    //创建时间
    @ApiModelProperty(value ="用户创建时间（不填，自动生成）" ,name = "createTime",dataType = "String")
    private String createTime;
    //修改时间
    @ApiModelProperty(value ="用户修改时间（不填，自动生成）" ,name = "updateTime",dataType = "String")
    private String updateTime;

    public UserInfo(String uuid, String userId, String userName, String userPassword, String userRole, String userState, String createTime, String updateTime) {
        this.uuid = uuid;
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRole = userRole;
        this.userState = userState;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public UserInfo() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
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
}
