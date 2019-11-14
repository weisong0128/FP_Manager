define(['public', 'tool'], function (publicApp, tool) {
    'use strict';
    var app = {
        upload: null,
        timestamp: "",
        filesName: [],
        init: function (timestamp) {
            this.timestamp = timestamp;
            this.uploadLog();
            this.events();
        },
        uploadLog: function () {
            var self = this;
            self.upload = new plupload.Uploader({
                //触发文件选择对话框的按钮，为那个元素id
                browse_button: 'uploadLog',
                drop_element: 'uploadLog',
                filters: {
                    // mime_types: [{title: "log files", extensions: "log*"}],
                    max_file_size: '6144M', //最大只能上传6G的文件
                    prevent_duplicates: true //不允许选取重复文件
                },
                multi_selection: true,/*是否可以在文件浏览对话框中选择多个文件，true为可以，false为不可以*/
                //服务器端的上传页面地址
                url: "/log/batchLogUpload",
                flash_swf_url: './fh-ui/js/plugins/plupload/Moxie.swf', //swf文件，当需要使用swf方式进行上传时需要配置该参数
                silverlight_xap_url: './fh-ui/js/plugins/plupload/Moxie.xap' //silverlight文件，当需要使用silverlight方式进行上传时需要配置该参数
            });

            //当文件添加到上传队列后触发
            self.upload.bind('FilesAdded', function (uploader, files) {
                $("#createCheckForm input[type='file']").attr("name", "uploadLog");
                // var logReg = /^(cl.log)(.*)$/;
                var logReg = /^(cl.log)(|.[0-9]+)$/;//后缀为数字加字符：/(^cl.log$)|(^cl.log.[a-zA-Z0-9]+$)/
                for (var i = 0; i < files.length; i++) {
                    var flag = false;
                    if (!logReg.test(files[i].name)) {
                        self.upload.removeFile(files[i]);
                        // files.splice(i);
                        flag = true;
                        publicApp._toastDialog("第" + (i + 1) + "文件错误", {"intent": "danger", "position": "top_center"});
                    }

                    if(files[i].size >= 6*1024*1024*1024) {
                        self.upload.removeFile(files[i]);
                        // files.splice(i);
                        flag = true;
                        publicApp._toastDialog("第" + (i + 1) + "文件超出限制大小", {"intent": "danger", "position": "top_center"});
                    }
                    flag ? files.splice(i): "";
                }
                var len = self.upload.files.length;
                if (len > 10) {
                    publicApp._toastDialog("最多只能上传十个文件,已截取前10个文件", {"intent": "danger", "position": "top_center"});
                    var spliceLen = self.upload.splice(10, len - 10);
                    files.splice(0 - spliceLen.length);

                }
                for (var f = 0; f < files.length; f++) {
                    var progress = '<div class="upload-wrap" data-wrap-id="' + files[f].id + '">' +
                        '<div class="file-name" title="' + files[f].name + '"><i class="aidicon aidicon-paperclip file-icon"></i>' + files[f].name + '</div>' +
                        '<div class="progress progress-ty progress-log" data-id="' + files[f].id + '">' +
                        '<div class="progress-bars">' +
                        '<div class="progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">' +
                        '</div>' +
                        '</div>' +
                        '<span class="progress-percent">0%<a class="file-uploaded-delete" data-id="' + files[f].id + '" data-action="del-log" >删除</a></span>' +
                        '</div>' +
                        '</div>';
                    var $progress = $(progress);
                    $('#filesList').append($progress);
                    $("#sureUpload").removeClass("disabled");
                }
                if ($("#filesList").children().length === 0) {
                    $(".del-all").css("visibility", "hidden");
                } else {
                    $(".del-all").css("visibility", "visible");
                }
            });

            //当队列中的某一个文件上传完成后触发
            self.upload.bind('FileUploaded', function (uploader, file, responseObject) {
                var res = JSON.parse(responseObject.response);
                var $dom = $('.upload-wrap[data-wrap-id="' + file.id + '"]');
                if (tool.checkStatusCode(res.code)) {
                    // $dom.addClass('uploadSuccess');
                    $dom.find(".progress-bar").addClass("progress-bar-success");
                    // console.log("上传成功！");
                    $(".del-all").css("visibility", "visible");
                    $("#sureAnalysis").removeClass("disabled");
                    self.filesName.push(file.name);
                } else {
                    // $dom.addClass('uploadFail');
                    $dom.find('.progress-bar').addClass('progress-bar-danger');
                    publicApp._toastDialog(res.data, {"intent": "danger", "position": "top_center"});
                }
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
                $dom.addClass('uploadFail');
                $dom.find('.progress-bar').addClass('progress-bar-danger');
                $(".del-all").css("visibility", "visible");
            });

            //在实例对象上调用init()方法进行初始化
            self.upload.init();
        },
        startUpload: function (projectName, projectLocation) {
            var self = this;
            var opt = {
                "projectName": projectName,
                "projectLocation": projectLocation,
                "createTime": self.timestamp
            };
            self.upload.setOption("multipart_params", opt);

            if ($.trim($("#pjName :selected").text()) !== "" && $.trim($("#pjLocation").val()) !== "" && $("#filesList").children().length > 0) {
                self.upload.start();
                return false;
            }
        },

        events: function () {
            var self = this;
            var $btn = $('#uploadLog');
            // 拖拽外部文件，进入目标时触发
            $btn.on('dragenter', function () {
                $btn.addClass('mouseHover');
            });
            // 拖拽外部文件，进入目标，离开目标，防止连续触发
            $btn.on('dragover', function () {
                !$btn.hasClass('mouseHover') && $('.dragUpload').addClass('mouseHover');
                return false;
            });
            // 拖拽外部文件，离开目标元素是触发
            $btn.on('dragleave', function () {
                $btn.removeClass('mouseHover');
            });
            // 在一个拖动过程中，鼠标释放时触发此事件
            $btn.on('drop', function () {
                return false;
            });

            var deleteAll = '[data-action="delete-all"]';
            $(document).off('click.deleteAll.log').on('click.deleteAll.log', deleteAll, function () {
                $("#filesList").empty();
                self.upload.splice(0, self.upload.files.length);
                $(".del-all").css("visibility", "hidden");
                if ($.trim($("#pjName :selected").text()) === "" || $.trim($("#pjLocation").val()) === "" || $("#filesList").children().length === 0) {
                    $("#sureUpload").addClass("disabled");
                    // $("#sureAnalysis").addClass("disabled");
                }
            });

            var delLog = '[data-action="del-log"]';
            $(document).off('click.delLog.log').on('click.delLog.log', delLog, function () {
                var id = $(this).attr("data-id");
                self.upload.removeFile(id);
                $("[data-wrap-id=" + id + "]").remove();
                if ($("#filesList").children().length === 0) {
                    $(".del-all").css("visibility", "hidden");
                    $("#sureUpload").addClass("disabled");
                    // $("#sureAnalysis").addClass("disabled");
                }
            });
            //开始上传
            // var uploadLog = '[id=sureUpload]';
            // $(document).off('click.uploadLog.log').on('click.uploadLog.log', uploadLog, function () {
            //   if($.trim($("#pjName :selected").text()) !== "" && $.trim($("#pjLocation").val()) !== "" && $("#filesList").children().length > 0) {
            //     self.upload.start();
            //     return false;
            //   }
            // });
        }
    };

    return app;
});