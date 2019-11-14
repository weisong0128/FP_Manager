define(['model', 'data', 'tool'], function (model, dataApp, tool) {

  var publicApp = {
    tempData: {
      isInitDateTime: true
    },


    /**
     * 设置 Ajax 全局参数
     *
     * **/
    initAjaxSetup: function () {
      var self = this;
      return $.ajaxSetup({
        error: function (xhr) {
          var msg = xhr.responseText + ',' + xhr.statusText;
          self._toastDialog(msg, {"intent": "danger", "position": "top_center"});
        }
      });
    },

    /**
     * 格式化页面方法
     * @param page: String 页面标志，callback: Function 回调
     *
     * **/
    initPageFn: function (page, callback) {
      if (dataApp.pageFn[page]) {
        callback && callback();
      } else {
        require(['../avop/assets/js/index/' + page + '.js'], function (app) {
          dataApp.pageFn[page] = app;
          callback && callback();
        });
      }
    },

    /**
     * 初始化头部dom
     * @param dom:
     *
     * **/
    initHeaderTpl: function (dom) {
      $('#header').html(dom);
    },

    /**
     * 初始化内容dom
     * @param dom:
     *
     * **/
    initWrapperTpl: function (dom) {
      $('#wrapper').html(dom);
    },

    /**
     * 初始化头部模板文件
     * @param page: String.
     *       'home':主页面、
     *       'machine':机器信息、
     *       'statistics':数据统计、
     *       'tools':测试工具、
     *       'log':异常日志
     *
     * **/
    initHeader: function (page) {

      var app = dataApp.pageFn[page];

      app.initHeader(page);
    },

    /**
     * 初始化内容模板文件
     * @param page: String.
     *       'home':主页面、
     *       'machine':机器信息、
     *       'statistics':数据统计、
     *       'tools':测试工具、
     *       'log':异常日志
     *
     * **/
    initWrapper: function (page) {

      var app = dataApp.pageFn[page];
      app.setWrapperClass(page);
      app.initWrapper(page);
    },

    /**
     * 初始化日期时间范围
     **/
    initDateTime: function (opt) {
      var option = {
        type: 'datetime',
        range: true,
        format: 'yyyy-MM-dd HH:mm:ss',
        ready: function () {
          if (opt.isInitDateTime) {
            $(".layui-this").eq(0).trigger("click");
            $(".layui-this").eq(0).trigger("click");
            opt.isInitDateTime = false;
          }
        }
      };

      var options = $.extend(true, option, opt);

      laydate.render(options);

      $(opt.elem).focus().blur();
    },

    /**
     * 获取事件区间
     *
     * **/
    getTimeRange: function (day, fmt) {
      var date = tool.setDateTime(dataApp.dateType[day], fmt ? fmt : "yyyy-MM-dd hh:mm");
      var endDate = tool.formatDateTime(fmt ? fmt : "yyyy-MM-dd hh:mm");
      return date + ' - ' + endDate;
    },

    /**
     * 初始化table
     * @params
     * opt：Object，表格的option
     *
     * **/
    initTable: function ($id, opt) {
      var option = {
        // 当表格在处理的时候（比如排序操作）是否显示“处理中...”
        processing: true,
        // // 是否开启服务器模式
        serverSide: true,
        // 定义在render时是否仅仅render显示的dom
        deferRender: true,
        // // 全局控制列表的翻页功能
        // paging: true,
        // 纵向滚动条
        scrollY: '400px',
        // 当显示更少记录时，是否允许表格减少高度
        scrollCollapse: true,
        // 关闭搜索功能
        searching: false,
        // columnDefs: [{
        //     orderable: false,
        //     targets: 0
        // }],
        language: {
          sEmptyTable: '暂无数据'
        },
        destroy: false
        // select: {
        //     // 可选择的配置有： 'api'、'single'、'multi'、'os'、'multi+shift'
        //     style: 'multi',
        //     // 控制是否在左下角显示选中信息
        //     info: false,
        //     selector: 'td:first-child'
        // },
        // order: [[1, 'asc']],
        // columns: params.columns,
        // ajax: params.ajaxFun
      };

      var options = $.extend(true, option, opt);
      var table = $id.DataTable(options);

      return table;
    },

    /**
     * 更新数据
     * @params
     * params：{
     *  id: 表格容器id
     *  ajaxFun：fn
     * }
     * **/
    updateTable: function ($id) {
      $id.DataTable().ajax.reload();
    },

    /**
     * 初始化下拉框
     *
     * **/
    initSelectGroups: function ($id, opt) {
      var option = {
        //  minimumResultsForSearch设置为-1，去除搜索框
        minimumResultsForSearch: -1
      };

      $.extend(true, option, opt);

      $id.select2(option);
    },

    /**
     * 激活的menu
     *
     * **/
    activeMenu: function (urlParam) {
      var menu = urlParam.path.split('/');
      var page = menu[1];
      var dom = '[data-page=' + page + ']';
      $(dom).closest('#menu').find('.menu').removeClass('active');
      $(dom).closest('.menu').addClass('active');
    },

    /**
     * 弹框提示
     * @param {String} msg 弹框内容
     * @param {Object} props 弹框配置
     */
    _toastDialog: function (msg, props) {
      if (this.toastDialog) {
        this.toastDialog.dismiss();
      }
      this.toastDialog = Dialog.toast(msg ? msg : '操作成功', {
        "intent": props ? props.intent : "success",
        "timeout": 2000
      });
    },

    /**
     * 是否全选
     * @params table：dataTables 表格对象，$allEle：jQuery 全选元素
     **/
    isTableAllSelect: function (table, $allEle) {
      if (table.rows({page: 'current'}).data().length !== 0 && (table.rows({page: 'current'}).data().length === table.rows({
        page: 'current',
        selected: true
      }).data().length)) {
        $allEle.addClass('selected');
      } else {
        $allEle.removeClass('selected');
      }
    },

    /**
     * 表格全选点击事件
     *  @params table：dataTables 表格对象，$allEle：jQuery 全选元素
     **/
    tableAllSelect: function (table, $allEle) {
      $allEle.toggleClass('selected');
      if ($allEle.hasClass('selected')) {
        $(table.rows({page: 'current'}).columns(0).nodes()[0]).each(function (a, b) {
          if (!$(b).hasClass("disable")) {
            table.rows(a).select();
          }
        });
      } else {
        $(table.rows({page: 'current'}).columns(0).nodes()[0]).each(function (a, b) {
          if (!$(b).hasClass("disable")) {
            table.rows(a).select(false);
          }
        });
      }
    },
    /**
     * 给按钮移除/添加disabled属性
     * @param {*} name 按钮id、class...
     * @param {*} value 大于0按钮激活，小于等于0按钮禁用
     */
    toggleDisabled: function (name, value) {
      if (value > 0) {
        $(name).removeAttr("disabled");
      } else if (value <= 0) {
        $(name).attr("disabled", "disabled");
      }
    },
    /**
     * 时间戳转换为yyyy-MM-dd hh:mm-ss格式
     * @param time
     * @returns {*|string}
     */
    timeFormat: function (time) {
      return tool.formatDateTime("yyyy-MM-dd hh:mm:ss", new Date(parseInt(time,10) * 1000));
    }
  };
  return publicApp;
});