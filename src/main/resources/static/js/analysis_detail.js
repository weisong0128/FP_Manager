var localObj = window.location;
var basePath = localObj.protocol+"//"+localObj.host;





// $('.project-select').select2({
//   //  minimumResultsForSearch设置为-1，去除搜索框 
//   minimumResultsForSearch: -1,
//   width: '300',
// });
var filter = {
    pjName: 'all',
    pjLocation: 'all',
    searchTime: 'year',
    partition: 1,
    pageSize: 20,
    pageNo: 1
  },
  pjNameSelect, pjLocationSelect,
  chartPie1 = echarts.init($('.chart-pie1')[0]),
  chartPie2 = echarts.init($('.chart-pie2')[0]),
  chartline1 = echarts.init($('.chart-line1')[0]),
  chartline2 = echarts.init($('.chart-line2')[0]),
  table;


// 业务名称，地市Select
$.ajax({
  url: basePath + '/project/all',
  type: 'GET',
  dataType: 'json'
}).then(function(res) {
  var pjNameHtml = '<option selected value="all">全部</option>',
    pjLocation = [],
    pjLocationHtml = '<option selected value="all">全部</option>';
  res.data.forEach(function(n) {
    pjNameHtml += '<option value="' + n.pjName + '">' + n.pjName + '</option>';
    n.pjLocationList.forEach(function(location) {
      if (pjLocation.indexOf(location) === -1) {
        pjLocation.push(location);
      }
    })
  });
  pjLocation.forEach(function(n) {
    pjLocationHtml += '<option value="' + n + '">' + n + '</option>';
  });
  pjNameSelect = $('.pjName-select').html(pjNameHtml).select2({
    width: 300
  }).on('select2:select', function(e) {
    var pjName = $(e.target).val(),
      selectPjLocation = [];
    filter.pjName = pjName.join(',');
    for (var i = 0; i < pjName.length; i++) {
      if (pjName[i] === 'all') {
        selectPjLocation = ['all'];
        break;
      } else {
        for (var j = 0; j < res.data.length; j++) {
          if (pjName[i] == res.data[j].pjName) {
            selectPjLocation = selectPjLocation.concat(res.data[j].pjLocationList);
          }
        }
      }
    }
    pjLocationSelect.select2('val', selectPjLocation);
    pjLocationSelect.trigger('select2:select');
    // _renderTable();
    // _renderChart();
  }).on('select2:unselect', function(e) {
    var pjName = $(e.target).val(),
      selectPjLocation = [];
    filter.pjName = pjName.join(',');
    for (var i = 0; i < pjName.length; i++) {
      if (pjName[i] === 'all') {
        selectPjLocation = ['all'];
        break;
      } else {
        for (var j = 0; j < res.data.length; j++) {
          if (pjName[i] == res.data[j].pjName) {
            selectPjLocation = selectPjLocation.concat(res.data[j].pjLocationList);
          }
        }
      }
    }
    pjLocationSelect.select2('val', selectPjLocation);
    pjLocationSelect.trigger('select2:unselect');
  });
  pjLocationSelect = $('.pjLocation-select').html(pjLocationHtml).select2({
    width: 300
  }).on('select2:select', function(e) {
    filter.pjLocation = $(e.target).val().join(',');
    _renderTable();
    _renderChart();
  }).on('select2:unselect', function(e) {
    filter.pjLocation = $(e.target).val().join(',');
    _renderTable();
    _renderChart();
  });
  $('.time-select').select2({
    width: 300
  }).on('select2:select', function(e) {
    filter.searchTime = $(e.target).val();
    _renderTable();
    _renderChart();
  });
  // 初始化表格及分页
  $.ajax({
    // url: '/project/proportion',
    url: basePath + '/project/rowresult',
    type: 'GET',
    dataType: 'json',
    data: filter
  }).then(function(res) {
    table = $('.table').DataTable({
      data: res.data.rowResult,
      // 禁止排序
      ordering: false,
      lengthChange: false,
      searching: false,
      scrollY: 440,
      scrollX: false,
      pageLength: 20,
      paging: false,
      info: false,
      columns: [{
        data: 'num',
        width: 200
      }, {
        data: 'rowName',
        width: 200
      }, {
        data: 'rowCount',
        width: 200
      }, {
        data: 'percent',
        width: 200
      }],
    });
    $('.page-wrapper').pagination({
      totalrows: res.data.page.totalRows,
      pagesize: res.data.page.pageSize,
      pageno: res.data.page.pageNo,
      jumpable: false,
      callback: function(pageno, pagesize) {
        filter.pageNo = pageno;
        filter.pageSize = pagesize;
        _renderTable();
      }
    });
    if (window.location.search !== '') {
      var urlObj = {};
      window.location.search.slice(1).split('&').forEach(function(n) {
        var a = n.split('=');
        urlObj[a[0]] = decodeURIComponent(a[1]);
      });
      pjNameSelect.select2('val', urlObj.pjName.split(','));
      pjLocationSelect.select2('val', urlObj.pjLocation.split(','));
      pjNameSelect.trigger('select2:select');
      
      // pjLocationSelect.trigger('select2:select');
      // encodeURIComponent
    }
  });
  _renderChart();
});

