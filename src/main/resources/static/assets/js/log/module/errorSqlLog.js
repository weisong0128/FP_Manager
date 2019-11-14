define([
  'model',
  'data',
  'tool',
  'public',
  'filter',
  'rightModal',
  'log/logModel',
  'viewTpl/analysis/errorSqlTpl'
], function (model, dataApp, tool, publicApp, filterApp, rightModalApp, logModel, errorSqlTpl) {

  var app = {
    props: {},
    table: "",
    condition: "",
    init: function (props) {
      this.props = props;
      publicApp.initAjaxSetup();
      this.initErrorSqlLogTable();
      this.events();
    },

    initErrorSqlLogTable: function () {

      if (this.table) {
        return;
      }
      var self = this;
      var $id = $('#errorSqlLogTable');
      var columns = [
        {
          data: "dateStr",
          orderable: true,
          render: function (args) {
            return '<div class="w-small"><span title="' + args + '">' + args + '</span></div>';
          }
        },
        {
          data: "tag",
          orderable: false,
          render: function (args) {
            return '<div class="w-small"><span class="ellipsis" title="' + args + '">' + args + '</span></div>';
          }
        },
        {
          data: "alterTag",
          orderable: false,
          render: function (args) {
            return '<div class="w-default"><span class="ellipsis" title="' + args + '">' + args + '</span></div>';
          }
        },
        {
          data: "sqlResult",
          orderable: false,
          render: function (args) {
            return '<div class="w-large"><span class="two-ellipsis" title="' + args + '">' + args + '</span></div>';
          }
        },
        {
          data: "operation",
          orderable: false,
          render: function (args) {
            return '<div class="w-xsmall operation" data-tag="' + args[0] + '" data-alterTag="' + args[1] + '" data-sqlResult="' + args[2] + '">' +
              '<button type="button" class="btn btn-link" data-action="showErrorSqlLogDetail">详情</button>' +
              '</div>';
          }
        }
      ];

      var totalRows= 0,
        rowStart=0,
        rowEnd= 0;
      var ajaxFun = function (params, callback) {
        var urlParams = tool.getUrlParams();
        var condition = $.extend(true, {}, self.condition);
        condition.pageNo = (params.start / params.length) + 1 || 1;
        condition.pageSize = params.length;
        condition.sort = params.order[0].dir;
        condition.pjName = decodeURI(decodeURI(urlParams.query["projectName"]));
        condition.pjLocation = decodeURI(decodeURI(urlParams.query["pjLocations"]));
        condition.captureTime = urlParams.query["captureTime"];
        logModel.getErrorSql(condition).then(function (res) {
          var obj = {
            data: []
          };
          if (tool.checkStatusCode(res.code)) {
            var total = res.data.page && res.data.page.totalRows || 0;
            totalRows = total;
            rowStart = res.data.page.rowStart + 1;
            rowEnd = res.data.page.rowEnd;
            if (total > dataApp.tableTotal) {
              total = dataApp.tableTotal;
              publicApp._toastDialog("数据量过大，只显示前30000条", {"intent": "warning", "position": "top_center"});
            }
            var pageData = res.data.errResult || [];
            var dataTemp = [];
            // 如有需要，可对表格数据进行封装（下面对返回的字段进行了筛选）
            for (var i = 0; i < pageData.length; i++) {
              var obj2 = {};
              // obj2['pjLocation'] = pageData[i].pjLocation || "";
              obj2['dateStr'] = pageData[i].dateStr || "";
              obj2['tag'] = pageData[i].tag !== null ? pageData[i].tag.replace(/\'|\"/g,"\'") : "";
              obj2['alterTag'] = pageData[i].alterTag !== null ? pageData[i].alterTag.replace(/\'|\"/g,"\'") : "";
              obj2['sqlResult'] = pageData[i].sqlResult !== null ? pageData[i].sqlResult.replace(/\'|\"/g,"\'") : "";
              obj2['operation'] = [pageData[i].tag, pageData[i].alterTag, pageData[i].sqlResult];
              dataTemp.push(obj2);
            }
            obj.data = dataTemp;
            obj.draw = params.draw;
            obj.recordsTotal = total;
            obj.recordsFiltered = total;
          } else {
            publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
          }
          callback && callback(obj);
        });
      };

      var options = {
        serverSide: true,
        destroy: true,
        ordering: true,
        orderClasses: false,
        order: [[0, 'desc']],
        scrollY: '570px',
        drawCallback:function(){
          $('#errorSqlLogTable_info').html("共 " + totalRows + " 条记录，当前显示第 " + rowStart + " - " + rowEnd + " 条");
        },
        columns: columns,
        ajax: ajaxFun
      };

      self.table = publicApp.initTable($id, options);
    },

    showDetail: function (detail) {
      rightModalApp.init({
        title: "不合格SQL详情",
        body: errorSqlTpl(detail)
      });
    },

    setTableFilter: function (module) {
      var filterObj = filterApp.getFilter(dataApp.filterObj[module]);
      if (filterObj.searchTime === undefined || filterObj.searchTime === "") {
        filterObj.timeTag = 'all';
      } else if (filterObj.searchTime === "custom") {
        filterObj.timeTag = 'customZone';
      } else {
        filterObj.timeTag = filterObj.searchTime;
      }
      this.condition = $.extend(true, {}, dataApp.errorSqlLogParams, filterObj);
      if(filterObj.searchTime === "custom") {
        var timeArr = $.trim($("#"+module + " .customTimeTag").val()).split('~') || "";
        this.condition.startTime = tool.getAbsoluteSecond(timeArr[0]) || 0;
        this.condition.endTime = tool.getAbsoluteSecond(timeArr[1]) || 0;
      }
      publicApp.updateTable($('#errorSqlLogTable'));
    },

    events: function () {
      var self = this;

      var errorSqlLogSearch = '[data-action="errorSqlLogSearch"]';
      $(document).off('click.errorSqlLogSearch.log').on('click.errorSqlLogSearch.log', errorSqlLogSearch, function () {
        var $closest = $(this).closest(".form-validate");
        var module = $(this).closest(".form-validate").attr("id");
        var value = $closest.find('.keyword').val();
        dataApp.errorSqlLogParams.keyWord = value;
        dataApp.errorSqlLogParams.isDistinct = Number($closest.find('.isDelDitto').prop("checked"));
        self.setTableFilter(module);
      });

      var showErrorSqlLogDetail = '[data-action="showErrorSqlLogDetail"]';
      $(document).off('click.showErrorSqlLogDetail.log').on('click.showErrorSqlLogDetail.log', showErrorSqlLogDetail, function () {
        var obj = {
          tag: $(this).closest('.operation').attr("data-tag"),
          alterTag: $(this).closest('.operation').attr("data-alterTag"),
          sqlResult: $(this).closest('.operation').attr("data-sqlResult")
        };
        self.showDetail(obj);
        $(this).parents("tr").addClass("currentRow");
      });

      var tinkerDetail = '[data-action="tinker-detail"]';
      $(document).off('mousedown.tinkerDetail.FPointer').on('mousedown.tinkerDetail.FPointer', tinkerDetail, function () {
        window.location.href = '../analysis/tinkerHelp.html';
      });
    }
  };
  return app;
});