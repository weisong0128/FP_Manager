define([
  'model',
  'data',
  'tool',
  'public',
  'analysis/analysisModel',
  'filter'
], function (model, dataApp, tool, publicApp, analysisModel, filterApp) {

  var app = {
    props: {},
    table: "",
    condition: {searchTime: 'year'},
    init: function (props) {
      this.props = props;
      publicApp.initAjaxSetup();
      this.initTableUseDetailTableTopTen();
      this.events();
      this.getStatisticsData();
    },
    getStatisticsData: function () {
      var self = this,
        condition = $.extend(true, {}, self.condition);
      condition.pjName = self.props.projectName;

      analysisModel.getStatisticsData(condition).then(function (data) {
        if (tool.checkStatusCode(data.code)) {
          var res = data.data,
            sqlType = self.sqlTypeChartData(res),
            unqualifiedSql = self.unQualifiedSQLChartData(res),
            timeFormat = self.timeFormat(res.hour);

          self.initSQLTypeChart(sqlType);
          self.initUnQualifiedSQLChart(unqualifiedSql);
          self.initAllSQLChart(timeFormat, res.sqlCount);
          self.initerrorSQLChart(res.yearMonth, res.errorCount);
        } else {
          publicApp._toastDialog(data.msg, {"intent": "danger", "position": "top_center"});
        }
      });
    },
    //SQL语句类型图所需值
    sqlTypeChartData: function (res) {
      var sqlType = [],
        total = 0,
        data = res.tag;
      for (var i = 0; i < data.length; i++) {
        total += Number(data[i].value);
        // if (data[i].name === null) {
        //   elseValue += data[i].value;
        //   data.pop();
        // }
      }
      if (total > 0) {
        res.tag.map(function (item) {
          var obj = {};

          if(item.tag === "ins|exp") {
            item.name = "导入导出语句";
          }
          // if (item.name === "其他语句") {
          //   obj.value = Number(item.value) + Number(elseValue);
          // } else {
          //   obj.value = item.value;
          // }
          obj.value = item.value;
          obj.name = [item.name, Number(obj.value * 100 / total).toFixed(2), String(obj.value).replace(/\d{1,3}(?=(\d{3})+$)/g, '$&,')].join("*");

          switch (item.name) {
            case "复杂语句":
              obj.itemStyle = {color: '#2985f7'};
              break;
            case "简单语句":
              obj.itemStyle = {color: '#00b050'};
              break;
            case "导入导出语句":
              obj.itemStyle = {color: '#a6937c'};
              break;
            case "其他语句":
              obj.itemStyle = {color: '#c5cedf'};
              break;
            default:
              obj.itemStyle = {color: '#c5cedf'};
          }
          sqlType.push(obj);
        });

      }
      return sqlType;
    },
    //不合格SQL占比类型图所需数据
    unQualifiedSQLChartData: function (res) {
      var unqualifiedSql = [];
      var unqualifiledObj = {};
      var qualifiledObj = {};
      var total = res.qualifiedSql + res.unqualifiedSql;

      if (total > 0) {
        unqualifiledObj.value = res.unqualifiedSql;
        unqualifiledObj.name = ["不合格SQL", Number(res.unqualifiedSql * 100 / total).toFixed(2), String(res.unqualifiedSql).replace(/\d{1,3}(?=(\d{3})+$)/g, '$&,')].join("*");
        unqualifiledObj.itemStyle = {color: '#c5cedf'};
        unqualifiedSql.push(unqualifiledObj);

        qualifiledObj.value = res.qualifiedSql;
        qualifiledObj.name = ["合格SQL", Number(res.qualifiedSql * 100 / total).toFixed(2), String(res.qualifiedSql).replace(/\d{1,3}(?=(\d{3})+$)/g, '$&,')].join("*");
        qualifiledObj.itemStyle = {color: '#2985f7'};
        unqualifiedSql.push(qualifiledObj);
      } else {
        unqualifiedSql = [];
      }
      return unqualifiedSql;
    },
    //时间格式转换
    timeFormat: function (data) {
      var arr = [];
      for (var i = 0; i < data.length; i++) {
        arr.push(data[i] + ":00");
      }
      return arr;
    },
    //SQL语句类型图初始化
    initSQLTypeChart: function (sqlType) {
      var option = {
        legend: {
          orient: 'vertical',
          right: '3%',
          // left: '40%',
          top: '25%',
          selectedMode: false,
          icon: 'circle',
          itemHeight: 10,
          itemWidth: 10,
          itemGap: 20,
          textStyle: {
            fontSize: 14,
            color: '#5A5E66'
          },
          formatter: function (params) {
            var value = params.split("*");
            return value[0] + '  '+ value[2] + '个';
          }
        },
        series: [
          {
            // name: "SQL语句类型",
            type: 'pie',
            radius: ['50%', '65%'],
            center: ['35%', '50%'],
            // avoidLabelOverlap: true,
            hoverAnimation: false,
            minAngle: 3,
            label: {
              normal: {
                show: true,
                formatter: function (params) {
                  var value = params.name.split("*");
                  return value[0] + '  ' + value[1] + '%';
                }
              }
            },
            data: sqlType
          }
        ]
      };
      var statisticsChart = echarts.init(document.getElementById('statisticsChart'));
      statisticsChart.setOption(option);
      //图表自适应
      $(window).on("resize", function () {
        statisticsChart.resize();
      });
    },
    //不合格SQL占比类型图初始化
    initUnQualifiedSQLChart: function (unqualifiedSql) {
      var option = {
        legend: {
          orient: 'vertical',
          // left: '60%',
          right: '3%',
          top: '30%',
          selectedMode: false,
          icon: 'circle',
          itemHeight: 10,
          itemWidth: 10,
          itemGap: 20,
          textStyle: {
            fontSize: 14,
            color: '#5A5E66'
          },
          formatter: function (params) {
            var value = params.split("*");
            return value[0] + '  '+ value[2] + '个';
          }
        },
        series: [
          {
            // name: "SQL语句类型",
            type: 'pie',
            radius: ['50%', '65%'],
            center: ['35%', '50%'],
            // avoidLabelOverlap: false,
            hoverAnimation: false,
            minAngle: 3,
            label: {
              normal: {
                show: true,
                formatter: function (params) {
                  var value = params.name.split("*");
                  return value[0] + '  ' + value[1] + '%';
                }
              }
            },
            data: unqualifiedSql
          }
        ]
      };
      var errorSqlChart = echarts.init(document.getElementById('errorSqlChart'));
      errorSqlChart.setOption(option);
      //图表自适应
      $(window).on("resize", function () {
        errorSqlChart.resize();
      });
    },
    //所有SQL各时刻走势初始化
    initAllSQLChart: function (time, num) {
      var option = {
        tooltip: {
          trigger: "axis",
          formatter: function (params) {
            var html = params.length > 0 ? "<div>" + params[0].name + "所有SQL：" + params[0].value + "个</div>" : '';
            return html;
          }
        },
        grid: {
          left: "5%",
          top: 30,
          right: "8%",
          bottom: "8%",
          containLabel: true
        },
        xAxis: {
          type: "category",
          data: time,

          axisTick: {
            alignWithLabel: true
          },
          axisLabel: {
            textStyle: {
              color: "#5A5E66"
            }
          },
          axisLine: {
            lineStyle: {
              color: "#dfe4ed"
            }
          }
        },
        yAxis: {
          type: "value",
          splitLine: {
            lineStyle: {
              type: "dashed",
              color: "#dfe4ed"
            }
          },
          axisLabel: {
            fontSize: 12,
            align: "right",
            textStyle: {
              color: "#5A5E66"
            }
          },
          axisTick: {
            show: false
          },
          axisLine: {
            show: false
          }
        },
        series: [{
          name: "所有SQL",
          type: "line",
          data: num,
          symbolSize: 6,
          hoverAnimation: true,
          lineStyle: {
            width: 1.5
          },
          itemStyle: {
            borderWidth: 2,
            color: "#2985f7"
          }
        }]
      };
      var allSQLChart = echarts.init(document.getElementById("allSqlChart"));
      allSQLChart.setOption(option);
      //图表自适应
      $(window).on("resize", function () {
        allSQLChart.resize();
      });
    },
    //错误信息走势初始化
    initerrorSQLChart: function (time, num) {
      var option = {
        tooltip: {
          trigger: "axis",
          formatter: function (params) {
            var html = params.length > 0 ? "<div>" + params[0].name + "错误SQL：" + params[0].value + "个</div>" : '';
            return html;
          }
        },
        grid: {
          left: "5%",
          top: 30,
          right: "8%",
          bottom: "8%",
          containLabel: true
        },
        xAxis: {
          type: "category",
          data: time,

          axisTick: {
            alignWithLabel: true
          },
          axisLabel: {
            textStyle: {
              color: "#5A5E66"
            }
          },
          axisLine: {
            lineStyle: {
              color: "#dfe4ed"
            }
          }
        },
        yAxis: {
          type: "value",
          splitLine: {
            lineStyle: {
              type: "dashed",
              color: "#dfe4ed"
            }
          },
          axisLabel: {
            fontSize: 12,
            align: "right",
            textStyle: {
              color: "#5A5E66"
            }
          },
          axisTick: {
            show: false
          },
          axisLine: {
            show: false
          }
        },
        series: [{
          name: "错误SQL",
          type: "line",
          data: num,
          symbolSize: 6,
          hoverAnimation: true,
          lineStyle: {
            width: 1.5
          },
          itemStyle: {
            borderWidth: 2,
            color: "#2985f7"
          }
        }]
      };
      var errorDetailChart = echarts.init(document.getElementById("errorDetailChart"));
      errorDetailChart.setOption(option);
      //图表自适应
      $(window).on("resize", function () {
        errorDetailChart.resize();
      });
    },

    initTableUseDetailTableTopTen: function () {
      if (this.table) {
        return;
      }
      var self = this;
      var $id = $('#tableUseDetailTableTopTen');
      var columns = [
        {
          data: "tableName",
          orderable: false,
          render: function (args) {
            return '<div class="w-default"><span class="ellipsis" title="' + args + '">' + args + '</span></div>';
          }
        },
        {
          data: "count",
          orderable: true,
          render: function (args) {
            return '<span class="w-small" title="' + args + '">' + args + '</span>';
          }
        },
        {
          data: "pjlocation",
          orderable: false,
          render: function (args) {
            return '<div class="w-default"><span class="ellipsis" title="' + args + '">' + args + '</span></div>';
          }
        },
        {
          data: "dateStr",
          orderable: true,
          render: function (args) {
            return '<span class="w-default" title="' + publicApp.timeFormat(args) + '">' + publicApp.timeFormat(args) + '</span>';
          }
        }
      ];

      var ajaxFun = function (params, callback) {
        var condition = $.extend(true, {}, self.condition);
        condition.pjName = self.props.projectName;
        condition.pageNo = 1;
        condition.pageSize = 10;
        condition.sort = 'desc';
        condition.sortName = 'cnt';

        analysisModel.getBusinessDetails(condition).then(function (res) {
          var obj = {
            data: []
          };
          if (tool.checkStatusCode(res.code)) {
            var total = res.page && res.page.totalRows || 0;
            var pageData = res.data || [];
            var dataTemp = [];
            // 如有需要，可对表格数据进行封装（下面对返回的字段进行了筛选）
            for (var i = 0; i < pageData.length; i++) {
              var obj2 = {};
              obj2['tableName'] = pageData[i].tableName || "";
              obj2['count'] = pageData[i].cnt || "";
              obj2['pjlocation'] = pageData[i].pjlocation || "";
              obj2['dateStr'] = pageData[i].date || "";
              dataTemp.push(obj2);
            }
            obj.data = dataTemp;
            obj.draw = params.draw;
            obj.recordsTotal = total;
            obj.recordsFiltered = total;
          } else {
            publicApp._toastDialog(res.msg, {"intent": "danger", "position": "top_center"});
          }
          callback && callback(obj);
        });
      };

      var options = {
        serverSide: false,
        destroy: true,
        scrollY: '570px',
        info: false,
        lengthChange: false,
        paging: false,
        ordering: true,
        orderClasses: false,
        order: [[1, 'desc'], [3, '']],
        columns: columns,
        ajax: ajaxFun
      };
      self.table = publicApp.initTable($id, options);
    },
    setTableFilter: function (module) {
      var filterObj = filterApp.getFilter(dataApp.filterObj[module]);
      if (filterObj.searchTime === undefined || filterObj.searchTime === "") {
        filterObj.searchTime = "year";
      }

      this.condition = $.extend(true, {}, dataApp.statisticsParams, filterObj);
      this.condition.pjName = this.props.projectName;
      if(filterObj.searchTime === "custom") {
        var timeArr = $.trim($("#"+module + " .customTimeTag").val()).split('~') || "";
        this.condition.startTime = tool.getAbsoluteSecond(timeArr[0]) || 0;
        this.condition.endTime = tool.getAbsoluteSecond(timeArr[1]) || 0;
      }
      this.getStatisticsData(this.condition);
      publicApp.updateTable($('#tableUseDetailTableTopTen'));
    },
    events: function () {
      var self = this;
      var statisticsSearch = '[data-action="statisticsSearch"]';
      $(document).off('click.statisticsSearch.FPointer').on('click.statisticsSearch.FPointer', statisticsSearch, function () {
        var $closest = $(this).closest(".form-validate");
        var module = $closest.attr("id");
        self.setTableFilter(module);
      });

      $("[data-action='statisticsSearch']").click();

    }
  };
  return app;
});