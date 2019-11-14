define([
  'model',
  'data',
  'tool',
  'public',
  'viewTpl/filter/index',
  'viewTpl/filter/condition'
], function (model, dataApp, tool, publicApp, filterTpl) {
  var filterApp = {

    getFilterModal: function (callback) {
      var self = this;
      this.events();
      if (!dataApp.filterObj.projectName) {
        model.getAllFilter().then(function (res) {
          if (tool.checkStatusCode(res.code)) {
            self.setFilterData(res.data, callback);
          } else {
            publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
          }
        });
      } else {
        callback && callback();
      }
    },

    setFilterData: function (data, callback) {
      var projectName = [], projectLocation = [];
      data.forEach(function (o) {
        projectName.push(o.pjName);
        o.pjLocationList.forEach(function (location) {
          if (projectLocation.indexOf(location) === -1) {
            projectLocation.push(location);
          }
        });
      });

      dataApp.filterObj.projectName = projectName;
      dataApp.filterObj.projectLocation = projectLocation;
      callback && callback();
    },

    getFilterDom: function (module) {
      var data = $.extend(true, {}, dataApp.filterObj, {module: module});
      return filterTpl(data);
    },

    events: function () {
      var showAllFilter = '[data-action="showAllFilter"]';
      $(document).off('click.showAllFilter.FPointer').on('click.showAllFilter.FPointer', showAllFilter, function () {
        var $closest = $(this).closest(".filter-group");
        if ($closest.hasClass("show-all")) {
          $closest.removeClass("show-all");
          $("#allFilterText").text("展开");
        } else {
          $closest.addClass("show-all");
          $("#allFilterText").text("收起");
        }
      });
    }
  };
  return filterApp;
});