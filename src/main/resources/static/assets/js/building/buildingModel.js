define([], function () {
  'use strict';
  var jsonContentType = 'application/json;charset=utf-8';
  var baseURL = '/project';
  var buildingApp = {
    /**
     * 检验元数据
     */
    checkMetadata: function (condition) {
      return $.ajax({
        url: baseURL + "/check",
        type: "GET",
        data: condition
      });
    },
    /**生成建表脚本*/
    generateScript: function (condition) {
      return $.ajax({
        url: baseURL + "/create",
        type: "GET",
        data: condition
      });
    },
    /**元数据入库*/
    metaDataStorage: function (condition) {
      return $.ajax({
        url: baseURL + "/metadate",
        type: "GET",
        data: condition
      });
    },
    /**提交异常编辑的接口参数*/
    editErrorParams: {
      "col": 0,
      "content": "",
      "fileName": "",
      "oldType": "",
      "path": "",
      "row": 0,
      "sheetNum": 0,
      "updateType": ""
    },
    /**编辑后再次下发检测*/
    editErrorForm:function (data) {
      return $.ajax({
        url: baseURL + "/updateexcel",
        type: "POST",
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: jsonContentType
      });
    }
  };
  return buildingApp;
});