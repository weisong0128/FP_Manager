define([
  'model',
  'data',
  'tool',
  'public'
], function (model, dataApp, tool, publicApp) {

  var app = {
    props: {
      index: 1
    },
    init: function () {

      this.events();

    },

    login: function () {
      var self = this;
      if (self.isFormError()) {
        return false;
      }
    
      var condition = $("#loginForm").serialize();

      model.login(condition).then(function (res) {
        if (tool.checkStatusCode(res.code)) {
          window.location.href = '../home/index.html';
          sessionStorage.setItem("userId",res.data.uuid);
          sessionStorage.setItem("userName",res.data.userName);
          sessionStorage.setItem("role",res.data.userRole);
        } else {
          publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
        }
      });
    },
    /**
     * 调用表单验证
     */
    callFormValid: function () {
      $("#loginForm").valid();
    },
    /**
     * 判断表单是否还有错误
     */
    isFormError: function () {
      return $('#loginForm').find('.form-group.has-error').length > 0;
    },

    events: function () {
      var self = this;
      var login = '[data-action="login"]';
      $(document).off('click.login.FPointer').on('click.login.FPointer', login, function () {
        self.callFormValid();
        self.login();
      });

      $(document).keyup(function (e) {
        if (e.keyCode === 13) {
          $("[data-action='login']").click();
        }
      });
    }
  };
  return app;
});