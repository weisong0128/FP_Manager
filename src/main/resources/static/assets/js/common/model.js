define([], function () {
  var baseUrl = "";
  /**
   * 公共接口
   * **/
  var model = {

    /**
     * 所有搜索参数
     */
    getAllFilter: function (data) {
      return $.ajax({
        url: baseUrl + "/project/all",
        // url: "../assets/js/data/analysis/list.json",
        headers: {"allow": "POST"},
        type: "GET",
        data: data
      });
    },
    /**
     * 授权模块，获取搜索条件的项目名称和地市
     */
    getPjNameAndCitiesByAuthorization:function(data) {
      return $.ajax({
        url: baseUrl + "/manage/getAllPjNameAndCities",
        type: "GET",
        data: data
      });
    },
    /**
     * 登录
     */
    login: function (data) {
      return $.ajax({
        url: baseUrl + "/fplogin/fplogin",
        type: "POST",
        data: data
      });
    },
    /**
     * 退出登录
     */
    quit:function (data) {
      return $.ajax({
        url: baseUrl + "/fplogin/quit",
        type: "GET",
        data: data
      });
    },

    /**
     * 小叮当帮助页获取数据
     */
    getTinkerHelp:function (data) {
      return $.ajax({
        // url: "../assets/js/data/analysis/tinkerData.json",
        url: baseUrl + "/project/solution",
        type: "GET",
        data: data
      });
    }
  };
  return model;
});