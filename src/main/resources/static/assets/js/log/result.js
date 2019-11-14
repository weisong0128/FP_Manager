define([
  'data',
  'tool',
  'public',
  'header',
  'filter',
  'log/logModel'
], function (dataApp, tool, publicApp, headerApp, filterApp) {

  var app = {
    props: {
      index: 2,
      projectName: ""
    },
    moduleApp: {
      errorLog: 'log/module/errorLog',
      errorSqlLog: 'log/module/errorSqlLog'
    },

    init: function () {
      headerApp.init(this.props);
      this.initProjectName();
      this.initFilter();
      this.events();
    },
    initProjectName: function () {
      var urlParams = tool.getUrlParams();
      this.props.projectName = decodeURI(decodeURI(urlParams.query["projectName"]));
      $("#projectName").html(this.props.projectName);
    },
    initFilter: function () {
      filterApp.init(function () {
        $("#errorLogFilter").html(filterApp.getFilterDom({module: "errorLog", placeholder: "请输入错误详情关键字"}));
        $("#errorSqlLogFilter").html(filterApp.getFilterDom({module: "errorSqlLog", placeholder: "请输入SQL详情关键字"}));
        filterApp.initSelect();
      });
    },

    initModule: function (module) {
      var self = this;
      require([this.moduleApp[module]], function (moduleApp) {
        moduleApp.init(self.props);
      });
    },

    events: function () {
      var self = this;
      var chooseLogItem = '[data-action="chooseLogItem"]';
      $(document).off('click.chooseLogItem.FPointer').on('click.chooseLogItem.FPointer', chooseLogItem, function () {
        var $closest = $(this).closest(".analysis-container");
        var index = $(this).index();
        var module = $(this).attr("data-module");
        $closest.find(".tab-level-0").removeClass("active");
        $(this).addClass("active");
        $closest.find(".tab-panel-0").removeClass("active");
        $closest.find(".tab-panel-0").eq(index).addClass("active");
        self.initModule(module);
      });

      $(chooseLogItem).eq(0).click();

    }
  };
  return app;
});