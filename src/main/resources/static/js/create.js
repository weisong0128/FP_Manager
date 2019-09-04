var localObj = window.location;
var basePath = localObj.protocol+"//"+localObj.host;



var test = null,
    create = null,
    download = null,
    timer = null;

$('.file-upload').on('click', function() {
  $('.file-input').trigger('click');
})

// 文件上传
$('.file-input').on('change', function(e) {
  console.log(this.files, e);
  var fd = new FormData();
  fd.append('file', this.files[0]);
  $('.test-btn, .create-btn, .download-btn, .start-btn').addClass('disabled');
  $('.step3').find('.progress-bar').width('0%').attr('aria-valuenow', 0);
  $('.step3').find('.progress-percent').text('0%');
  clearInterval(timer);
  $.ajax({
    url: basePath + '/project/uploadexcel',
    type: 'POST',
    dataType: 'json',
    data: fd,
    processData: false,
    contentType: false,
  }).then(function(res) {
    if (res.code === 0) {
      test = res.data;
      $('.test-btn').removeClass('disabled');
    } else {
      Dialog.toast(res.data, {
        intent: 'danger'
      });
      $('.test-btn').addClass('disabled');
    }
  })
})

// 检验元数据
$('.test-btn').on('click', function() {
  var that = this;
  if ($(this).hasClass('disabled')) return;
  $(this).addClass('load');
  $.ajax({
    url: basePath + '/project/check',
    type: 'GET',
    dataType: 'json',
    data: test,
  }).then(function(res) {
    $(that).removeClass('load');
    if (res.code === 0) {
      create = res.data;
      $('.create-btn').removeClass('disabled');
      $('.test-btn').addClass('disabled');
    } else {
      if (res.data.errType == 1) {
        Dialog.open({
          title: '<span class="aidicon aidicon-alert-circle-outline aidicon-warning"></span>提示',
          width: 400,
          height: 260,
          closable: false,
          content: '<div class="form-horizon error-dialog">' +
              '<form class="form-validate-dom1">' +
              '<p>' + res.data.msg + '</p>' +
              '<div class="form-group clearfix">' +
              '<label class="form-label" for="exampleInputName1">字段类型</label>' +
              '<span>' + res.data.oldType + '</span>' +
              '</div>' +
              '<div class="form-group clearfix">' +
              '<label class="form-label" for="exampleInputPhone1">FP字段类型</label>' +
              '<input type="text" class="form-control fp-input" placeholder="请输入FP字段类型">' +
              '</div></form></div>',
          button: [{
            label: '确定',
            click: function() {
              var val = $.trim($('.fp-input').val()),
                  data = $.extend(true, {
                    content: val,
                    updateType: 'T'
                  }, res.data);
              if (val !== '') {
                $.ajax({
                  url: basePath + '/project/updateexcel',
                  type: 'POST',
                  dataType: 'json',
                  data: data,
                }).then(function (rest) {
                  if (rest.code == 0){
                    $('.test-btn').click()
                  }
                })
              } else {
                return false;
              }

            }
          }]
        });
      } else {
        Dialog.open({
          title: '<span class="aidicon aidicon-alert-circle-outline aidicon-warning"></span>提示',
          width: 400,
          height: 200,
          closable: false,
          content: '<div class="form-horizon error-dialog">' +
              '<form class="form-validate-dom1">' +
              '<p>' + res.data.msg + '</p>' +
              '<div class="form-group clearfix">' +
              '<label class="form-label" for="exampleInputPhone1">清输入</label>' +
              '<input type="text" class="form-control fp-input" placeholder="">' +
              '</div></form></div>',
          button: [{
            label: '确定',
            click: function() {
              var val = $.trim($('.fp-input').val()),
                  data = $.extend(true, {
                    content: val,
                    updateType: 'E'
                  }, res.data);
              if (val !== '') {
                $.ajax({
                  url: basePath + '/project/updateexcel',
                  type: 'POST',
                  dataType: 'json',
                  data: data,
                }).then(function (rest) {
                  if (rest.code == 0){
                    $('.test-btn').click()
                  }
                })
              } else {
                return false;
              }

            }
          }]
        });
      }

      // $('.create-btn').addClass('disabled');
    }
  })

});

// 生成建表脚本
$('.create-btn').on('click', function() {
  var that = this;
  if ($(this).hasClass('disabled')) return;
  $(this).addClass('load');
  $.ajax({
    url: basePath + '/project/create',
    type: 'GET',
    dataType: 'json',
    data: create,
    beforeSend: function(a) {
      var per = 0;
      clearInterval(timer);
      timer = setInterval(function() {
        $('.step3').find('.progress-bar').width(per + '%').attr('aria-valuenow', per);
        $('.step3').find('.progress-percent').text(per + '%');
        if (per <= 99) {
          per += 1;
        }
      }, 100);

    },
    success: function(a) {
      clearInterval(timer);
      $('.step3').find('.progress-bar').width('100%').attr('aria-valuenow', 100);
      $('.step3').find('.progress-percent').text('100%');
    }
  }).then(function(res) {
    $(that).removeClass('load');
    if (res.code === 0) {
      download = res.data;
      $('.step4').find('a').attr('href', '/project/download/?sqlName=' + res.data.sqlName + '&path=' + res.data.path);
      $('.download-btn, .start-btn').removeClass('disabled');
      $('.create-btn').addClass('disabled');
    } else {
      Dialog.toast(res.msg, {
        intent: 'danger'
      });
      $('.download-btn, .start-btn').addClass('disabled');
    }
  })

});

// 下载建表脚本
$('.download-btn').on('click', function() {
  if ($(this).hasClass('disabled')) return;
  window.open(basePath + '/project/download/?sqlName=' + download.sqlName + '&path=' + download.path, '_blank');
});

// 业务元数据信息入库
$('.start-btn').on('click', function() {
  var that = this;
  if ($(this).hasClass('disabled')) return;
  $(this).addClass('load');
  $.ajax({
    url: basePath + '/project/metadate',
    type: 'GET',
    dataType: 'json',
    data: {
      sqlName: download.sqlName,
      path: download.path
    },
  }).then(function(res) {
    $(that).removeClass('load');
    if (res.code === 0) {
      Dialog.toast(res.msg, {
        intent: 'success'
      });
      $('.start-btn').addClass('disabled');
      setTimeout(" history.go(0)",3000);
    } else {
      Dialog.toast(res.msg, {
        intent: 'danger'
      });
    }
  })
});