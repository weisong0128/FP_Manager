define([], function () {
  var baseUrl = "";

  var modelApp = {

    /**
     * 业务分析列表
     */
    getAnalysisList: function (data) {
      return $.ajax({
        url: baseUrl + "/project/all",
        // url: "../assets/js/data/analysis/list.json",
        type: "GET",
        data: data
      });
    },

    /**
     * 表使用详情
     */
    getBusinessDetails: function (data) {
      return $.ajax({
        url: baseUrl + "/business/getBusinessDetails",
        // url: "../assets/js/data/analysis/businessDetails.json",
        type: "GET",
        data: data
      });
    },

    /**
     * 表字段使用详情
     */
    getRowResult: function (data) {
      return $.ajax({
        url: baseUrl + "/project/rowresult",
        // url: "../assets/js/data/analysis/rowResult.json",
        type: "GET",
        data: data
      });
    },

    /**
     * 所有sql信息
     */
    getAllSql: function (data) {
      return $.ajax({
        url: baseUrl + "/project/list",
        // url: "../assets/js/data/analysis/allSql.json",
        type: "GET",
        data: data
      });
    },

    /**
     * 错误sql信息
     */
    getErrorSql: function (data) {
      return $.ajax({
        url: baseUrl + "/project/result",
        // url: "../assets/js/data/analysis/errorSql.json",
        type: "GET",
        data: data
      });
    },

    /**
     * 错误信息
     */
    getErrorDetail: function (data) {
      return $.ajax({
        url: baseUrl + "/project/operation",
        // url: "../assets/js/data/analysis/errorDetail.json",
        type: "GET",
        data: data
      });
    },
    /**统计总览*/
    getStatisticsData: function (data) {
      return $.ajax({
        url: baseUrl + "/project/proportion",
        // url:"../assets/js/data/analysis/statics.json",
        type: "GET",
        data: data
      });
    }
    // /**表使用频率Top10*/
    // getTableUseTop10: function (data) {
    //   return $.ajax({
    //     // url: baseUrl + "/project/proportion",
    //     url:"../assets/js/data/analysis/businessDetails.json",
    //     type: "GET",
    //     data: data
    //   })
    // }
  };
  return modelApp;
});