$('.nav-card-tabs').on('click', 'li', function() {
  var partition = $(this).data('value');
  $(this).addClass('active').siblings().removeClass('active');
  filter.partition = partition;
  _renderTable();
});

// 渲染表格
function _renderTable() {
  $.ajax({
    url: basePath + '/project/rowresult',
    type: 'GET',
    dataType: 'json',
    data: filter,
  }).then(function(res) {
    table.rows().remove();
    table.rows.add(res.data.rowResult);
    table.draw();
    $('.page-wrapper').pagination({
      totalrows: res.data.page.totalRows,
      pagesize: res.data.page.pageSize,
      pageno: res.data.page.pageNo,
      jumpable: false,
      callback: function(pageno, pagesize) {
        filter.pageNo = pageno;
        filter.pageSize = pagesize;
        _renderTable();
      }
    });
  })
}

function _renderChart() {
  $('.sql-detail-link').attr('href', './sql_detail.html??pjName=' + filter.pjName + '&pjLocation=' + filter.pjLocation);
  $('.error-link').attr('href', './error.html??pjName=' + filter.pjName + '&pjLocation=' + filter.pjLocation);
  $.ajax({
    url: basePath + '/project/proportion',
    type: 'GET',
    dataType: 'json',
    data: {
      pjName: filter.pjName,
      pjLocation: filter.pjLocation,
      searchTime: filter.searchTime
    }
  }).then(function(res) {
    var data = res.data,
      tagLegend = [];
    data.tag.forEach(function(n) {
      tagLegend.push(n.name);
    });
    chartPie1.setOption({
      tooltip: {
        trigger: 'item',
        formatter: "{a} <br/>{b} : {c} ({d}%)"
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: tagLegend
      },
      series: [{
        name: 'SQL语句类型',
        type: 'pie',
        radius: '55%',
        center: ['50%', '50%'],
        data: data.tag,
        itemStyle: {
          emphasis: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    });
    chartPie2.setOption({
      legend: {
        orient: 'vertical',
        left: 'left',
        data: ['合格的SQL', '不合格的SQL']
      },
      series: [{
        type: 'pie',
        radius: '55%',
        center: ['50%', '50%'],
        data: [{
          name: '合格的SQL',
          value: data.qualifiedSql
        }, {
          name: '不合格的SQL',
          value: data.unqualifiedSql
        }],
        itemStyle: {
          emphasis: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    });
    chartline1.setOption({
      xAxis: {
        data: data.hour
      },
      series: [{
        data: data.sqlCount
      }]
    });
    chartline2.setOption({
      xAxis: {
        data: data.yearMonth
      },
      series: [{
        data: data.errorCount
      }]
    });
  })
}

chartPie1.setOption({
  title: {
    text: '某站点用户访问来源',
    subtext: '纯属虚构',
    x: 'center',
    show: false
  },
  tooltip: {
    trigger: 'item',
    formatter: "{a} <br/>{b} : {c} ({d}%)"
  },
  legend: {
    orient: 'vertical',
    left: 'left',
    data: ['直接访问', '邮件营销', '联盟广告', '视频广告', '搜索引擎']
  },
  series: [{
    name: '访问来源',
    type: 'pie',
    radius: '55%',
    center: ['50%', '50%'],
    itemStyle: {
      emphasis: {
        shadowBlur: 10,
        shadowOffsetX: 0,
        shadowColor: 'rgba(0, 0, 0, 0.5)'
      }
    }
  }]
});

chartPie2.setOption({
  tooltip: {
    trigger: 'item',
    formatter: "{a} <br/>{b} : {c} ({d}%)"
  },
  legend: {
    orient: 'vertical',
    left: 'left',
    data: ['直接访问', '邮件营销', '联盟广告', '视频广告', '搜索引擎']
  },
  series: [{
    type: 'pie',
    radius: '55%',
    center: ['50%', '50%'],
    itemStyle: {
      emphasis: {
        shadowBlur: 10,
        shadowOffsetX: 0,
        shadowColor: 'rgba(0, 0, 0, 0.5)'
      }
    }
  }]
});

chartline1.setOption({
  xAxis: {
    type: 'category'
  },
  yAxis: {
    type: 'value'
  },
  grid: {
    left: '5%',
    right: '5%',
    top: '10%',
    bottom: '15%'
  },
  series: [{
    type: 'line'
  }]
});

chartline2.setOption({
  xAxis: {
    type: 'category'
  },
  yAxis: {
    type: 'value'
  },
  grid: {
    left: '5%',
    right: '5%',
    top: '10%',
    bottom: '15%'
  },
  series: [{
    type: 'line'
  }]
});