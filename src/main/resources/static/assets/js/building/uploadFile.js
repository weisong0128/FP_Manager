define(['public'], function (publicApp) {
  'use strict';
  var app = {
    id: null,
    upload: null,
    init: function (id, callback) {
      this.id = id;
      this.uploadFile(callback);
    },
    uploadFile: function (callback) {
      var self = this;
      //实例化一个plupload上传对象
      self.upload = new plupload.Uploader({
        //触发文件选择对话框的按钮，为那个元素id
        browse_button: self.id,
        filters: {
          mime_types: [{title: "excel files", extensions: "xls,xlsx"}],
          max_file_size: '1M', //最大只能上传1M的文件
          prevent_duplicates: true //不允许选取重复文件
        },
        multi_selection: false,/*是否可以在文件浏览对话框中选择多个文件，true为可以，false为不可以*/
        //服务器端的上传页面地址
        url: "/project/uploadexcel",
        flash_swf_url: './fh-ui/js/plugins/plupload/Moxie.swf', //swf文件，当需要使用swf方式进行上传时需要配置该参数
        silverlight_xap_url: './fh-ui/js/plugins/plupload/Moxie.xap' //silverlight文件，当需要使用silverlight方式进行上传时需要配置该参数
      });

      //当文件添加到上传队列后触发
      self.upload.bind('FilesAdded', function (uploader, files) {
            var reg = /^([\u4E00-\u9FA5A-Za-z0-9-]+)_([\u4E00-\u9FA5A-Za-z0-9-]+)_([\u4E00-\u9FA5A-Za-z0-9-]+)_((((1[6-9]|[2-9]\d)\d{2})(\/|\-|\.|)(0?[13578]|1[02])(\/|\-|\.|)(0?[1-9]|[12]\d|3[01]))|(((1[6-9]|[2-9]\d)\d{2})(\/|\-|\.|)(0?[13456789]|1[012])(\/|\-|\.|)(0?[1-9]|[12]\d|30))|(((1[6-9]|[2-9]\d)\d{2})(\/|\-|\.|)0?2(\/|\-|\.|)(0?[1-9]|1\d|2[0-8]))|(((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)).(xls|xlsx|XLS|XLSX)$/;

            if (!reg.test(files[0].name)) {
              publicApp._toastDialog("文件名格式错误", {"intent": "danger", "position": "top_center"});
              return false;
            }
            var progress = '<div class="upload-wrap uploadFileInfo" data-wrap-id="' + files[0].id + '">' +
                '<div><i class="aidicon aidicon-paperclip file-icon"></i>' + files[0].name + '</div>' +
                '<div class="progress progress-ty progress-width" data-id="' + files[0].id + '">' +
                '<div class="progress-bars">' +
                '<div class="progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">' +
                '</div>' +
                '</div>' +
                '<span class="progress-percent">0%</span>' +
                '</div>' +
                '</div>';
            var $progress = $(progress);
            $(".upload-wrap").hasClass("uploadFileInfo") ? $(".uploadFileInfo").replaceWith($progress) : $("#uploadFile-module").append($progress);
            uploader.start();
          }
      );

//当队列中的某一个文件上传完成后触发
      self.upload.bind('FileUploaded', function (uploader, file, responseObject) {
        $('.upload-wrap[data-wrap-id="' + file.id + '"]').addClass('uploadSuccess');
        callback && callback(responseObject.response);
      });

//会在文件上传过程中不断触发，可以用此事件来显示上传进度
      self.upload.bind('UploadProgress', function (uploader, file) {
        var $dom = $('[data-id="' + file.id + '"]');
        //显示进度
        var percent = file.percent;
        $dom.find('.progress-bar').attr('aria-valuenow', percent);
        $dom.find('.progress-bar').width(percent + '%');
        $dom.find('.progress-percent').html(percent + '%');
      });

// 上传失败时触发
      self.upload.bind('Error', function (uploader, errObject) {
        var $dom = $('.upload-wrap[data-wrap-id="' + errObject.file.id + '"]');
        $dom.find('.progress-bar').addClass('progress-bar-danger');
        publicApp._toastDialog("上传失败", {"intent": "danger", "position": "top_center"});
      });

//在实例对象上调用init()方法进行初始化
      self.upload.init();
    }
  };

  return app;
})
;