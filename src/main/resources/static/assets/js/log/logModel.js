define([], function () {
  var jsonContentType = 'application/json;charset=utf-8';
  var baseUrl = "";

  var modelApp = {

    // /**
    //  * 业务分析列表
    //  */
    // getLogList: function () {
    //   return $.ajax({
    //     url: "../assets/js/data/log/list.json",
    //     type: "GET"
    //   })
    // },
    /**
     * 错误信息
     */
    getErrorLog: function (data) {
      return $.ajax({
        url: baseUrl + "/project/operation",
        // url: "../assets/js/data/log/errorLog.json",
        type: "GET",
        data: data
      });
    },
    /**
     * 不合格sql信息
     */
    getErrorSql: function (data) {
      return $.ajax({
        url: baseUrl + "/log/getSqlErroListByParam",
        // url: "../assets/js/data/log/errorSql.json",
        type: "GET",
        data: data
      });
    },
    /**
     * 获取日志列表
     */
    getLogList: function (data) {
      return $.ajax({
        url: baseUrl + "/log/getLogListByParam",
        // url: "../assets/js/data/log/allLog.json",
        type: "GET",
        data: data
      });

    },
    /**
     * 开始检测
     */
    startAnalyse: function (data) {
      return $.ajax({
        url: baseUrl + "/log/startAnalyse",
        type: "GET",
        data: data
      });
    },
    /**
     * 批量删除
     */
    deleteLogs: function (data) {
      return $.ajax({
        url: baseUrl + "/log/batchDeleteLogAnaylse",
        type: "POST",
        data: JSON.stringify(data),
        contentType: jsonContentType
      });
    },
    /**
     * 获取检测滚动条进度
     * @param data
     */
    getProgressValue: function (data) {
      return $.ajax({
        url: baseUrl + "/log/getAnalyseProcess",
        type: "POST",
        data: JSON.stringify(data),
        contentType: jsonContentType
      });
    },
    //日志重新下发
    againIssiue: function (data) {
      return $.ajax({
        url: baseUrl + "/log/restartAnalyse",
        type: "GET",
        data: data
      });
    }
  };
  return modelApp;
});