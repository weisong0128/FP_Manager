define([
  'model',
  'data',
  'tool',
  'public',
  'header',
  'analysis/analysisModel',
  'viewTpl/analysis/analysisListTpl'
], function (model, dataApp, tool, publicApp, headerApp, analysisModel, analysisListTpl) {

  var app = {
    props: {
      index: 1
    },

    init: function () {
      headerApp.init(this.props);
      this.initList();
      this.events();
    },

    initList: function (condition) {
      var self = this;
      analysisModel.getAnalysisList(condition).then(function (res) {
        if (tool.checkStatusCode(res.code)) {
          $("#analysisList").html(analysisListTpl(res.data));
          self.initAnalysisPager(res.page);
        } else {
          publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
        }
      });
    },

    initAnalysisPager: function (page) {
      $("#analysisPager").pagination({
        totalrows: page.totalRows,
        pagesize: page.pageSize,
        pageno: page.pageNo,
        pageinfo: '共 #{totalrows} 条记录，每页显示 #{pagesize } 条，当前显示第 #{ pageno } 页(#{from } - #{ to}条)，共 #{totalpages} 页',
        resizer: {values: [15, 30, 45, 60], label: " #{size} 条/页"},
        callback: function () {
          // self.location.href = createUrl(pageno);
          return false;
        }
      });
    },

    events: function () {
      var self = this;
      var analysisDetail = '[data-action="analysisDetail"]';
      $(document).off('click.analysisDetail.FPointer').on('click.analysisDetail.FPointer', analysisDetail, function () {
        var projectName = $(this).attr("data-pjName");
        window.location.href = '../analysis/detail.html?projectName=' + encodeURI(encodeURI(projectName));
      });

      var pjNameSearch = '[data-action="pjNameSearch"]';
      $(document).off('click.pjNameSearch.FPointer').on('click.pjNameSearch.FPointer', pjNameSearch, function () {
        var keyWord = $("#keyWord").val();
        var condition = {};
        condition.pjName = keyWord;
        self.initList(condition);
      });

      var clearSearch = '[data-action="clear-search"]';
      $(document).off('mousedown.clearSearch.FPointer').on('mousedown.clearSearch.FPointer', clearSearch, function () {
        $("#keyWord").val("");
        self.initList();
      });

      var tinkerDetail = '[data-action="tinker-detail"]';
      $(document).off('mousedown.tinkerDetail.FPointer').on('mousedown.tinkerDetail.FPointer', tinkerDetail, function () {
        window.location.href='../analysis/tinkerHelp.html';
      });
    }
  };
  return app;
});