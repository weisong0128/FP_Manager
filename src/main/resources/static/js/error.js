var localObj = window.location;
var basePath = localObj.protocol+"//"+localObj.host;



var table,
  filter = {
    pageSize: 10,
    pageNo: 1,
    timeTag: 'all'
  };

// 初始化表格及分页
$.ajax({
  url: basePath + '/project/operation',
  type: 'GET',
  dataType: 'json',
  data: filter
}).then(function (res) {
  table = $('.table').DataTable({
    data: res.data.operation,
    // 禁止排序
    ordering: false,
    lengthChange: false,
    searching: false,
    scrollY: 440,
    scrollX: false,
    pageLength: 20,
    paging: false,
    info: false,
    columns: [
      { data: 'dateStr', width: 100 },
      { data: 'errcode', width: 50 },
      { data: 'errInfo', width: 500, className: 'break-all' },
      { data: 'pjName', width: 100 },
      { data: 'pjLocation', width: 200 }
    ],
  });
  $('.page-wrapper').pagination({
    totalrows: res.data.page.totalRows,
    pagesize: res.data.page.pageSize,
    pageno: res.data.page.pageNo,
    jumpable: false,
    callback: function (pageno, pagesize) {
      filter.pageNo = pageno;
      filter.pageSize = pagesize;
      _renderTable();
    }
  });
});


// 项目名称，项目地点筛选项
$.ajax({
  url: basePath + '/project/all',
  type: 'GET',
  dataType: 'json'
}).then(function (res) {
  var pjNameHtml = '',
    pjLocation = [],
    pjLocationHtml = '';
  res.data.forEach(function (n) {
    pjNameHtml += '<label class="checkbox-inline checkbox-card"><input type="checkbox" name="pjName" value="' + n.pjName + '"><span class="text-label">' + n.pjName + '</span></label>';
    n.pjLocationList.forEach(function (location) {
      if (pjLocation.indexOf(location) === -1) {
        pjLocation.push(location);
      }
    })
  });
  pjLocation.forEach(function (n) {
    pjLocationHtml += '<label class="checkbox-inline checkbox-card"><input type="checkbox" name="pjLocation" value="' + n + '"><span class="text-label">' + n + '</span></label>';
  });
  $('.pjName-group').append($(pjNameHtml));
  $('.pjLocation-group').append($(pjLocationHtml));
  if (window.location.search !== '') {
    var urlObj = {};
    window.location.search.slice(1).split('&').forEach(function (n) {
      var a = n.split('=');
      urlObj[a[0]] = decodeURIComponent(a[1]);
    });
    var pjName = urlObj.pjName.split(','),
      pjLocation = urlObj.pjLocation.split(',');
    $('input[name="pjName"]').each(function () {
      if (pjName.indexOf($(this).val()) !== -1) {
        $(this).prop('checked', true);
      }
    });
    $('input[name="pjLocation"]').each(function () {
      if (pjLocation.indexOf($(this).val()) !== -1) {
        $(this).prop('checked', true);
      }
    });
    filter.pjName = urlObj.pjName;
    filter.pjLocation = urlObj.pjLocation;
    _renderTable();
  }
});


$('.form-group').on('change', 'input[type="checkbox"]', function () {
  var $group = $(this).closest('.form-group'),
    val = $(this).val(),
    checked = $(this).prop('checked'),
    $allCheckbox = $group.find('.chooseAll input[type="checkbox"]');

  if (val === 'all') {
    if (checked) {
      $group.find('input[type="checkbox"]').prop('checked', true);
    } else {
      $group.find('input[type="checkbox"]').prop('checked', false);
    }
  } else {
    if (!checked) {
      $allCheckbox.prop('checked', false);
    } else {
      if ($group.find('.checkbox-card').not($('.chooseAll')).find('input[type="checkbox"]:checked').size() < $group.find('.checkbox-card').not($('.chooseAll')).size()) {
        $allCheckbox.prop('checked', false);
      } else {
        $allCheckbox.prop('checked', true);
      }
    }
  };
});

// 筛选项
$('.form-group').on('change', 'input[type="checkbox"], input[type="radio"]', function () {
  var data = {
    timeTag: [],
    pjName: [],
    pjLocation: []
  };
  $('.filter-form').serializeArray().forEach(function (n) {
    data[n.name].push(n.value);
  });
  $.each(data, function (key, value) {
    data[key] = value.join(',');
  });
  console.log($('.filter-form').serializeArray(), data);
  $.extend(true, filter, data);
  _renderTable();
});

// 渲染表格
function _renderTable() {
  $.ajax({
    url: basePath + '/project/operation',
    type: 'GET',
    dataType: 'json',
    data: filter,
  }).then(function (res) {
    table.rows().remove();
    table.rows.add(res.data.operation);
    table.draw();
    $('.page-wrapper').pagination({
      totalrows: res.data.page.totalRows,
      pagesize: res.data.page.pageSize,
      pageno: res.data.page.pageNo,
      jumpable: false,
      callback: function (pageno, pagesize) {
        filter.pageNo = pageno;
        filter.pageSize = pagesize;
        _renderTable();
      }
    });
  })
}