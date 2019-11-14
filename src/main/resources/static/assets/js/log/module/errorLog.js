define([
  'model',
  'data',
  'tool',
  'public',
  'filter',
  'rightModal',
  'log/logModel'
], function (model, dataApp, tool, publicApp, filterApp, rightModalApp, logModel) {

  var app = {
    props: {},
    table: "",
    // errLevel: {
    //   CRIT: "重度",
    //   ERRO: "中度",
    //   WARN: "轻度",
    //   INFO: "环境变量"
    // },
    condition: "",
    init: function (props) {
      this.props = props;
      publicApp.initAjaxSetup();
      this.initErrorInfoTable();
      this.events();
    },

    initErrorInfoTable: function () {

      if (this.table) {
        return;
      }
      var self = this;
      var $id = $('#errorLogTable');
      var columns = [
        {
          data: "dateStr",
          orderable: true,
          render: function (args) {
            return '<div class="w-small"><span class="ellipsis" title="' + args + '">' + args + '</span></div>';
          }
        },
        {
          data: "errCode",
          orderable: false,
          render: function (args) {
            return '<div class="w-xsmall"><span title="' + args + '">' + args + '</span></div>';
          }
        },
        {
          data: "errLevel",
          orderable: false,
          render: function (args) {
            return '<span class="w-xsmall ellipsis" title="' + args + '">' + args + '</span>';
          }
        },
        {
          data: "errInfo",
          orderable: false,
          render: function (args) {
            return '<div class="w-large"><span class="two-ellipsis" title="' + args + '">' + args + '</span></div>';
          }
        },
        {
          data: "operation",
          orderable: false,
          render: function (args) {
            return '<div class="w-xsmall operation" data-detail="' + args + '">' +
              '<button type="button" class="btn btn-link" data-action="showErrorLogDetail">详情</button>' +
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
        condition.sortName = 'date';

        condition.pjName = decodeURI(decodeURI(urlParams.query["projectName"]));
        condition.pjLocation = decodeURI(decodeURI(urlParams.query["pjLocations"]));
        condition.captureTime = urlParams.query["captureTime"];
        if (condition.timeTag === "" || condition.timeTag === undefined) {
          condition.timeTag = "all";
        }
        logModel.getErrorLog(condition).then(function (res) {
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
            var pageData = res.data.operation || [];
            var dataTemp = [];
            // 如有需要，可对表格数据进行封装（下面对返回的字段进行了筛选）
            for (var i = 0; i < pageData.length; i++) {
              var obj2 = {};
              // obj2['pjLocation'] = pageData[i].pjLocation || "";
              obj2['dateStr'] = pageData[i].dateStr || "";
              obj2['errCode'] = pageData[i].errcode || "";
              obj2['errLevel'] = pageData[i].errLevel || "";
              obj2['errInfo'] = pageData[i].errInfo !== null ? pageData[i].errInfo.replace(/\'|\"/g,"\'") : "";
              obj2['operation'] = pageData[i].errInfo || "";
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
        columns: columns,
        ajax: ajaxFun,
        drawCallback:function(){
          $('#errorLogTable_info').html("共 " + totalRows + " 条记录，当前显示第 " + rowStart + " - " + rowEnd + " 条");
        }
      };

      self.table = publicApp.initTable($id, options);
    },

    showDetail: function (detail) {
      rightModalApp.init({
        title: "错误信息",
        body: "<span>" + detail + "</span>"
      });
    },

    setTableFilter: function (module) {
      var filterObj = filterApp.getFilter(dataApp.filterObj[module]);
      if (filterObj.searchTime === undefined || filterObj.searchTime === "" || filterObj.searchTime === "custom") {
        filterObj.timeTag = 'all';
      } else {
        filterObj.timeTag = filterObj.searchTime;
      }
      if (filterObj.errorLevel !== undefined || filterObj.errorLevel !== "") {
        filterObj.errLevel = filterObj.errorLevel;
      }
      this.condition = $.extend(true, {}, dataApp.errorDetailLogParams, filterObj);
      if(filterObj.searchTime === "custom") {
        var timeArr = $.trim($("#"+module + " .customTimeTag").val()).split('~') || "";
        this.condition.startTime = tool.getAbsoluteSecond(timeArr[0]) || 0;
        this.condition.endTime = tool.getAbsoluteSecond(timeArr[1]) || 0;
      }
      publicApp.updateTable($('#errorLogTable'));
    },

    events: function () {
      var self = this;

      var errorLogSearch = '[data-action="errorLogSearch"]';
      $(document).off('click.errorLogSearch.log').on('click.errorLogSearch.log', errorLogSearch, function () {
        var $closest = $(this).closest(".form-validate");
        var module = $closest.attr("id");
        var value = $closest.find('.keyword').val();
        dataApp.errorDetailLogParams.keyWord = value;
        dataApp.errorDetailLogParams.isDistinct = Number($closest.find('.isDelDitto').prop("checked"));
        self.setTableFilter(module);
      });

      var showErrorLogDetail = '[data-action="showErrorLogDetail"]';
      $(document).off('click.showErrorLogDetail.log').on('click.showErrorLogDetail.log', showErrorLogDetail, function () {
        var detail = $(this).closest('.operation').attr("data-detail");
        self.showDetail(detail);
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