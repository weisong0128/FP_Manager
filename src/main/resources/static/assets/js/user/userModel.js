define([], function () {
    'use strict';
    var jsonContentType = 'application/json;charset=utf-8';
    var baseURL = "/user";
    var userApp = {
        /**
         * 获取全部用户列表数据
         */
        getUserList: function (condition) {
            return $.ajax({
                url: baseURL + "/getUserInfoByParames",
                type: "GET",
                data: condition
            });
        },
        /**
         * 用户列表接口参数
         */
        userListParams: {
            "pageNo": "1",
            "pageSize": "10",
            "keyWord": "",
            "userRole": "",
            "userState": ""
        },
        /**
         * 获取全部角色
         */
        getAllRole: function () {
            return $.ajax({
                url: "../assets/js/data/user/roleList.json",
                type: "GET"
            });
        },
        /**
         *  新增用户信息
         * **/
        addUser: function (data) {
            return $.ajax({
                url: baseURL + "/createUser",
                type: "POST",
                data: JSON.stringify(data),
                dataType: 'json',
                contentType: jsonContentType
            });
        },
        /**
         * 编辑用户 
         */
        updateUser: function (data) {
            return $.ajax({
                url: baseURL + "/updateUserInfo",
                type: "POST",
                data: JSON.stringify(data),
                dataType: 'json',
                contentType: jsonContentType
            });
        },
        /**
         * 改变账号状态
         */
        changeAccountStatus: function (data) {
            return $.ajax({
                url: baseURL + "/updateState?uuids=" + data.uuids + "&state=" + data.state,
                type: "POST"
            });
        }
    };
    return userApp;
});