define([
  'model',
  'data',
  'tool',
  'public',
  'header',
  'authorization/authorizeModel',
  'viewTpl/authorize/authorizeTpl',
  'filter'
], function (model, dataApp, tool, publicApp, headerApp, authorizeModel, authorizeTpl, filterApp) {
  var table = null;
  var app = {
    props: {
      index: 4
    },
    page: 0,
    options: {},
    init: function () {
      headerApp.init(this.props);
      this.initAuthorizeTable();
      this.event();
      this.initValidate();
      this.initFilter();
    },
    /**
     * 初始化表格
     */
    initAuthorizeTable: function () {
      var $id = $("#authorizeTable");
      var columns = [
        {
          render: function () {
            return '';
          }
        },
        {
          data: "projectName",
          orderable: false,
          render: function (params) {
            return '<span data-authorizeId="' + params[1] + '">' + params[0] + '</span>';
          }
        },
        {
          data: "cities", orderable: false, render: function (args) {
            return '<div class="w-small"><span class="ellipsis" title="' + args + '">' + args + '</span></div>';
          }
        },
        {data: "mac", orderable: false},
        {data: "masterIp", orderable: false},
        {
          data: "feedback", orderable: false, render: function (params) {
            if (params === "0") {
              return '<span>已反馈</span>';
            } else {
              return '<span >未反馈</span>';
            }
          }
        },
        {
          data: "envirNote", orderable: false, render: function (params) {
            if (params === "1") {
              return '<span>研发测试环境</span>';
            } else if (params === "3") {
              return '<span>已停用</span>';
            } else {
              return '<span >线上生产环境</span>';
            }
          }
        },
        {data: "downloadTime", orderable: true},
        {
          data: "phone", orderable: false, render: function (params) {
            return "<span>" + params.replace(/^(.{3})(.{3})(.{5})$/g, '$1-$2-$3') + "</span>";
          }
        },
        {data: "envirHead", orderable: false},
        {
          data: "note", orderable: false, render: function (args) {
            return '<div class="w-small"><span class="ellipsis" title="' + args + '">' + args + '</span></div>';
          }
        },
        {
          data: "snFile", orderable: false, render: function (args) {
            return '<div class="w-small"><span class="ellipsis" title="' + args + '">' + args + '</span></div>';
          }
        },
        {
          data: "operation", orderable: false, render: function (args) {
            return '<div class="operation" data-authorizeId=' + args[0] + ' data-authorize="' + args + '">' +
              '<a type="button" class="btn btn-link" data-action="editAuthorize">编辑</a>' +
              '<a type="button" class="btn btn-link" data-action="delAuthorize">删除</a></div>';
          }
        },
        {data: "uuid", visible: false}
      ];

      var totalRows = 0,
        rowStart = 0,
        rowEnd = 0;
      var ajaxFun = function (params, callback) {
        var condition = $.extend(true, {}, authorizeModel.authorizeListParams);
        condition.pageNo = (params.start / params.length) + 1 || 1;
        condition.pageSize = params.length;
        condition.sortField = params.order[0].dir;
        authorizeModel.getAuthorizeList(condition).then(function (res) {
          var obj = {
            data: []
          };
          if (tool.checkStatusCode(res.code)) {
            var total = res.page.totalRows,
              pageData = res.data || [],
              dataTemp = [];

            totalRows = total;
            rowStart = res.page.rowStart + 1;
            rowEnd = res.page.rowEnd;
            if (total > dataApp.tableTotal) {
              total = dataApp.tableTotal;
              publicApp._toastDialog("数据量过大，只显示前30000条", {"intent": "warning", "position": "top_center"});
            }
            pageData.map(function (item) {
              var itemObj = {};
              itemObj['uuid'] = item.uuid;
              itemObj['projectName'] = item.projectName === "" ? ["--", ""] : [item.projectName, item.uuid];
              itemObj['envirHead'] = item.envirHead === "" ? "--" : item.envirHead;
              itemObj['phone'] = item.phone === "" || item.phone == null ? "--" : item.phone;
              itemObj['cities'] = item.cities === "" ? "--" : item.cities;
              itemObj['downloadTime'] = item.downloadTime === "" ? "--" : item.downloadTime;
              itemObj['mac'] = item.mac === "" ? "--" : item.mac;
              itemObj['masterIp'] = item.masterIp === "" ? "--" : item.masterIp;
              itemObj['envirNote'] = item.envirNote === "" ? "--" : item.envirNote;
              itemObj['snFile'] = item.snFile === "" ? "--" : item.snFile;
              itemObj['feedback'] = item.feedback === "" ? "--" : item.feedback;
              itemObj['note'] = item.note === "" ? "--" : item.note;
              itemObj['operation'] = [item.uuid, item.projectName, item.envirHead, item.phone, item.cities, item.downloadTime, item.mac, item.masterIp, item.envirNote, item.snFile, item.feedback, item.note];
              dataTemp.push(itemObj);
              return dataTemp;
            });
            obj.data = dataTemp;
            obj.draw = params.draw;//draw:请求次数
            obj.recordsTotal = total;//recordsTotal:总记录数
            obj.recordsFiltered = total;//recordsFiltered:过滤后的总记录数
          } else {
            publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
          }
          callback && callback(obj);
        });
      };
      var options = {
        destroy: true,
        serverSide: true,
        ordering: true,
        orderClasses: false,
        scrollY: '570px',
        columns: columns,
        ajax: ajaxFun,
        columnDefs: [{
          orderable: false,
          className: 'select-checkbox',
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
        order: [[7, 'desc']],
        drawCallback: function () {
          $('#authorizeTable_info').html("共 " + totalRows + " 条记录，当前显示第 " + rowStart + " - " + rowEnd + " 条");
        },
        scrollX: true,
        fixedColumns: {
          leftColumns: 2,
          rightColumns: 2
        }
      };
      table = publicApp.initTable($id, options);
    },
    /**
     * 新增、编辑授权弹窗
     */
    operationAuthorizeDialog: function (authorize) {
      var self = this;
      Dialog.open({
        id: "operationAuthorizeForm",
        title: tool.isObject(authorize) ? "编辑授权" : "新增授权",
        width: 600,
        height: 'auto',
        modal: true,
        content: authorizeTpl(authorize),
        button: [
          {
            id: "operationSure",
            label: "确定",
            intent: "primary",
            focus: true,
            click: function () {
              self.callFormValid();
              return self.submitAuthorizeForm(authorize ? authorize.uuid : "");
            }
          },
          {
            id: "operationCancel",
            label: "取消", click: function () {
              $("table tr").removeClass("selected");
            }
          }
        ],
        onShow: function () {
          self.validateForm();
        }
      });
    },
    initValidate: function () {
      $.validator.addMethod("isAccount", function (value) {
        // var account = /^(((13[0-9]{1})|(14[5-9]{1})|(17[0-8]{1})|(15[0-3]{1})|(15[5-9]{1})|(166)|(18[0-9]{1})|(19[7-9]{1}))+\d{8})$/;
        var account = /^[0-9]{11}$/;
        return account.test(value);
      }, "请输入正确的手机号码");
      // $.validator.addMethod("isProvinces", function () {
      //   var city =$("#provinces").cascader('getChoosedData');
      //   var provice = $("#provinces").cascader('getParentsForId',city);
      //   return Boolean(provice);
      // }, "未选择项目安装地市");
    },
    /**
     * 初始化表单验证
     */
    validateForm: function () {
      $("#operationAuthorizeForm").validate({
        debug: true,//只验证不提交表单
        rules: {
          tel: {
            required: true,
            minlength: 11,
            maxlength: 11,
            isAccount: ""
          }
          // ,provinces: {
          //   required: true,
          //   isProvinces: ""
          // }
        },
        messages: {
          tel: {
            required: "请输入手机号码",
            minlength: "手机号码少于11位",
            maxlength: "手机号码超过11位",
            isAccount: "存在非数字字符"
          }
          // ,provinces: {
          //   required: "请选择项目安装地市",
          //   isProvinces: "请选择项目安装地市"
          // }
        }
      });
    },
    /**
     * 调用表单验证
     */
    callFormValid: function () {
      $("#operationAuthorizeForm").valid();
    },
    /**
     * 判断表单是否还有错误
     */
    isFormError: function () {
      return $('#operationAuthorizeForm').find('.form-group.has-error').length > 0;
    },
    /**
     * 提交表单
     */
    submitAuthorizeForm: function (itemId) {
      var self = this;
      var $dom =$("#provinces");
      var city =$dom.cascader('getChoosedData');
      var provice = $dom.cascader('getParentsForId',city);
      if(provice === "" || provice === null || provice === undefined) {
        $dom.parent(".form-group").addClass("has-error has-icon");
        $dom.find(".cascader-list").addClass("form-control").css("line-height", "24px");
        $dom.append("<i class=\"aidicon aidicon-alert-circle\"></i>");
        // publicApp._toastDialog("请选择项目安装地市", {"intent": "danger", "position": "top_center"});
      }
      if (self.isFormError()) {
        return false;
      }
      var condition = {}, dtd;
      // var city =$("#provinces").cascader('getChoosedData');
      // var provice = $("#provinces").cascader('getParentsForId',city);
      condition.projectName = $.trim($("#name").val());
      condition.cities = $.trim(city[0]);
      condition.downloadTime = $.trim($("#downloadTime").val());
      condition.envirHead = $.trim($("#officer").val());
      condition.phone = $.trim($("#tel").val());
      condition.mac = $.trim($("#mac").val());
      condition.masterIp = $.trim($("#ip").val());
      condition.envirNote = $.trim($("#environmentInfo").val());
      condition.snFile = $.trim($("#snFile").val());
      condition.feedback = $.trim($("#feedback").val());
      condition.note = $.trim($("#remark").val());
      var val = provice !== null && provice !== undefined ? provice[0].value : "";
      condition.provinces = $.trim(val);

      if (itemId) {
        condition.uuid = itemId;
        dtd = authorizeModel.editAuthorize(condition);
      } else {
        dtd = authorizeModel.saveAuthorize(condition);
      }
      $.when(dtd).then(function (res) {
        if (tool.checkStatusCode(res.code)) {
          publicApp.updateTable($('#authorizeTable'));
          publicApp._toastDialog(res.msg, {"intent": "success", "position": "top_center"});
          self.addHighlight(res);
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
          $("[data-authorizeId=" + res.data + "]").parents("tr").addClass("add-active");
          def.resolve();
        }, 500);
        return def;
      };
      $.when(wait($def)).done(function () {
        setTimeout(function () {
          $("[data-authorizeId=" + res.data + "]").parents("tr").removeClass("add-active");
        }, 3000);
      });
    },
    //删除权限列表数据弹窗
    delAuthorizeDialog: function (itemIds, val) {
      var self = this;
      Dialog.open({
        id: "delDialog",
        title: val > 1 ? "<span class='aidicon aidicon-alert-circle-outline aidicon-warning'></span><span>确定要删除这" + val + "个用户吗？</span>" : "<span class='aidicon aidicon-alert-circle-outline aidicon-warning'></span><span>确定要删除该用户吗？</span>",
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
              self.delAuthorizes(itemIds);
              self.clearAllChecked();
              publicApp.toggleDisabled(".toggle-disabled", -1);
            }
          },
          {
            id: "delCancel",
            label: "取消"
          }
        ]
      });
    },
    //删除权限列表
    delAuthorizes: function (itemIds) {
      authorizeModel.delAuthorizes(itemIds).then(function (res) {
        if (tool.checkStatusCode(res.code)) {
          publicApp._toastDialog(res.msg, {"intent": "success", "position": "top_center"});
          publicApp.updateTable($('#authorizeTable'));
        } else {
          publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
        }
      });
    },
    //获取表格多选框选中的值     
    getCheckboxData: function () {
      var selectedRows = table.rows('.selected').data(),
        len = selectedRows.length,
        ids = '',
        arr = [];
      for (var i = 0; i < len; i++) {
        ids += selectedRows[i].uuid;
        ids += ',';
      }
      arr.push(ids.substring(0, ids.length - 1));
      arr.push(len);
      return arr;
    },
    //初始化弹窗内日期
    initCalendar: function () {
      laydate.render({
        elem: '#downloadTime',
        eventElem: '.calendar-icon',
        showBottom: false,
        trigger: 'click'
      });
    },
    //初始化弹窗的下拉
    initDialogSelect: function (envValue, feedValue, province) {
      var self = this;
      authorizeModel.getCity().then(function (geo) {
        var city = self.getCity(geo);
        var options = {
          type: 'list',
          inputValue: province,
          data: city
        };
        $('#provinces').cascader(options);
        $(".cascader").css("width",$("#name").width()+26);
        $('#provinces').cascader(options).on('item.chosen.bs.cascader', function () {
          var $dom = $("#provinces");
          var chooseCity =$dom.cascader('getChoosedData');
          var provice = $dom.cascader('getParentsForId',chooseCity);
          if(provice !== "" && provice !== null && provice !== undefined) {
            $dom.parent(".form-group").removeClass("has-error has-icon");
            $dom.find(".cascader-list").removeClass("form-control");
            $("#provinces i").remove(".aidicon-alert-circle");
          }
        });
      });

      var environmentInfoList = [{id: "0", text: '线上生产环境'}, {id: "1", text: '研发测试环境'}, {id: "3", text: '已停用'}];
      $('#environmentInfo').select2({
        placeholder: "请选择",
        width: $("#name").width() + 26,
        dropdownCssClass: "selectDrop",
        minimumResultsForSearch: -1,
        data: environmentInfoList
      });
      var initEnvironmentInfoId = (envValue === "" ? "0" : envValue);
      $("#environmentInfo").select2('val', initEnvironmentInfoId);

      var feedbackList = [{id: "0", text: '已反馈'}, {id: "1", text: '未反馈'}];
      $('#feedback').select2({
        placeholder: "请选择",
        width: $("#name").width() + 26,
        dropdownCssClass: "selectDrop",
        minimumResultsForSearch: -1,
        data: feedbackList
      });
      var feedbackId = (feedValue === "" ? "0" : feedValue);
      $("#feedback").select2('val', feedbackId);
    },
//将city.json内数据转换格式
    getCity: function (geo) {
      var self = this;
      var city = [];

      for (var item in geo) {
        var obj = {};
        obj.id = item;
        obj.value = item;
        obj.label = item;
        if (Object.prototype.toString.call(geo) === '[object Array]') {
          return [];
        } else {
          obj.children = typeof (geo[item]) === 'object' ? self.getCity(geo[item]) : [];
        }
        city.push(obj);
      }
      return city;
    },
    /**
     * 初始化头部下拉框
     */
    initFilter: function () {
      var self = this;
      filterApp.init(function () {
        $("#authorizationFilter").html(filterApp.getFilterDom({module: "authorization", placeholder: "请输入关键字"}));
        filterApp.initSelect();
        self.initCal();
      }, 'authorization');
    },
    setTableFilter: function (module) {
      var filterObj = filterApp.getFilter(dataApp.filterObj[module]);
      var searchObj = {};
      searchObj.keyWord = $.trim($("[data-module=" + module + "]").val() || "");
      searchObj.projectName = $.trim(filterObj.pjName || "");
      searchObj.cities = $.trim(filterObj.pjLocation || "");
      searchObj.envirNote = $.trim(filterObj.environment || "");
      searchObj.feedback = $.trim(filterObj.feedback || "");
      var timeArr = $.trim($("#downTimeTag").val()).replace(/\s+/g, "").split('~') || "";
      searchObj.startTime = timeArr[0] || "";
      searchObj.endTime = timeArr[1] || "";
      $.extend(true, authorizeModel.authorizeListParams, searchObj);
      publicApp.updateTable($('#authorizeTable'));
    },
    //获取下拉框多选时的值
    getMulSelectText: function (id) {
      var arr = [];
      $(id + " option:selected").each(function () {
        arr.push($(this).text());
      });
      return arr.join(",");
    },
    //筛选条件日期初始化
    initCal: function () {
      laydate.render({
        elem: '#downTimeTag',
        eventElem: '.cal-icon',
        range: "~",
        trigger: 'click'
      });
    },
    //清除authorize表全选按钮的选中样式
    clearAllChecked: function () {
      var $selectAll = $('#authorizeTable_wrapper [data-action="select-all-event"]').parent('th');
      $selectAll.hasClass("selected") ? $selectAll.removeClass('selected') : '';
      publicApp.toggleDisabled(".toggle-disabled", table.rows({selected: true}).count());
    },
    event: function () {
      var self = this;
      var authorizationSearch = '[data-action="authorizationSearch"]';
      $(document).off('click.authorizationSearch.authorize').on('click.authorizationSearch.authorize', authorizationSearch, function () {
        var module = $(this).closest(".form-validate").attr("id");
        self.setTableFilter(module);
        self.clearAllChecked();
      });

      var authorizationReset = '[data-action="authorizationReset"]';
      $(document).off('click.authorizationReset.authorize').on('click.authorizationReset.authorize', authorizationReset, function () {
        filterApp.resetFilter($(this));
        var module = $(this).closest(".form-validate").attr("id");
        self.setTableFilter(module);
      });

      //批量删除
      var delAuthorizes = '[data-action="delAuthorizes"]';
      $(document).off('click.delAuthorizes.authorize').on('click.delAuthorizes.authorize', delAuthorizes, function () {
        var arr = self.getCheckboxData();
        self.delAuthorizeDialog(arr[0], arr[1]);
      });

      var addAuthorize = '[data-action="addAuthorize"]';
      $(document).off('click.addAuthorize.authorize').on('click.addAuthorize.authorize', addAuthorize, function () {
        self.operationAuthorizeDialog();
        self.initCalendar();
        self.initDialogSelect("", "", "");
      });

      var emptyAdd = '[data-action="empty-add"]';
      $(document).off('click.emptyAdd.authorize').on('click.emptyAdd.authorize', emptyAdd, function () {
        $('[data-action="addAuthorize"]').click();
      });
      var editAuthorize = '[data-action="editAuthorize"]';
      $(document).off('click.editAuthorize.authorize').on('click.editAuthorize.authorize', editAuthorize, function () {
        var authorizeArr = $(this).closest(".operation").attr("data-authorize").split(",");
        var authorize = {
          uuid: authorizeArr[0],
          projectName: authorizeArr[1],
          envirHead: authorizeArr[2],
          phone: authorizeArr[3],
          cities: authorizeArr[4],
          downloadTime: authorizeArr[5],
          mac: authorizeArr[6],
          masterIp: authorizeArr[7],
          envirNote: authorizeArr[8],
          snFile: authorizeArr[9],
          feedback: authorizeArr[10],
          note: authorizeArr[11]
        };
        self.operationAuthorizeDialog(authorize);
        self.initCalendar();
        self.initDialogSelect(authorize.envirNote, authorize.feedback, authorize.cities);
      });

      //单个删除
      var delAuthorize = '[data-action="delAuthorize"]';
      $(document).off('click.delAuthorize.authorize').on('click.delAuthorize.authorize', delAuthorize, function () {
        var authorize = $(this).closest(".operation").attr("data-authorize").split(",");
        var itemId = authorize[0];
        self.delAuthorizeDialog(itemId, 1);
      });

      //导出EXCEL
      var exportExcel = '[data-action="exportExcel"]';
      $(document).off('click.exportExcel.authorize').on('click.exportExcel.authorize', exportExcel, function () {
        var module = "authorization";
        var filterObj = filterApp.getFilter(dataApp.filterObj[module]);
        var searchObj = {};
        searchObj.keyWord = $.trim($("[data-module=" + module + "]").val() || "");
        searchObj.projectName = $.trim(filterObj.pjName || "");
        searchObj.cities = $.trim(filterObj.pjLocation || "");
        searchObj.envirNote = $.trim(filterObj.environment || "");
        searchObj.feedback = $.trim(filterObj.feedback || "");
        var timeArr = $.trim($("#downTimeTag").val()).replace(/\s+/g, "").split('~') || "";
        searchObj.startTime = timeArr[0] || "";
        searchObj.endTime = timeArr[1] || "";
        var condition = "keyWord="+searchObj.keyWord+"&projectName="+searchObj.projectName+"&cities="+searchObj.cities+"&envirNote="+searchObj.envirNote+"&feedback="+searchObj.feedback+"&startTime="+searchObj.startTime+"&endTime="+searchObj.endTime;
        $("#exportExcel").attr("href", dataApp.urlDomain + "/export/export?"+condition);
      });

      /*全选按钮*/
      var exploringSelectAllEvent = '#authorizeTable_wrapper [data-action="select-all-event"]';
      $(document).off('click.exploringSelectAllEvent.authorize').on('click.exploringSelectAllEvent.authorize', exploringSelectAllEvent, function () {
        var $selectAll = $(".dataTables_scroll #select-checkbox").parent('th');
        publicApp.tableAllSelect(table, $selectAll);
        publicApp.toggleDisabled(".toggle-disabled", table.rows({selected: true}).count());
      });

      /*表格选择按钮*/
      var selectCheckbox = 'td.select-checkbox';
      $(document).off('click.selectCheckbox.authorize').on('click.selectCheckbox.authorize', selectCheckbox, function () {
        var $selectAll = $(exploringSelectAllEvent).parent('th');
        publicApp.isTableAllSelect(table, $selectAll);
        publicApp.toggleDisabled(".toggle-disabled", table.rows({selected: true}).count());
      });

      /*表格分页点击事件*/
      var paginateButton = '.paginate_button';
      $(document).off('click.paginateButton.authorize').on('click.paginateButton.authorize', paginateButton, function () {
        if (table.page() !== self.page) {
          var $selectAll = $(exploringSelectAllEvent).parent('th');
          publicApp.isTableAllSelect(table, $selectAll);
          self.clearAllChecked();
          publicApp.toggleDisabled(".toggle-disabled", -1);
        }
        self.page = table.page();
      });
    }
  };
  return app;
});