define([
  'model',
  'data',
  'tool',
  'public',
  'viewTpl/header/index'
], function (model, dataApp, tool, publicApp, headerTpl) {
  var headerApp = {
    props: {},
    init: function (props) {
      this.props = props;
      this.render();
    },

    render: function () {
      var role = sessionStorage.getItem("role");
      var userName = sessionStorage.getItem("userName");
      if(role === null  || userName === null) {
        window.location.href = '../login/index.html';
      }
      var navList = this.assignItem(dataApp.pageObj, role);

      // var pageObj = $.extend(true, {}, dataApp.pageObj);
      var pageObj = $.extend(true, {}, navList);

      if (this.props.index < Object.keys(pageObj).length) {
        pageObj[this.props.index].active = true;
      }

      pageObj.userName = userName;
      $('#header').html(headerTpl(pageObj));
      this.events();
    },
    assignItem: function (navList, role) {
      var list = JSON.parse(JSON.stringify(navList));
      switch (role) {
        case "0":
          list = navList;
          break;
        case "1":
          list.splice(4, 2);
          break;
        default:
          list.splice(2, 4);
          break;
      }
      return list;
    },
    loginOut: function () {
      var condition = {};
      condition.uuid = sessionStorage.getItem("userId");
      model.quit(condition).then(function (res) {
        if (tool.checkStatusCode(res.code)) {
          sessionStorage.removeItem("userId");
          sessionStorage.removeItem("role");
          window.location.href = '../login/index.html';
        } else {
          publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
        }
      });
    },

    tipsDialog: function (index) {
      Dialog.open({
        id: "dialog-example5",
        title: "<span class='aidicon aidicon-alert-circle-outline aidicon-warning'></span>确定要离开该页面吗？",
        width: 420,
        height: 'auto',
        modal: true,
        content: '<div style="color:#878D99;font-size:14px;">您的建表任务还没完成，离开后任务将会重置</div>',
        button: [
          {
            id: "sure", label: "确认", intent: "primary", focus: true, click: function () {
              dataApp.isBuilding = false;
              window.location = dataApp.pageObj[index].path;
            }
          },
          {
            id: "cancel", label: "取消", click: function () {
              dataApp.isBuilding = true;
            }
          }
        ]
      });
    },
    events: function () {
      var self = this;

      var chooseMenuEvent = '[data-action="chooseMenuEvent"]';
      $(document).off('click.chooseMenuEvent.FP').on('click.chooseMenuEvent.FP', chooseMenuEvent, function () {

        var index = $(this).index();
        if (dataApp.isBuilding) {
          self.tipsDialog(index);
          return false;
        }

        window.location = dataApp.pageObj[index].path;
      });

      var loginOut = '[data-action="loginOut"]';
      $(document).off('click.loginOut.FP').on('click.loginOut.FP', loginOut, function () {
        self.loginOut();
      });
    }
  };
  return headerApp;
});