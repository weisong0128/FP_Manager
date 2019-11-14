define([
  'model',
  'data',
  'tool',
  'public',
  'header',
  'user/userModel',
  'viewTpl/user/userTpl',
  'filter'
], function (model, dataApp, tool, publicApp, headerApp, userModel, userTpl, filterApp) {
  var table = null;
  var app = {
    props: {
      index: 5
    },
    page: 0,
    init: function () {
      headerApp.init(this.props);
      this.initUserTable();
      this.event();
      this.initValidate();
      this.initFilter();
    },
    initUserTable: function () {
      var $id = $("#userTable");
      var columns = [
        {
          render: function () {
            return '';
          }
        },
        {data: "userName", orderable: false},
        {
          data: "account", orderable: false, render: function (params) {
            return "<span>" + params.replace(/^(.{3})(.{3})(.{5})$/g, '$1-$2-$3') + "</span>";
          }
        },
        {
          data: "password", orderable: false, render: function (params) {
            var temp = params.substr(1, params.length).replace(/./g, "*");
            return "<span>" + params.substr(0, 1) + temp + params.substr(params.length - 1, params.length) + "</span>";
          }
        },
        {
          data: "role", orderable: false, render: function (params) {
            var roleName = "";
            if (params === "0") {
              roleName = "管理员";
            } else if (params === "1") {
              roleName = "运维";
            } else {
              roleName = "普通用户";
            }
            return "<span>" + roleName + "</span>";
          }
        },
        {
          data: "status", orderable: false, render: function (params) {
            var isChecked = Number(params[0]) === 0 ? "checked" : "";
            return '<span class="btn-switch status-operation" data-userId="' + params[1] + '" data-status="' + params[0] + '">' +
              '<label>' +
              '<input type="checkbox" name="checkbox" aria-describedby="toggle" value="' + params[0] + '" ' + isChecked + '>' +
              '<span class="btn-switch-inner" data-action="changeUserStatus">' +
              '<span class="btn-switch-close">停用</span>' +
              '<span class="btn-switch-open">启用</span>' +
              '</span>' +
              '</label>' +
              '</span>';
          }
        },
        {
          data: "operation", orderable: false, render: function (args) {
            var isClick = args[5] === 0 ? " style='display:none;'" : '';
            return '<div class="operation" data-user="' + args + '">' +
              '<a type="button" class="btn btn-link" data-action="editUser">编辑</a>' +
              '<a type="button" class="btn btn-link user-disable-link" data-action="delUser"' + isClick + '>删除</a></div>';
          }
        },
        {data: "userId", visible: false}
      ];
      var totalRows = 0,
        rowStart = 0,
        rowEnd = 0;
      var ajaxFun = function (params, callback) {
        var condition = $.extend(true, {}, userModel.userListParams);
        condition.pageNo = (params.start / params.length) + 1 || 1;
        condition.pageSize = params.length;

        userModel.getUserList(condition).then(function (res) {
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
            // 如有需要，可对表格数据进行封装（下面对返回的字段进行了筛选）
            pageData.map(function (user) {
              var userObj = {};
              userObj['userId'] = user.uuid;
              userObj['account'] = user.userId === "" || user.userId == null ? "--" : user.userId;
              userObj['userName'] = user.userName === "" ? "--" : user.userName;
              userObj['password'] = user.userPassword === "" ? "--" : user.userPassword;
              userObj['role'] = user.userRole === "" ? "--" : user.userRole;
              userObj['status'] = [user.userState, user.uuid];
              userObj['operation'] = [user.uuid, user.userId, user.userName, user.userPassword, user.userRole, user.userState];
              dataTemp.push(userObj);
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
        ordering: false,
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
          $('#userTable_info').html("共 " + totalRows + " 条记录，当前显示第 " + rowStart + " - " + rowEnd + " 条");
        }
      };
      table = publicApp.initTable($id, options);
    },
    /**
     * 新增、编辑用户弹窗
     */
    operationUserDialog: function (user) {
      var self = this,
        userId = user ? user.userId : "",
        state = user ? user.state : 0,
        userName = user ? user.userName : "";
      Dialog.open({
        id: "operationUserForm",
        title: tool.isObject(user) ? "编辑用户" : "新增用户",
        width: 500,
        height: 'auto',
        modal: true,
        content: userTpl(user),
        button: [
          {
            id: "operationSure",
            label: "确定",
            intent: "primary",
            focus: true,
            click: function () {
              self.callFormValid();
              return self.submitUserForm(userId, state, userName);
            }
          },
          {
            id: "operationCancel",
            label: "取消"
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
      }, "请输入正确的账号，账号为手机号码");

      $.validator.addMethod("isUserName", function (value) {
        var userName = /^[0-9a-zA-Z_]{3,16}$/;
        return userName.test(value);
      }, "用户名为3位及以上，支持字母、数字、下划线");

      $.validator.addMethod("isPassword", function (value) {
        var account = /^[0-9a-zA-Z_]{6,16}$/;
        return account.test(value);
      }, "请输入6-16位密码，支持字母、数字、下划线");

      $.validator.addMethod("samePassword", function () {
        var password = $('#password').val();
        var confirmPassword = $('#confirmPassword').val();
        if (!confirmPassword && !password) {
          return true;
        } else {
          return confirmPassword === password;
        }
      }, "两次填写的密码不一样");
    },
    /**
     * 初始化表单验证
     */
    validateForm: function () {
      $("#operationUserForm").validate({
        debug: true,//只验证不提交表单
        rules: {
          account: {
            required: true,
            minlength: 11,
            maxlength: 11,
            isAccount: ""
          },
          userName: {
            required: true,
            minlength: 3,
            maxlength: 16,
            isUserName: ""
          },
          password: {
            required: true,
            minlength: 6,
            maxlength: 16,
            isPassword: ""
          },
          confirmPassword: {
            required: true,
            minlength: 6,
            maxlength: 16,
            samePassword: ""
          }
        },
        messages: {
          account: {
            required: "请输入账号",
            minlength: "手机号码少于11位",
            maxlength: "手机号码超过11位",
            isAccount: "存在非数字字符"
          },
          userName: {
            required: "请输入用户名",
            minlength: "用户名不能少于3个字符",
            maxlength: "用户名不能超过16个字符"
          },
          password: {
            required: "请输入密码",
            minlength: "密码不能少于6个字符",
            maxlength: "密码不能超过16个字符"
          },
          confirmPassword: {
            required: "请输入确认密码",
            minlength: "密码不能少于6个字符",
            maxlength: "密码不能超过16个字符"
          }
        }
      });
    },
    /**
     * 调用表单验证
     */
    callFormValid: function () {
      $("#operationUserForm").valid();
    },
    /**
     * 判断表单是否还有错误
     */
    isFormError: function () {
      return $('#operationUserForm').find('.form-group.has-error').length > 0;
    },
    /**
     * 提交表单
     */

    submitUserForm: function (uuid, state, preUserName) {
      var self = this;
      if (this.isFormError()) {
        return false;
      }
      var condition = {}, dtd;
      condition.userId = $.trim($("#account").val());
      condition.userName = $.trim($("#userName").val());
      condition.userRole = $.trim($("#role").val());
      condition.userPassword = $.trim($("#password").val());
      condition.userState = state;

      if (uuid) {
        condition.uuid = uuid;
        if (preUserName !== null || preUserName !== undefined) {
          if (preUserName === condition.userName) {
            condition.isEdit = false;
          } else {
            condition.isEdit = true;
          }
        }
        dtd = userModel.updateUser(condition);
      } else {
        dtd = userModel.addUser(condition);
      }

      $.when(dtd).then(function (res) {
        if (tool.checkStatusCode(res.code)) {
          publicApp.updateTable($('#userTable'));
          publicApp._toastDialog(res.msg, {"intent": "success", "position": "top_center"});
          self.addHighlight(res);
          self.clearAllChecked(-1);
        } else {
          publicApp._toastDialog(res.data, {"intent": "danger", "position": "top_center"});
        }
      });
    },
    //新增高亮
    addHighlight: function (res) {
      var $def = $.Deferred();
      var wait = function (def) {
        setTimeout(function () {
          $("[data-userId=" + res.data + "]").parents("tr").addClass("add-active");
          def.resolve();
        }, 500);
        return def;
      };
      $.when(wait($def)).done(function () {
        setTimeout(function () {
          $("[data-userId=" + res.data + "]").parents("tr").removeClass("add-active");
        }, 3000);
      });
    },
    //启用、停用、删除账号弹窗
    changeAccountDialog: function (data, val, status) {
      var self = this;
      Dialog.open({
        id: "changeAccountDialog",
        title: val > 1 ? "<span class='aidicon aidicon-alert-circle-outline aidicon-warning'></span><span>确定要" + status + "这" + val + "个用户吗？</span>" : "<span class='aidicon aidicon-alert-circle-outline aidicon-warning'></span><span>确定要" + status + "该用户吗？</span>",
        width: 420,
        height: 'auto',
        modal: true,
        content: '',
        button: [
          {
            id: "changeAccountSure",
            label: "确定",
            intent: "primary",
            focus: true,
            click: function () {
              self.changeAccount(data);
            }
          },
          {
            id: "changeAccountCancel",
            label: "取消",
            click: function () {
              if (val === 1 && status !== "删除") {
                var uuid = data.uuids;
                var $dom = $("[data-userId = '" + uuid + "'] input");
                if ($dom.val() === "0") {
                  $dom.prop("checked", true);
                } else {
                  $dom.prop("checked", false);
                }
              }
            }
          }
        ]
      });
    },
    //启用、停用、删除账号（0：启用，1：禁用，2：删除）
    changeAccount: function (data) {
      var self = this;
      userModel.changeAccountStatus(data).then(function (res) {
        if (tool.checkStatusCode(res.code)) {
          publicApp._toastDialog(res.msg);
          publicApp.updateTable($('#userTable'));
          self.clearAllChecked(-1);
        } else {
          publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
        }
      });
    },
    //批量启用、禁用、删除账号弹出框
    callStatusDialog: function (type, state) {
      var self = this,
        obj = {},
        arr = self.getCheckboxData();
      obj.uuids = arr[0];
      obj.state = state;
      self.changeAccountDialog(obj, arr[1], type);
    },
    //获取表格多选框选中的值     
    getCheckboxData: function () {
      var selectedRows = table.rows('.selected').data(),
        len = selectedRows.length,
        ids = '',
        arr = [];
      for (var i = 0; i < len; i++) {
        ids += selectedRows[i].userId;
        ids += ',';
      }
      arr.push(ids.substring(0, ids.length - 1));
      arr.push(len);
      return arr;
    },
    //初始化弹窗的下拉框
    initDialogSelect: function (value) {
      userModel.getAllRole().then(function (res) {
        if (tool.checkStatusCode(res.code)) {
          var arr = [];
          var initRoleId = "";
          res.data.map(function (role) {
            var obj = {};
            if (role.role === "普通用户") {
              initRoleId = role.roleId;
            }
            obj.id = role.roleId;
            obj.text = role.role;
            arr.push(obj);
          });
          $('#role').select2({
            placeholder: "请选择角色",
            width: $("#account").width() + 26,
            dropdownCssClass: "selectDrop",
            minimumResultsForSearch: -1,
            data: arr
          });
          initRoleId = value === "" ? initRoleId : value;
          $("#role").select2('val', initRoleId);
        }
      });
    },
    // 初始化筛选条件
    initFilter: function () {
      filterApp.init(function () {
        $("#userFilter").html(filterApp.getFilterDom({module: "user", placeholder: "请输入用户名、账号"}));
        filterApp.initSelect();
      });
    },
    setTableFilter: function (module) {
      var filterObj = filterApp.getFilter(dataApp.filterObj[module]);
      var searchObj = {};
      searchObj.keyWord = $.trim($("[data-module=" + module + "]").val() || "");
      searchObj.userRole = $.trim(filterObj.role || "");
      searchObj.userState = $.trim(filterObj.state || "");

      $.extend(true, userModel.userListParams, searchObj);
      publicApp.updateTable($('#userTable'));
    },
    //清除user表全选按钮的选中样式
    clearAllChecked: function (value) {
      var $selectAll = $('#userTable_wrapper [data-action="select-all-event"]').parent('th');
      $selectAll.hasClass("selected") ? $selectAll.removeClass('selected') : '';
      publicApp.toggleDisabled(".toggle-disabled", value);
    },
    event: function () {
      var self = this;
      var userSearch = '[data-action="userSearch"]';
      $(document).off('click.userSearch.user').on('click.userSearch.user', userSearch, function () {
        var module = $(this).closest(".form-validate").attr("id");
        self.setTableFilter(module);
        self.clearAllChecked(-1);
      });

      var userReset = '[data-action="userReset"]';
      $(document).off('click.userReset.user').on('click.userReset.user', userReset, function () {
        filterApp.resetFilter($(this));
        var module = $(this).closest(".form-validate").attr("id");
        self.setTableFilter(module);
        self.clearAllChecked(-1);
      });

      //多个启用
      var enableUsers = '[data-action="enableUsers"]';
      $(document).off('click.enableUsers.user').on('click.enableUsers.user', enableUsers, function () {
        self.callStatusDialog("启用", 0);
      });

      //多个停用
      var stopUsers = '[data-action="stopUsers"]';
      $(document).off('click.stopUsers.user').on('click.stopUsers.user', stopUsers, function () {
        self.callStatusDialog("停用", 1);
      });

      //表格内启用停用
      var changeUserStatus = '[data-action="changeUserStatus"]';
      $(document).off('click.changeUserStatus.user').on('click.changeUserStatus.user', changeUserStatus, function () {
        var userId = $(this).closest(".status-operation").attr("data-userId");
        var userStatus = $(this).closest(".status-operation").attr("data-status");
        var obj = {};
        obj.uuids = userId;
        obj.state = userStatus === "1" ? 0 : 1;
        self.changeAccountDialog(obj, 1, userStatus === "1" ? "启用" : "停用");
      });

      var addUser = '[data-action="addUser"]';
      $(document).off('click.addUser.user').on('click.addUser.user', addUser, function () {
        self.operationUserDialog();
        self.initDialogSelect("");
      });

      var emptyAdd = '[data-action="empty-add"]';
      $(document).off('click.emptyAdd.authorize').on('click.emptyAdd.authorize', emptyAdd, function () {
        $('[data-action="addUser"]').click();
      });

      var editUser = '[data-action="editUser"]';
      $(document).off('click.editUser.user').on('click.editUser.user', editUser, function () {
        var userArr = $(this).closest(".operation").attr("data-user").split(",");
        var user = {
          userId: userArr[0],
          account: userArr[1],
          userName: userArr[2],
          password: userArr[3],
          role: userArr[4],
          state: userArr[5]
        };
        self.operationUserDialog(user);
        self.initDialogSelect(userArr[4]);
      });

      //单个删除
      var delUser = '[data-action="delUser"]';
      $(document).off('click.delUser.user').on('click.delUser.user', delUser, function () {
        var user = $(this).closest(".operation").attr("data-user").split(",");
        var userId = user[0];
        var obj = {};
        obj.uuids = userId;
        obj.state = 2;
        self.changeAccountDialog(obj, 1, "删除");
      });

      //批量删除
      var delUsers = '[data-action="delUsers"]';
      $(document).off('click.delUsers.user').on('click.delUsers.user', delUsers, function () {
        self.callStatusDialog("删除", 2);
      });

      /*全选按钮*/
      var exploringSelectAllEvent = '#userTable_wrapper [data-action="select-all-event"]';
      $(document).off('click.exploringSelectAllEvent.user').on('click.exploringSelectAllEvent.user', exploringSelectAllEvent, function () {
        var $selectAll = $(this).parent('th');
        publicApp.tableAllSelect(table, $selectAll);
        publicApp.toggleDisabled(".toggle-disabled", table.rows({selected: true}).count());
      });

      /*表格选择按钮*/
      var selectCheckbox = '#userTable td.select-checkbox';
      $(document).off('click.selectCheckbox.user').on('click.selectCheckbox.user', selectCheckbox, function () {
        var $selectAll = $(exploringSelectAllEvent).parent('th');
        publicApp.isTableAllSelect(table, $selectAll);
        publicApp.toggleDisabled(".toggle-disabled", table.rows({selected: true}).count());
      });

      /*表格分页点击事件*/
      var paginateButton = '.paginate_button';
      $(document).off('click.paginateButton.user').on('click.paginateButton.user', paginateButton, function () {
        if (table.page() !== self.page) {
          var $selectAll = $(exploringSelectAllEvent).parent('th');
          publicApp.isTableAllSelect(table, $selectAll);
          self.clearAllChecked(-1);
        }
        self.page = table.page();
      });
    }
  };
  return app;
});