define([
  'model',
  'data',
  'tool',
  'public',
  'header',
  'viewTpl/building/editTpl',
  '../building/uploadFile.js',
  '../building/uploadScript.js',
  '../building/buildingModel.js'
], function (model, dataApp, tool, publicApp, headerApp, editTpl, fileUpload, scriptUpload, buildingModel) {

  var app = {
    props: {
      index: 3
    },
    fileName: null,
    path: null,
    sqlFileName: null,
    sqlFilePath: null,
    init: function () {
      headerApp.init(this.props);
      this.uploadFile();
      this.uploadScript();
      this.event();
    },
    uploadFile: function () {
      var self = this;
      self.displayCheckLoading("none", -1);
      $(".checked").css("display", "none");

      fileUpload.init("uploadFile", function (data) {
        var res = JSON.parse(data);
        if (tool.checkStatusCode(res.code)) {
          self.fileName = res.data.fileName;
          self.path = res.data.path;
          self.displayCheckLoading("none", 1);
          dataApp.isBuilding = true;
          publicApp._toastDialog(res.msg, {"intent": "success", "position": "top_center"});
        } else {
          self.displayCheckLoading("none", -1);
          publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
        }
      });
    },
    //检验元数据，提交检测按钮显示和取消loading
    displayCheckLoading: function (display, isDisabled) {
      $(".loading-building").css("display", display);
      $(".checking").css("display", display);
      var $dom = $("#submitCheck");
      isDisabled === -1 ? $dom.addClass("btn-primary") : $dom.removeClass("btn-primary");
      $dom.addClass("btn-default");
      publicApp.toggleDisabled("#submitCheck", isDisabled);
    },
    //脚本生成中显示和取消loading
    displayGenerateLoading: function (display, isDisabled) {
      $(".generating").css("display", display);
      $(".loading-generating").css("display", display);
      var $dom = $("#generateScript");
      isDisabled === -1 ? $dom.addClass("btn-primary") : $dom.removeClass("btn-primary");
      $dom.addClass("btn-default");
      publicApp.toggleDisabled("#generateScript", isDisabled);
    },
    //元数据入库显示和取消loading
    displayStorageLoading: function (display, isDisabled) {
      $(".loading-storage").css("display", display);
      publicApp.toggleDisabled("#metadataStorage", isDisabled);
    },
    submitCheck: function () {
      var self = this,
        condition = {};
      self.displayCheckLoading("inline-block", -1);
      $(".checked").css("display", "none");
      $("#uploadFile").css("display", "none");
      $("#againUploadFile").css("display", "block");
      condition.fileName = self.fileName;
      condition.path = self.path;
      buildingModel.checkMetadata(condition).then(function (res) {
        if (tool.checkStatusCode(res.code)) {
          if (res.data.length <= 0) {
            $(".checked").css("display", "inline-block");
            self.displayGenerateLoading("none", 1);
            publicApp._toastDialog("检测完成", {"intent": "success", "position": "top_center"});
          } else {
            publicApp._toastDialog("检测到异常,请修改", {"intent": "warning", "position": "top_center"});
            self.editAbnormalDialog(res.data || []);
            self.displayGenerateLoading("none", -1);
            $(".checked").css("display", "none");
          }
        } else {
          publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
        }
        self.displayCheckLoading("none", 1);
      });
    },
    generateScript: function () {
      var self = this;
      var condition = {};
      self.displayGenerateLoading("inline-block", -1);
      condition.fileName = self.fileName;
      condition.path = self.path;
      buildingModel.generateScript(condition).then(function (res) {
        if (tool.checkStatusCode(res.code)) {
          self.sqlFileName = res.data.sqlName;
          self.sqlFilePath = res.data.path;
          $(".generated").css("display", "inline-block");
          publicApp.toggleDisabled("#metadataStorage", 1);
          publicApp._toastDialog(res.msg, {"intent": "success", "position": "top_center"});
        } else {
          publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
        }
        self.displayGenerateLoading("none", 1);
      });
    },
    uploadScript: function () {
      var self = this;
      scriptUpload.init("uploadScript", function (data) {
        var res = JSON.parse(data);
        if (tool.checkStatusCode(res.code)) {
          self.sqlFileName = res.data.data.fileName;
          self.sqlFilePath = res.data.data.path;
          self.displayStorageLoading("none", 1);
          publicApp._toastDialog(res.msg, {"intent": "success", "position": "top_center"});
        } else {
          self.displayStorageLoading("none", -1);
          publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
        }
      });
    },
    metadataStorage: function () {
      var self = this;
      var condition = {};
      self.displayStorageLoading("inline-block", -1);
      condition.sqlName = self.sqlFileName;
      condition.path = self.sqlFilePath;
      buildingModel.metaDataStorage(condition).then(function (res) {
        if (tool.checkStatusCode(res.code)) {
          publicApp._toastDialog("业务元数据信息入库完成", {"intent": "success", "position": "top_center"});
          setTimeout(function () {
            window.location.reload();//刷新页面
          }, 2000);
        } else {
          self.displayStorageLoading("none", 1);
          publicApp._toastDialog("业务元数据信息入库失败" + "<div style='color:#878D99;font-size:14px;'>失败原因：描述超过60字，通知描述建议少于40字，句子分割为少于10字的短句。</div>", {
            "intent": "danger",
            "position": "top_center"
          });
        }
      });
    },
    //异常编辑弹窗
    editAbnormalDialog: function (data) {
      var self = this;
      Dialog.open({
        id: "editAbnormalForm",
        title: "<i class='aidicon aidicon-alert-circle-outline aidicon-warning'></i><div>请修改异常<p class= 'title-tips'>检测完毕，发现以下" + data.length + "个异常，修改后，请重新下发检测</p></div>",
        width: 500,
        height: 'auto',
        modal: true,
        content: editTpl(data),
        button: [
          {
            id: "operationSure",
            label: "重新下发检测",
            intent: "primary",
            focus: true,
            click: function () {
              self.callFormValid();
              return self.againCheck(data);
            }
          },
          {
            id: "operationCancel",
            label: "取消"
          }
        ],
        onShow: function () {
          var rules = {};
          var messages = {};
          for (var i = 0; i < data.length; i++) {
            var temp = {};
            var mess = {};
            temp['required'] = true;
            mess['required'] = "内容不能为空！";
            rules['error' + i] = temp;
            messages['error' + i] = mess;
          }

          self.validateForm(rules, messages);
        }
      });
    },
    /**
     * 初始化表单验证
     */
    validateForm: function (rules, messages) {
      $("#editAbnormalForm").validate({
        debug: true,//只验证不提交表单
        rules: rules,
        messages: messages
      });
    },
    /**
     * 调用表单验证
     */
    callFormValid: function () {
      $("#editAbnormalForm").valid();
    },
    /**
     * 判断表单是否还有错误
     */
    isFormError: function () {
      return $('#editAbnormalForm').find('.form-group.has-error').length > 0;
    },
    //再次下发检测
    againCheck: function (data) {
      var self = this;
      var arr = [];
      if (self.isFormError()) {
        publicApp._toastDialog("异常未填写完成", {"intent": "warning", "position": "top_center"});
        return false;
      }
      self.displayCheckLoading("inline-block", -1);
      for (var i = 0; i < data.length; i++) {
        var condition = {};
        condition.col = data[i].col;
        condition.content = $.trim($("#error" + i).val());
        condition.fileName = data[i].fileName;
        condition.oldType = data[i].oldType;
        condition.path = data[i].path;
        condition.row = data[i].row;
        condition.sheetNum = data[i].sheetNum;
        Number(data[i].errType) === 1?condition.updateType = "T": '';
        // condition.updateType = "E";
        arr.push(condition);
      }
      buildingModel.editErrorForm(arr).then(function (res) {
        if (res.data === null || res.data.length <= 0) {
          $(".checked").css("display", "inline-block");
          publicApp.toggleDisabled("#generateScript", 1);
        } else {
          publicApp._toastDialog("检测到异常,请修改", {"intent": "warning", "position": "top_center"});
          self.editAbnormalDialog(res.data || []);//检测还有异常，再次弹出编辑框
        }
        self.displayCheckLoading("none", 1);
      });
    },
    //文件再次上传弹窗
    againUploadDialog: function () {
      Dialog.open({
        id: "dialog-upload",
        title: "<span class='aidicon aidicon-alert-circle-outline aidicon-warning'></span>确定要重新上传文件吗？",
        width: 420,
        height: 'auto',
        modal: true,
        content: '<div style="color:#878D99;font-size:14px;">您刚上传的文件尚未建表，是否继续上传</div>',
        button: [
          {
            id: "sure", label: "确认", intent: "primary", focus: true, click: function () {
              $("#uploadFile").click();
              return;
            }
          },
          {
            id: "cancel", label: "取消"
          }
        ]
      });
    },
    event: function () {
      var self = this;
      var againUploadFile = '[data-action="again-upload-file"]';
      $(document).off('click.againUploadFile.authorize').on('click.againUploadFile.authorize', againUploadFile, function () {
        self.againUploadDialog();
      });

      //模板下载
      var downloadExcel = '[data-action="download-excel"]';
      $(document).off('click.downloadExcel.authorize').on('click.downloadExcel.authorize', downloadExcel, function () {
        $(".download").attr("href", dataApp.urlDomain + "/project/downloadexcel");
      });

      var submitCheck = '[data-action="submit-check"]';
      $(document).off('click.submitCheck.building').on('click.submitCheck.building', submitCheck, function () {
        self.submitCheck();
      });

      var generateScript = '[data-action="generate-script"]';
      $(document).off('click.generateScript.building').on('click.generateScript.building', generateScript, function () {
        self.generateScript();
      });

      //脚本下载
      var downloadScript = '[data-action="download-script"]';
      $(document).off('click.downloadScript.authorize').on('click.downloadScript.authorize', downloadScript, function () {
        $("#downloadScript").attr("href", dataApp.urlDomain + "/project/download?sqlName=" + encodeURI(self.sqlFileName) + "&path=" + encodeURI(self.sqlFilePath));
        $("#uploadScript").removeClass("btn-primary");
        $("#uploadScript").addClass("btn-default");
        publicApp.toggleDisabled("#uploadScript", 1);
      });

      var metadataStorage = '[data-action="metadata-storage"]';
      $(document).off('click.metadataStorage.authorize').on('click.metadataStorage.authorize', metadataStorage, function () {
        self.metadataStorage();
      });
    }
  };
  return app;
});