var localObj = window.location;
var basePath = localObj.protocol+"//"+localObj.host;



// 项目地址自动补全
$.ajax({
  url: basePath + '/project/all',
  type: 'GET',
  dataType: 'json'
}).then(function(res) {
  var data = [];
  res.data.forEach(function(n) {
    data.push(n.pjName);
  })
  $('#typeahead2').typeahead({
    source: data,
    showHintOnFocus: true
  });
})

// 开始分析
$('.submit').on('click', function() {
  var that = this,
    data = {};
  if ($('.form').valid()) {
    $(this).addClass('disabled');
    $('.form').serializeArray().forEach(function(n) {
      data[n.name] = n.value;
    });
    $.ajax({
      url: basePath + '/project/upload',
      type: 'POST',
      dataType: 'json',
      data: data
    }).then(function(res) {
      $(that).removeClass('disabled');
      if (res.code === 0) {
        Dialog.open({
          title: "<span class='aidicon aidicon-check-circle-outline aidicon-success'></span>分析完成",
          width: 320,
          height: 'auto',
          modal: true,
          content: '<div class="success-dialog"><a href="./fail_sql.html?pjName=' + data.pjName + '&pjLocation=' + data.pjLocation + '">不合格SQL展示</a><a href="./error.html?pjName=' + data.pjName + '&pjLocation=' + data.pjLocation + '">报错信息展示</a></div>',
          button: [{
            label: '关闭'
          }]
        });
      } else {
        Dialog.toast(res.msg, {
          intent: 'danger'
        });
      }
    })
  }


});