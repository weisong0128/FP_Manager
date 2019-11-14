define([
  'model',
  'data',
  'tool',
  'public',
  'filter',
  'rightModal',
  'analysis/analysisModel',
  'viewTpl/analysis/modalSearchTpl',
  'viewTpl/analysis/tableUserDetailTpl'
], function (model, dataApp, tool, publicApp, filterApp, rightModalApp, analysisModel, modalSearchTpl, tableUserDetailTpl) {

  var app = {
    props: {},
    table: "",
    condition: "",
    filedModules: {
      "1": "usageFrequencyTable",
      "2": "normalSearchTable",
      "3": "rangeQueryTable",
      "4": "groupStatisticsTable",
      "5": "sortTable",
      "6": "blurSearchTable"
    },
    filedTables: {},
    detail:[],
    init: function (props) {
      this.props = props;
      publicApp.initAjaxSetup();
      this.initTableUseDetailTable();
      this.events();
    },

    initTableUseDetailTable: function () {

      if (this.table) {
        return;
      }
      var self = this;
      var $id = $('#tableUseDetailTable');
      var columns = [
        {
          data: "tableName",
          orderable: false,
          render: function (args) {
            return '<div class="w-default"><span class="ellipsis" title="' + args + '">' + args + '</span></div>';
          }
        },
        {
          data: "count",
          orderable: true,
          render: function (args) {
            return '<span class="w-xsmall" title="' + args + '">' + args + '</span>';
          }
        },
        {
          data: "pjlocation",
          orderable: false,
          render: function (args) {
            return '<div class="w-small"><span class="ellipsis" title="' + args + '">' + args + '</span></div>';
          }
        },
        {
          data: "dateStr",
          orderable: true,
          render: function (args) {
            return '<span class="w-small" title="' + publicApp.timeFormat(args) + '">' + publicApp.timeFormat(args) + '</span>';
          }
        },
        {
          data: "operation",
          orderable: false,
          render: function (args) {
            return '<div class="w-xsmall operation" data-detail="' + args + '">' +
                '<button type="button" class="btn btn-link" data-action="showDetail">详情</button>' +
                '</div>';
          }
        }
      ];
      var totalRows= 0,
        rowStart=0,
        rowEnd= 0;
      var ajaxFun = function (params, callback) {
        var condition = $.extend(true, {}, self.condition);
        condition.pjName = self.props.projectName;
        condition.pageNo = (params.start / params.length) + 1 || 1;
        condition.pageSize = params.length;
        condition.sort = params.order[0].dir;
        condition.sortName = params.order[0].column === 1 ? 'cnt': params.order[0].column === 3 ? 'date' : 'cnt';

        analysisModel.getBusinessDetails(condition).then(function (res) {
          var obj = {
            data: []
          };
          if (tool.checkStatusCode(res.code)) {
            var total = res.page && res.page.totalRows || 0;
            totalRows = total;
            rowStart = res.page.rowStart + 1;
            rowEnd = res.page.rowEnd;
            if (total > dataApp.tableTotal) {
              total = dataApp.tableTotal;
              publicApp._toastDialog("数据量过大，只显示前30000条", {"intent": "warning", "position": "top_center"});
            }
            var pageData = res.data || [];
            var dataTemp = [];
            // 如有需要，可对表格数据进行封装（下面对返回的字段进行了筛选）
            for (var i = 0; i < pageData.length; i++) {
              var obj2 = {};
              obj2['tableName'] = pageData[i].tableName || "";
              obj2['count'] = pageData[i].cnt || "";
              obj2['pjlocation'] = pageData[i].pjlocation || "";
              obj2['dateStr'] = pageData[i].date || "";
              obj2['operation'] = [pageData[i].tableName, pageData[i].pjlocation].join("*");
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
        order: [[1, 'desc'], [3, '']],
        scrollY: '570px',
        drawCallback:function(){
          $('#tableUseDetailTable_info').html("共 " + totalRows + " 条记录，当前显示第 " + rowStart + " - " + rowEnd + " 条");
        },
        columns: columns,
        ajax: ajaxFun
      };

      self.table = publicApp.initTable($id, options);
    },

    initTableFieldTable: function () {
      var partition = this.filedModules[dataApp.filedTableParams.partition];
      if (this.filedTables[partition]) {
        return;
      }
      var self = this;
      var $id = $('#' + partition);
      var columns = [
        {
          data: "num",
          orderable: false,
          render: function (args) {
            return '<span title="' + args + '">' + args + '</span>';
          }
        },
        {
          data: "rowName",
          orderable: false,
          render: function (args) {
            return '<div class="w-small"><span class="ellipsis" title="' + args + '">' + args + '</span></div>';
          }
        },
        {
          data: "rowCount",
          orderable: true,
          render: function (args) {
            return '<span title="' + args + '">' + args + '</span>';
          }
        },
        {
          data: "percent",
          orderable: false,
          render: function (args) {
            return '<span title="' + args + '">' + args + '</span>';
          }
        }
      ];

      var ajaxFun = function (params, callback) {
        var condition = $.extend(true, dataApp.filedTableParams, self.condition);

        condition.pjName = self.props.projectName;
        condition.tableName = self.detail[0];
        condition.pjLocation = self.detail[1];
        // condition.pageNo = (params.start / params.length) + 1 || 1;
        // condition.pageSize = params.length;

        analysisModel.getRowResult(condition).then(function (res) {
          var obj = {
            data: []
          };
          if (tool.checkStatusCode(res.code)) {
            var total = res.data.page && res.data.page.totalRows || 0;
            var pageData = res.data.rowResult || [];
            var dataTemp = [];
            // 如有需要，可对表格数据进行封装（下面对返回的字段进行了筛选）
            for (var i = 0; i < pageData.length; i++) {
              var obj2 = {};
              obj2['num'] = pageData[i].num || "";
              obj2['rowName'] = pageData[i].rowName || "";
              obj2['rowCount'] = pageData[i].rowCount || "";
              obj2['percent'] = pageData[i].percent || "";
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
        serverSide: false,
        destroy: true,
        ordering: true,
        orderClasses: false,
        order: [[2, 'desc']],
        lengthMenu: [10, 15, 25, 50, 100],
        pageLength: 15,
        scrollY: '100%',
        columns: columns,
        pagingType: "simple_numbers",
        infoCallback: function (settings, start, end, max, total) {
          return '共' + total + '条';
        },
        ajax: ajaxFun
      };

      this.filedTables[partition] = publicApp.initTable($id, options);
    },

    showDetail: function (data) {
      rightModalApp.init({
        title: modalSearchTpl(data),
        width: "630px",
        body: tableUserDetailTpl()
      });

      this.filedTables = {};
      var chooseFieldItem = '[data-action="chooseFieldItem"]';
      $(chooseFieldItem).eq(0).click();
    },

    setTableFilter: function (module) {
      var filterObj = filterApp.getFilter(dataApp.filterObj[module]);
      this.condition = $.extend(true, {}, dataApp.tableUseDetailParams, filterObj);

      publicApp.updateTable($('#' + module + 'Table'));
    },

    events: function () {
      var self = this;

      var tableUseDetailSearch = '[data-action="tableUseDetailSearch"]';
      $(document).off('click.tableUseDetailSearch.FPointer').on('click.tableUseDetailSearch.FPointer', tableUseDetailSearch, function () {
        var $closest = $(this).closest(".form-validate");
        var module = $closest.attr("id");
        var value = $closest.find('.keyword').val();
        dataApp.tableUseDetailParams.tableName = value;
        self.setTableFilter(module);
      });

      $("[data-action='tableUseDetailSearch']").click();

      var showDetail = '[data-action="showDetail"]';
      $(document).off('click.showDetail.FPointer').on('click.showDetail.FPointer', showDetail, function () {
        self.detail = $(this).closest('.operation').attr("data-detail").split("*");
        var data = {
          title: self.detail[0]
        };
        self.showDetail(data);
        $(this).parents("tr").addClass("currentRow");
      });

      var searchTableUserDetail = '[data-action="searchTableUserDetail"]';
      $(document).off('click.searchTableUserDetail.FPointer').on('click.searchTableUserDetail.FPointer', searchTableUserDetail, function () {
        var value = $(this).closest(".searchbox").find('input').val();
        var partition = self.filedModules[dataApp.filedTableParams.partition];
        dataApp.filedTableParams.rowName = value;
        publicApp.updateTable($('#' + partition));
      });

      var closeTableUserDetail = '[data-action="closeTableUserDetail"]';
      $(document).off('mousedown.closeTableUserDetail.FPointer').on('mousedown.closeTableUserDetail.FPointer', closeTableUserDetail, function () {
        $(this).closest(".searchbox").find('input').val('');
        var partition = self.filedModules[dataApp.filedTableParams.partition];
        dataApp.filedTableParams.rowName = "";
        publicApp.updateTable($('#' + partition));
      });

      var chooseFieldItem = '[data-action="chooseFieldItem"]';
      $(document).off('click.chooseFieldItem.FPointer').on('click.chooseFieldItem.FPointer', chooseFieldItem, function () {
        var $closest = $(this).closest(".table-field-container");
        var index = $(this).index();
        var partition = $(this).attr("data-partition");
        $closest.find(".tab-level-0").removeClass("active");
        $(this).addClass("active");
        $closest.find(".tab-panel-0").removeClass("active");
        $closest.find(".tab-panel-0").eq(index).addClass("active");
        dataApp.filedTableParams.partition = partition;
        self.initTableFieldTable();
      });
    }
  };
  return app;
});