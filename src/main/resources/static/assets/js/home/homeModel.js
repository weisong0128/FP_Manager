define([], function () {
    'use strict';
    var baseURL = "/index";
    var homeModal = {

        /**
         * 获取china.json文件数据
         */
        getChina: function () {
            return $.ajax({
                url: "./../assets/js/data/map/china.json",
                type: "GET"
            });
        },
        /**
         * 获取geoCoord.json文件数据
         */
        getGeoCoord: function () {
            return $.ajax({
                url: "./../assets/js/data/map/geoCoord.json",
                type: "GET"
            });
        },
        /**
         * 获取全国FP数据库点数据（首页）
         */
        getFPDatabseDot: function () {
            return $.ajax({
                url: baseURL + "/getAllauthManage",
                type: "GET"
            });
        },
        /**
         * 获取各项目线上环境授权TOP10的数据（首页）
         */
        getProjectTop10: function () {
            return $.ajax({
                url: baseURL + '/authManageTOP10',
                type: "GET"
            });
        },
        /**
         * 获取FP数据库地区分布TOP5的数据（首页）
         */
        getDBAreaDistributionTop5: function () {
            return $.ajax({
                url: baseURL + "/authManageTOP5",
                type: "GET"
            });
        },
        /**
         * 获取开放授权趋势的数据（首页）
         */
        getOpenAuthorizeData: function () {
            return $.ajax({
                url: baseURL + "/openAuthManage",
                type: "GET"
            });
        },
        /**
         * 获取首页头部卡片内的数据
         */
        getHeaderCardData: function () {
            return $.ajax({
                url: baseURL + "/authManageCount",
                type: "GET"
            });
        }
    };

    return homeModal;
});