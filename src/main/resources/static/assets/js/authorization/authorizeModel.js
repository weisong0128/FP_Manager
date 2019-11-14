define([], function () {
    'use strict';
    var jsonContentType = 'application/json;charset=utf-8';
    var baseURL = '/manage';
    var authorizeApp = {
        /**
         * 获取全部权限管理列表数据
         */
        getAuthorizeList: function (condition) {
            return $.ajax({
                url: baseURL + "/getAllAuthManage",
                type: "GET",
                data: condition
            });
        },
        /**
         * 授权管理列表接口参数
         */
        authorizeListParams: {
            "pageNo": "1",
            "pageSize": "10",
            "sortField": "desc",
            "keyWord": "",
            "projectName": "",
            "cities": "",
            "envirNote": "",
            "feedback": "",
            "startTime" : "",
            "endTime":""
        },
        /**
         * 获取全部项目名称
         */
        getAllItem: function () {
            return $.ajax({
                url: baseURL + "/getAllPjName",
                type: "GET"
            });
        },
        /**
         * 获取项目安装地区
         */
        getInstallArea: function () {
            return $.ajax({
                url: baseURL + "/getAllCities",
                type: "GET"
            });
        },
        /**
         *  删除权限列表数据
         * **/
        delAuthorizes: function (itemIds) {
            return $.ajax({
                url: baseURL + "/deleteAuthManage?uuids="+itemIds,
                type: "GET"
            });
        },
        /**
         *  添加权限列表信息
         * **/
        saveAuthorize: function (data) {
            return $.ajax({
                url: baseURL + "/createAuthManage",
                type: "POST",
                data: JSON.stringify(data),
                dataType: 'json',
                contentType: jsonContentType
            });
        },
        editAuthorize: function (data) {
            return $.ajax({
                url: baseURL + "/updateAuthManage",
                type: "POST",
                data: JSON.stringify(data),
                dataType: 'json',
                contentType: jsonContentType
            });
        },
        getCity: function () {
            return $.ajax({
                url: "./../assets/js/data/map/city.json",
                type: "GET"
            });
        }
    };
    return authorizeApp;
});