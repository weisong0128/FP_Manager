define([
  'model',
  'data',
  'tool',
  'public',
  'header',
  'rightModal',
  'viewTpl/analysis/tinkerHelpTpl'
], function (model, dataApp, tool, publicApp, headerApp, rightModalApp, tinkerHelpTpl) {

  var app = {
    props: {
      index: 6
    },
    keyWord: "",
    init: function () {
      headerApp.init(this.props);
      this.initTable();
      this.events();
    },

    initTable: function (condition) {
      var self = this;
      var $id = $('#tinkerTable');
      var columns = [
        {
          data: "errCode", orderable: false, render: function (args) {
            return '<div class="w-xsmall">' + self.keyWordLight(args, self.keyWord) + '</div>';
          }
        }, {
          data: "errKeyWord", orderable: false, render: function (args) {
            return '<div class="w-small ellipsis" title=' + args + '>' + self.keyWordLight(args, self.keyWord) + '</div>';
          }
        }, {
          data: "errReason", orderable: false, render: function (args) {
            return "<div class='w-large three-ellipis' title=\'" + args + "\'>" + self.keyWordLight(args, self.keyWord) + "</div>";
          }
        }, {
          data: "solution", orderable: false, render: function (args) {
            return "<div class='w-large three-ellipis' title=\'" + args + "\'>" + self.keyWordLight(args, self.keyWord) + "</div>";
          }
        }, {
          data: "operation", orderable: false, render: function (args) {
            return "<div class='operation w-xsmall' data-errKeyWord=\'"+args[2]+"\' data-reason= \'" + args[0] + "\' data-solution= \'" + args[1] + "\'>" +
              '<button type="button" class="btn btn-link" data-action="show-detail">详情</button>' +
              '</div>';
          }
        }
      ];

      var totalRows= 0,
        rowStart=0,
        rowEnd= 0;

      var ajaxFun = function (params, callback) {
        var con = $.extend(true, {}, condition);
        con.pageNo = (params.start / params.length) + 1 || 1;
        con.pageSize = params.length;
        model.getTinkerHelp(con).then(function (res) {
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
              publicApp._toastDialog(res.msg, {"intent": "warning", "position": "top_center"});
            }
            var pageData = res.data.fpHelp || [];
            var dataTemp = [];
            // 如有需要，可对表格数据进行封装（下面对返回的字段进行了筛选）
            for (var i = 0; i < pageData.length; i++) {
              var obj2 = {};
              obj2['errCode'] = pageData[i].errCode || "";
              obj2['errKeyWord'] = pageData[i].errKeyWord || "";
              var errReason = pageData[i].errReason !== null ? pageData[i].errReason.replace(/\'|\"/g,"\'") : "";
              var solution = pageData[i].solution !== null ? pageData[i].solution.replace(/\'|\"/g,"\'") : "";
              obj2['errReason'] = errReason;
              obj2['solution'] = solution;
              obj2['operation'] = [errReason, solution, pageData[i].errKeyWord];
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
        ordering: false,
        scrollY: '650px',
        drawCallback:function(){
          $('#tinkerTable_info').html("共 " + totalRows + " 条记录，当前显示第 " + rowStart + " - " + rowEnd + " 条");
        },
        columns: columns,
        ajax: ajaxFun
      };

      publicApp.initTable($id, options);
    },

    showDetail: function (detail) {
      rightModalApp.init({
        title: "FP报错详解",
        body: tinkerHelpTpl(detail)
      });
    },

    //关键字高亮
    keyWordLight: function (str, keyWord) {
      var reg = new RegExp(("(" + keyWord + ")"), "gm");
      var replace = '<span style=\"color:#f00;\">$1</span>';
      return str.replace(reg, replace);
    },

    events: function () {
      var self = this;

      var tinkerSearch = '[data-action="tinker-search"]';
      $(document).off('click.tinkerSearch.tinkerHelp').on('click.tinkerSearch.tinkerHelp', tinkerSearch, function () {
        var keyWord = $("#keyWord").val();
        var condition = {};
        condition.errKeyWord = keyWord;
        self.keyWord = keyWord;
        self.initTable(condition);
      });

      $('.searchbox .aidicon-close').on('mousedown', function () {
        $(this).siblings('input').val('');
        self.keyWord = '';
        self.initTable();
      });

      $(document).keyup(function (e) {
        if (e.keyCode === 13) {
          $("[data-action='tinker-search']").click();
        }
      });

      var showDetail = '[data-action="show-detail"]';
      $(document).off('click.showDetail.tinkerHelp').on('click.showDetail.tinkerHelp', showDetail, function () {
        var obj = {
          reason: $(this).closest('.operation').attr("data-reason"),
          solution: $(this).closest('.operation').attr("data-solution"),
          errKeyWord: $(this).closest('.operation').attr("data-errKeyWord")
        };
        self.showDetail(obj);
        $(this).parents("tr").addClass("currentRow");
      });
    }
  };
  return app;
});