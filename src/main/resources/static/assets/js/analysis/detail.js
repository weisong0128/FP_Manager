define([
  'data',
  'tool',
  'public',
  'header',
  'filter'
], function (dataApp, tool, publicApp, headerApp, filterApp) {

  var app = {
    props: {
      index: 1,
      projectName: ""
    },
    moduleApp: {
      statistics: 'analysis/module/statistics',
      tableUseDetail: 'analysis/module/tableUseDetail',
      allSql: 'analysis/module/allSql',
      errorSql: 'analysis/module/errorSql',
      errorDetail: 'analysis/module/errorDetail'
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
      var self = this;
      filterApp.init(function () {
        filterApp.setFilterDom($("#statisticsFilter"), {module: "statistics"});
        filterApp.setFilterDom($("#tableUseDetailFilter"), {module: "tableUseDetail", placeholder: "请输入表名关键字"});
        filterApp.setFilterDom($("#allSqlFilter"), {module: "allSql", placeholder: "请输入SQL详情关键字"});
        filterApp.setFilterDom($("#errorSqlFilter"), {module: "errorSql", placeholder: "请输入SQL详情关键字"});
        filterApp.setFilterDom($("#errorDetailFilter"), {module: "errorDetail", placeholder: "请输入错误详情关键字"});
        filterApp.getPjLocationList("",self.props.projectName);
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

      //所有sql Tab点击
      var chooseSqlItem = '[data-action="chooseSqlItem"]';
      $(document).off('click.chooseSqlItem.FPointer').on('click.chooseSqlItem.FPointer', chooseSqlItem, function () {
        var $closest = $(this).closest(".sql-wrap");
        var index = $(this).index();
        var module = $(this).attr("data-module");
        $closest.find(".tab-level-1").removeClass("active");
        $(this).addClass("active");
        $closest.find(".tab-panel-1").removeClass("active");
        $closest.find(".tab-panel-1").eq(index).addClass("active");
        self.initModule(module);
      });

      //详情Tab点击
      var chooseDetailItem = '[data-action="chooseDetailItem"]';
      $(document).off('click.chooseDetailItem.FPointer').on('click.chooseDetailItem.FPointer', chooseDetailItem, function () {
        var $closest = $(this).closest(".analysis-container");
        var index = $(this).index();
        var module = $(this).attr("data-module");
        $closest.find(".tab-level-0").removeClass("active");
        $(this).addClass("active");
        $closest.find(".tab-panel-0").removeClass("active");
        $closest.find(".tab-panel-0").eq(index).addClass("active");
        if (module !== "allSqlDetail") {
          self.initModule(module);
        } else {
          $(chooseSqlItem).eq(0).click();
        }

      });

      $(chooseDetailItem).eq(0).click();
    }
  };
  return app;
});