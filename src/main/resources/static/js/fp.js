var localObj = window.location;
var basePath = localObj.protocol+"//"+localObj.host;



var table,
  filter = {
    pageSize: 10,
    pageNo: 1
  };

// 初始化表格及分页
$.ajax({
  url: basePath + '/project/solution',
  type: 'GET',
  dataType: 'json',
  data: filter
}).then(function (res) {
  table = $('.table').DataTable({
    data: res.data.fpHelp,
    // 禁止排序
    ordering: false,
    lengthChange: false,
    searching: true,
    scrollY: 440,
    scrollX: false,
    pageLength: 20,
    paging: false,
    info: false,
    columns: [
      { data: 'errCode', width: 60 },
      { data: 'errKeyWord', width: 150, className: 'break-all' },
      { data: 'errReason', width: 300 },
      { data: 'solution', width: 500, className: 'break-all' }
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

$(document).on('change', '.dataTables_filter input', function () {
  filter.errKeyWord = $(this).val();
  _renderTable();
})

// 渲染表格
function _renderTable() {
  $.ajax({
    url: basePath + '/project/solution',
    type: 'GET',
    dataType: 'json',
    data: filter,
  }).then(function (res) {
    table.rows().remove();
    table.rows.add(res.data.fpHelp);
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