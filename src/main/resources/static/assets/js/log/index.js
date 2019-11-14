define([
  'model',
  'data',
  'tool',
  'public',
  'header',
  'filter',
  'log/logModel',
  'viewTpl/log/createCheckTpl',
  '../log/uploadLog.js'
], function (model, dataApp, tool, publicApp, headerApp, filterApp, logModel, createCheckTpl, uploadLog) {

  var app = {
      props: {
        index: 2
      },
      table: "",
      checkState: {
        "1": "progress-bar-danger",
        "3": "",
        "0": "progress-bar-success"
      },
      page: 0,
      timestamp: "",
      filesName: [],
      condition: "",
      init: function () {
        headerApp.init(this.props);
        this.initFilter();
        this.initLogTable();
        this.initValidate();
        this.events();
      },

      initFilter: function () {
        filterApp.init(function () {
          $("#logFilter").html(filterApp.getFilterDom({module: "log"}));
          filterApp.initSelect();
        });
      },
      /**
       * 初始化表格
       *
       * **/
      initLogTable: function () {
        var self = this;
        var $id = $('#logListTable');
        var columns = [
          {
            render: function () {
              return '';
            }
          },
          {
            data: "pjName", orderable: false, render: function (params) {
              return '<div class="w-default ellipsis">' + params + '</div>';
            }
          },
          {data: "pjLocation", orderable: false},
          {
            data: "dateStr", orderable: true, render: function (params) {
              return '<div>' + publicApp.timeFormat(params) + '</div>';
            }
          },
          {
            data: "progress", orderable: false, render: function (params) {
              var res = params[1] === "1" ? '<span class="progress-percent progress-percent-danger"><span class="aidicon aidicon-close-circle">失败</span><a id="' + params[2] + '"  data-action="look-result" data-result="' + params[3] + '" >查看原因</a></span>' : '<span class="progress-percent" precent-uuid="' + params[2] + '">' + params[0] + '%</span>';

              return '<div class="progress progress-ty progress-table">' +
                '<div class="progress-bars">' +
                '<div class="progress-bar ' + self.checkState[params[1]] + '" role="progressbar" progress-uuid="' + params[2] + '" aria-valuenow="' + params[0] + '" aria-valuemin="0" aria-valuemax="100" style="width: ' + params[0] + '%;">' +
                '</div>' +
                '</div>' +
                res +
                '</span>' +
                '</div>';
            }
          },
          {
            data: "operation", orderable: false, render: function (args) {
              var url = '../log/result.html?projectName=' + encodeURI(encodeURI(args[0])) + '&logId=' + args[1] + '&pjLocations=' + encodeURI(encodeURI(args[3]) + '&captureTime=' + args[4]);
              var wait = '<a type="button" class="btn btn-link btn-disabled" disabled="disabled" data-action="wait-result">等待结果</a>';
              var look = '<a target="_self" href="' + url + '" class="btn btn-link">查看结果</a>';
              var again = '<a type="button" class="btn btn-link" data-action="again-issue" data-pjName="' + args[0] + '" data-uuid="' + args[1] + '" data-pjLocation="' + args[3] + '" data-createTime="' + args[4] + '">重新下发</a>';
              var res = "";
              switch (args[2]) {
                case "1":
                  res = wait + again;
                  break;
                case "0":
                  res = look;
                  break;
                case "3":
                  res = wait;
                  break;
                default:
                  break;
              }
              return '<div class="operation log-operation" data-logId="' + args[1] + '">' + res + '</div>';
            }
          },
          {data: "uuid", visible: false}
        ];

        var totalRows = 0,
          rowStart = 0,
          rowEnd = 0;
        var ajaxFun = function (params, callback) {
          var condition = $.extend(true, {}, self.condition);
          condition.pageNo = (params.start / params.length) + 1 || 1;
          condition.pageSize = params.length;
          condition.sort = params.order[0].dir;
          condition.userId = sessionStorage.getItem("userId");
          logModel.getLogList(condition).then(function (res) {
            var obj = {
              data: []
            };
            if (tool.checkStatusCode(res.code)) {
              var total = res.data.page.totalRows || 0;
              totalRows = total;
              rowStart = res.data.page.rowStart + 1;
              rowEnd = res.data.page.rowEnd;
              if (total > dataApp.tableTotal) {
                total = dataApp.tableTotal;
                publicApp._toastDialog("数据量过大，只显示前30000条", {"intent": "warning", "position": "top_center"});
              }
              var pageData = res.data.data || [];
              var dataTemp = [];
              // 如有需要，可对表格数据进行封装（下面对返回的字段进行了筛选）
              for (var i = 0; i < pageData.length; i++) {
                var obj2 = {};
                obj2['uuid'] = pageData[i].uuid;
                obj2['pjName'] = pageData[i].projectName;
                obj2['pjLocation'] = pageData[i].address;
                obj2['dateStr'] = pageData[i].createTime;
                obj2['progress'] = [pageData[i].progress, pageData[i].parsingState, pageData[i].uuid, pageData[i].result];
                obj2['state'] = pageData[i].parsingState;
                obj2['operation'] = [pageData[i].projectName, pageData[i].uuid, pageData[i].parsingState, pageData[i].address, pageData[i].createTime];
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
          }).then(function () {
            var con = self.getTableLogId().join(",");
            logModel.getProgressValue(con).then(function (res) {
              if (tool.checkStatusCode(res.code)) {
                var data = res.data;
                if (data.length > 0) {
                  self.getProgressValue();
                }
              } else {
                publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
              }
            });
          });
        };

        var options = {
          destroy: true,
          serverSide: true,
          ordering: true,
          orderClasses: false,
          order: [[3, 'desc']],
          scrollY: '570px',
          columns: columns,
          ajax: ajaxFun,
          columnDefs: [{
            orderable: false,
            className: 'select-checkbox ',
            targets: 0
          }],
          language: {
            sEmptyTable: '没有任何数据，现在就 <a data-action="empty-add">新增一个</a> 吧！'
          },
          select: {
            // 可选择的配置有： 'api'、'single'、'multi'、'os'、'multi+shift'
            style: 'multi',
            // 控制是否在左下角显示选中信息
            info: false,
            selector: 'td:first-child'
          },
          drawCallback: function () {
            $('#logListTable_info').html("共 " + totalRows + " 条记录，当前显示第 " + rowStart + " - " + rowEnd + " 条");
          }
        };
        self.table = publicApp.initTable($id, options);
      },
      /**批量删除日志数据弹窗*/
      delLogDialog: function (data) {
        var self = this;
        Dialog.open({
          id: "delLogDialog",
          title: "<span class='aidicon aidicon-alert-circle-outline aidicon-warning'></span><span>确定要删除这" + data.length + "条数据吗？</span>",
          width: 420,
          height: 'auto',
          modal: true,
          content: '<div class="padding-left-large-5"><div class="font-color-weak padding-horizontal-large-2 padding-bottom-large-2">记录删除后将无法恢复，确定删除吗？</div></div>',
          button: [
            {
              id: "delSure",
              label: "确定",
              intent: "primary",
              focus: true,
              click: function () {
                self.delLogs(data);

              }
            },
            {
              id: "delCancel",
              label: "取消"
            }
          ]
        });
      },
      /**批量删除表格数据*/
      delLogs: function (data) {
        var self = this;
        var condition = {};
        condition.uuids = data.join(",");
        logModel.deleteLogs(data.join(",")).then(function (res) {
          if (tool.checkStatusCode(res.code)) {
            publicApp._toastDialog(res.msg, {"intent": "success", "position": "top_center"});
            publicApp.updateTable($('#logListTable'));
            self.clearAllChecked(-1);
          } else {
            publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
          }
        });
      },
      /**创建检测弹窗*/
      createCheckDialog: function () {
        var self = this;
        Dialog.open({
          id: "createCheck",
          title: "创建检测",
          width: 650,
          height: 'auto',
          modal: true,
          content: createCheckTpl(),
          button: [
            {
              id: "sureUpload",
              label: "开始上传",
              intent: "primary",
              focus: true,
              click: function () {
                self.callFormValid();
                if ($("#filesList").children().length === 0) {
                  publicApp._toastDialog("未选择文件", {"intent": "danger", "position": "top_center"});
                  return false;
                }

                if (!self.isFormError()) {
                  var projectName = $.trim($("#pjName :selected").text());
                  var projectLocation = $.trim($("#pjLocation").val());
                  uploadLog.startUpload(projectName, projectLocation);
                  $("#pjName").attr("disabled", "disabled");
                  $("#pjLocation").attr("disabled", "disabled").css("color", "#c5cedf");
                }
                return false;
              }
            },
            {
              id: "sureAnalysis",
              label: "开始检测",
              intent: "primary",
              focus: true,
              click: function () {
                self.callFormValid();
                return self.submitCheckForm();
              }
            },
            {
              id: "createCancel",
              label: "取消"
            }
          ],
          onShow: function () {
            self.timestamp = Date.parse(new Date()) / 1000;
            $("#sureUpload").addClass("disabled");
            $("#sureAnalysis").addClass("disabled");
            uploadLog.init(self.timestamp);
            self.initDialogSelect();
            self.validateForm();
          }
        });
      },
    initValidate: function () {
      $.validator.addMethod("isLegal", function (value) {
        var reg = /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/;
        return reg.test(value);
      }, "仅支持字母汉字数字下划线");
    },
    /**
     * 初始化表单验证
     */
    validateForm: function () {
      $("#createCheckForm").validate({
        debug: true,//只验证不提交表单
        rules: {
          pjName: {
            required: true
          },
          pjLocation: {
            required: true,
            isLegal: ""
          }
        },
        messages: {
          pjName: {
            required: "项目名称不能为空"
          },
          pjLocation: {
            required: "安装地市不能为空",
            isLegal: "仅支持字母汉字数字下划线"
          }
        }
      });
    },
      /**调用表单验证*/
      callFormValid: function () {
        $("#createCheckForm").valid();
      },
      /**
       * 判断表单是否还有错误
       */
      isFormError: function () {
        return $('#createCheckForm').find('.form-group.has-error').length > 0;
      },
      /**
       * 提交开始检测表单
       */
      submitCheckForm: function () {
        var self = this;
        if (self.isFormError()) {
          return false;
        } else if ($("#filesList .upload-wrap").length <= 0) {
          publicApp._toastDialog("未选择日志文件", {"intent": "danger", "position": "top_center"});
          return false;
        } else if ($("#filesList .progress-bar-success").length <= 0) {
          publicApp._toastDialog("不存在上传成功的文件", {"intent": "danger", "position": "top_center"});
          return false;
        }
        var condition = {};
        condition.projectName = $.trim($("#pjName :selected").text());
        condition.projectLocation = $.trim($("#pjLocation").val());
        condition.createTime = self.timestamp;
        condition.userId = sessionStorage.getItem("userId");

        logModel.startAnalyse(condition).then(function (res) {

          if (tool.checkStatusCode(res.code)) {
            publicApp._toastDialog(res.msg, {"intent": "success", "position": "top_center"});
            publicApp.updateTable($('#logListTable'));

            self.addHighlight(res);
            self.getProgressValue();

            publicApp.toggleDisabled(".toggle-disabled", -1);
          } else {
            publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
          }
        });
      },
      //新增高亮
      addHighlight: function (res) {
        var $def = $.Deferred();
        var wait = function (def) {
          setTimeout(function () {
            $("[data-logId=" + res.data.uuid + "]").parents("tr").addClass("add-active");
            def.resolve();
          }, 500);
          return def;
        };
        $.when(wait($def)).done(function () {
          setTimeout(function () {
            $("[data-logId=" + res.data.uuid + "]").parents("tr").removeClass("add-active");
          }, 3000);
        });
      },
      //获取检测状态
      getProgressValue: function () {
        var self = this;
        var interval = setInterval(function () {
          var condition = self.getTableLogId().join(",");
          logModel.getProgressValue(condition).then(function (res) {
            if (tool.checkStatusCode(res.code)) {
              var data = res.data;
              self.changeProgressStyle(data);
            } else {
              publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
            }
            if (res.data.length === 0) {
              clearInterval(interval);
            }

            for (var i = 0; i < res.data.length; i++) {
              if (!res.data[i].finish) {
                break;
              }
              if (i === res.data.length - 1) {
                clearInterval(interval);
              }
            }

          });
        }, 2000);
      },
      //重新下发
      againIssiue: function (condition) {
        var self = this;

        logModel.againIssiue(condition).then(function (res) {
          if (tool.checkStatusCode(res.code)) {
            console.log(res);
            var data = res.data;
            self.getProgressValue(data, true);
          } else {
            publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
          }
        });

      },
      //获取当前表格的所有data-logId
      getTableLogId: function () {
        var $dom = $(".log-operation");
        var logIdArr = new Array();
        for (var i = 0; i < $dom.length; i++) {
          logIdArr.push($dom.eq(i).attr("data-logId"));
        }
        return logIdArr;
      },
      //改变滚动条样式
      changeProgressStyle: function (data, again) {
        data.map(function (item) {
          if (!item.isError) {
            var $dom = $("[progress-uuid='" + item.uuid + "']");
            if (again) {
              $dom.removeClass("progress-bar-danger");
              // $dom.parents(".progress").children(".progress-percent").css("display", "none");
              $dom.parents(".progress").children(".progress-percent").html("<span>" + item.process + "%</span>");
            }

            $dom.attr("aria-valuenow", item.process);
            $dom.css("width", item.process + "%");
            $("[precent-uuid='" + item.uuid + "']").html(item.process + "%");
            if (item.process === 100) {
              $dom.addClass("progress-bar-success");
            }
            if (item.show) {
              var $currentRow = $("[data-logId='" + item.uuid + "']").parents("tr");
              var projectName = $currentRow.children("td:nth-child(2)").text(),
                logId = item.uuid,
                pjLocations = $currentRow.children("td:nth-child(3)").text(),
                captureTime = new Date($currentRow.children("td:nth-child(4)").text()).getTime() / 1000;

              var url = '../log/result.html?projectName=' + encodeURI(encodeURI(projectName)) + '&logId=' + logId + '&pjLocations=' + encodeURI(encodeURI(pjLocations) + '&captureTime=' + captureTime);
              $("[data-logId='" + item.uuid + "']").html('<a target="_self" href="' + url + '" class="btn btn-link">查看结果</a>');
            }
          } else {
            publicApp.updateTable($('#logListTable'));
          }
        });
      },
      initDialogSelect: function () {
        model.getAllFilter().then(function (res) {
          if (tool.checkStatusCode(res.code)) {
            var arr = [];
            res.data.map(function (item, index) {
              var obj = {};
              obj.id = index;
              obj.text = item.pjName;
              arr.push(obj);
            });
            $("#pjName").select2({
              placeholder: "请选择项目名称",
              width: $("#pjLocation").width() + 26,
              dropdownCssClass: "selectDrop",
              minimumResultsForSearch: -1,
              data: arr
            });
            $("#pjName").select2('val', " ");
          }
        });
      },
      setTableFilter: function (module) {
        var filterObj = filterApp.getFilter(dataApp.filterObj[module]);
        if (filterObj.searchTime === undefined || filterObj.searchTime === "") {
          filterObj.timeTag = 'all';
        } else if (filterObj.searchTime === "custom") {
          filterObj.timeTag = "";
        } else {
          filterObj.timeTag = filterObj.searchTime;
        }
        if (filterObj.pjName !== "" || filterObj.pjName !== undefined) {
          filterObj.projectName = filterObj.pjName;
        }
        if (filterObj.pjLocation !== "" || filterObj.pjLocation !== undefined) {
          filterObj.address = filterObj.pjLocation;
        }
        if (filterObj.startTime !== "" || filterObj.startTime !== undefined) {
          filterObj.starTime = filterObj.startTime;
          delete (filterObj["startTime"]);
        }
        this.condition = $.extend(true, {}, filterObj);
        if(filterObj.searchTime === "custom") {
          var timeArr = $.trim($("#"+module + " .customTimeTag").val()).split('~') || "";
          this.condition.starTime = tool.getAbsoluteSecond(timeArr[0]) || 0;
          this.condition.endTime = tool.getAbsoluteSecond(timeArr[1]) || 0;
        }
        publicApp.updateTable($('#logListTable'));
      },
      //获取表格多选框选中的值
      getCheckboxData: function () {
        var self = this;
        var selectedRows = self.table.rows('.selected').data(),
          len = selectedRows.length,
          arr = [];
        for (var i = 0; i < len; i++) {
          arr.push(selectedRows[i].uuid);
        }

        return arr;
      },
      //清除log表全选按钮的选中样式
      clearAllChecked: function (value) {
        var $selectAll = $('#logListTable_wrapper [data-action="select-all-event"]').parent('th');
        $selectAll.hasClass("selected") ? $selectAll.removeClass('selected') : '';
        publicApp.toggleDisabled(".toggle-disabled", value);
      },
      events: function () {
        var self = this;

        var logSearch = '[data-action="logSearch"]';
        $(document).off('click.logSearch.log').on('click.logSearch.log', logSearch, function () {
          var $closest = $(this).closest(".form-validate");
          var module = $closest.attr("id");
          self.setTableFilter(module);
        });

        var createCheck = '[data-action="createCheck"]';
        $(document).off('click.createCheck.log').on('click.createCheck.log', createCheck, function () {
          self.createCheckDialog();
        });

        //生成报告
        var generateReport = '[data-action="generate-report"]';
        $(document).off('click.generateReport.log').on('click.generateReport.log', generateReport, function () {
            var selectedValue = self.getCheckboxData().join(",");

            $("#generateReport").attr("href", dataApp.urlDomain + "/log/batchWordExport?uuids=" + selectedValue);
            // self.clearAllChecked(-1);
            // var tipsDdialog = Dialog.open({
            //   id: "showResult",
            //   title: false,
            //   width: "auto",
            //   height: "auto",
            //   theme: 'resultInfo',
            //   content: function () {
            //     var resultInfo = '<style type="text/css">' +
            //       '.resultInfo .idlg-close {display:none !important;}' +
            //       '.resultInfo .idlg-main {min-height:25px;padding:5px 10px !important;word-break:break-word;}' +
            //       '</style>' +
            //       '<div style="text-align:center;color:#666;font-size:14px;">该功能待开发</div>';
            //     return resultInfo;
            //   },
            //   follow: {target: "generateReport", placement: 'top'}
            // });
            //
            // setTimeout(function () {
            //   tipsDdialog.close();
            // }, 5000);
          }
        );

//批量删除
        var deleteLogs = '[data-action="delete-logs"]';
        $(document).off('click.deleteLogs.log').on('click.deleteLogs.log', deleteLogs, function () {
          var arr = self.getCheckboxData();
          self.delLogDialog(arr);
        });

//查看失败原因
        var lookResult = '[data-action="look-result"]';
        var dialog;
        $(document).off('mouseover.lookResult.log').on('mouseover.lookResult.log', lookResult, function () {
          var data = $(this).attr("data-result");

          dialog = Dialog.open({
            id: "showResult",
            title: false,
            width: "auto",
            height: "auto",
            theme: 'resultInfo',
            content: function () {
              var resultInfo = '<style type="text/css">' +
                '.resultInfo .idlg-close {display:none !important;}' +
                '.resultInfo .idlg-main {min-height:25px;min-width:200px;max-width:500px;padding:0px 10px !important;word-break:break-word;}' +
                '</style>' +
                '<div style="text-align:center;color:#666;font-size:14px;">' + data + '</div>';
              return resultInfo;
            },
            follow: {target: $(this).attr("id"), placement: 'bottom'}
          });
        });

        $(document).off('mouseout.lookResult.log').on('mouseout.lookResult.log', lookResult, function () {
          dialog.close();
        });

//重新下发
        var againIssue = '[data-action="again-issue"]';
        $(document).off('click.againIssue.log').on('click.againIssue.log', againIssue, function () {
          var condition = {};
          condition.pjName = $(this).attr("data-pjName");
          condition.pjLocation = $(this).attr("data-pjLocation");
          condition.createTime = $(this).attr("data-createTime");
          condition.uuid = $(this).attr("data-uuid");

          self.againIssiue(condition);
        });

        var emptyAdd = '[data-action="empty-add"]';
        $(document).off('click.emptyAdd.log').on('click.emptyAdd.log', emptyAdd, function () {
          $('[data-action="createCheck"]').click();
        });

        /*全选按钮*/
        var exploringSelectAllEvent = '#logListTable_wrapper [data-action="select-all-event"]';
        $(document).off('click.exploringSelectAllEvent.log').on('click.exploringSelectAllEvent.log', exploringSelectAllEvent, function () {
          var $selectAll = $(this).parent('th');
          publicApp.tableAllSelect(self.table, $selectAll);
          publicApp.toggleDisabled(".toggle-disabled", self.table.rows({selected: true}).count());
        });

        /*表格选择按钮*/
        var selectCheckbox = '#logListTable td.select-checkbox';
        $(document).off('click.selectCheckbox.log').on('click.selectCheckbox.log', selectCheckbox, function () {
          var $selectAll = $(exploringSelectAllEvent).parent('th');
          publicApp.isTableAllSelect(self.table, $selectAll);
          publicApp.toggleDisabled(".toggle-disabled", self.table.rows({selected: true}).count());
        });

        /*表格分页点击事件*/
        var paginateButton = '.paginate_button';
        $(document).off('click.paginateButton.log').on('click.paginateButton.log', paginateButton, function () {
          if (self.table.page() !== self.page) {
            var $selectAll = $(exploringSelectAllEvent).parent('th');
            publicApp.isTableAllSelect(self.table, $selectAll);
            self.clearAllChecked(-1);
          }
          self.page = self.table.page();
        });

        var tinkerDetail = '[data-action="tinker-detail"]';
        $(document).off('mousedown.tinkerDetail.FPointer').on('mousedown.tinkerDetail.FPointer', tinkerDetail, function () {
          window.location.href = '../analysis/tinkerHelp.html';
        });
      }
    }
  ;
  return app;
})
;