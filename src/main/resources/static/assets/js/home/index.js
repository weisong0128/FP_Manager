define([
  'model',
  'data',
  'tool',
  'public',
  'header',
  'home/homeModel',
  'viewTpl/home/homeHeaderTpl'
], function (model, dataApp, tool, publicApp, headerApp, homeModel, homeHeaderTpl) {

  var app = {
    props: {
      index: 0
    },
    init: function () {
      headerApp.init(this.props);
      this.headerCardRender();
      this.getMapData();
      this.getProjectChartData();
      this.getDBAreaDistribution();
      this.getOpenAuthorize();
    },
    /**
     * 首页头部数据获取及渲染
     */
    headerCardRender: function () {
      homeModel.getHeaderCardData().then(function (data) {
        if (tool.checkStatusCode(data.code)) {
          var headerData = data.data,
            headerCard = {};
          headerCard["fpTotal"] = String(headerData.uuidCount).replace(/\d{1,3}(?=(\d{3})+$)/g, '$&,');
          headerCard["online"] = String(headerData.envirNote1).replace(/\d{1,3}(?=(\d{3})+$)/g, '$&,');
          headerCard["test"] = String(headerData.envirNote2).replace(/\d{1,3}(?=(\d{3})+$)/g, '$&,');
          headerCard["fpInstall"] = String(headerData.cities).replace(/\d{1,3}(?=(\d{3})+$)/g, '$&,');
          headerCard["fpApplication"] = String(headerData.projectnName).replace(/\d{1,3}(?=(\d{3})+$)/g, '$&,');
          $("#headerCard").html(homeHeaderTpl(headerCard));
        } else {
          publicApp._toastDialog(data.msg, { "intent": "danger", "position": "top_center" });
        }
      });
    },
    /**
     * FP数据库全国热区地图的配置
     * @param geoCoordMap geoCoord.json文件数据
     * @param provinceData 全国FP数据库环境授权数据
     */
    mapRender: function (geoCoordMap, provinceData) {
      var myMap = echarts.init(document.getElementById("FPDatabaseMap"));
      homeModel.getChina().then(function (geoJson) {
        echarts.registerMap("china", geoJson);

        var convertData = function (data) {
          var res = [];
          for (var i = 0; i < data.length; i++) {
            var geoCoord = geoCoordMap[data[i].provinces];
            if (geoCoord) {
              res.push({
                name: data[i].provinces,
                value: (data[i].onlinecount+data[i].testcount),
                online: data[i].onlinecount,
                test: data[i].testcount,
                coord: geoCoord
              });
            }
          }
          return res;
        };

        var option = {
          tooltip: {
            trigger: 'item',
            padding: 12,
            formatter: function (params) {
              var html = params.data === undefined ? '' : '<div>' +
                params.data.name + '</div>' +
                '<div>' +
                '线上环境授权 : <span>' + params.data.online +
                '</span></div>' +
                '<div>' +
                '测试环境授权 : <span>' + params.data.test +
                '</span></div>';
              return html;
            }
          },
          visualMap: {
            show: true,
            x: 'left',
            y: 'bottom',
            type: 'piecewise',
            padding: [0, 0, 30, 20],
            itemWidth: 16,
            itemHeight: 8,
            pieces: [
              { gt: 100, label: '> 100个', color: "rgba(250, 85, 85, 1)" },
              { gt: 50, lte: 100, label: '50 ~ 100个', color: "rgba(246, 126, 47, 1)" },
              { gt: 30, lte: 50, label: '30 ~ 50个', color: "rgba(255, 194, 102, 1)" },
              { gt: 9, lte: 30, label: '9 ~ 30个', color: "rgba(102, 194, 255, 1)" },
              { gt: 0, lte: 9, label: '0 ~ 9个', color: "rgba(212, 220, 233, 1)" }
            ]
          },
          geo: {//地图配置
            name: '地图',
            map: 'china',
            zoom: 1.2,//当前视角的缩放比例
            coordinateSystem: 'geo'
          },
          series: [{
            name: '热力图',
            type: 'map',
            mapType: 'china',
            zoom: 1.2,
            coordinateSystem: 'geo',
            data: convertData(provinceData),
            label: {
              normal: {
                show: true
              },
              emphasis: {
                show: true, //鼠标移入显示地区名称
                color: "#fff"
              }
            },
            itemStyle: {
              normal: {
                areaColor: 'rgb(212, 220, 233)',
                borderColor: '#fff',
                borderWidth: 2
              },
              emphasis: {
                areaColor: '#2985f7',
                borderColor: '#fff',
                borderWidth: 2
              }
            },
            tooltip: {
              trigger: 'none' //取消地图块上面的提示
            },
            markPoint: {
              symbol: 'pin',
              data: convertData(provinceData),
              hoverAnimation: true,
              label: {
                normal: {
                  formatter: function (val) {
                    return val.data.value;
                  }
                }
              },
              itemStyle: {
                normal: {
                  color: '#FF8433'//定位小点颜色
                }
              }
            }

          }]
        };
        myMap.setOption(option);

        //图表自适应
        $(window).on("resize", function () {
          myMap.resize();
        });
      });
    },
    /**
     * 获取地图数据并渲染地图
     */
    getMapData: function () {
      var self = this;
      homeModel.getGeoCoord().then(function (geo) {
        homeModel.getFPDatabseDot().then(function (fpDB) {
          if (tool.checkStatusCode(fpDB.code)) {
            self.mapRender(geo, fpDB.data);
          } else {
            publicApp._toastDialog(fpDB.msg, { "intent": "danger", "position": "top_center" });
          }
        });
      });
    },
    /**
     * 各项目线上环境授权TOP10柱状图配置
     */
    projectChartRender: function (itemName, itemValue) {
      var myChart = echarts.init(document.getElementById("projectAuthorize"));
      var option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow"
          }
        },
        grid: {
          left: "5%",
          top: "3%",
          right: "8%",
          bottom: "5%",
          containLabel: true
        },
        xAxis: {
          type: "value",
          splitNumber: 10,
          splitLine: {
            lineStyle: {
              type: "dashed",
              color: "#dfe4ed"
            }
          },
          axisTick: {
            show: false //隐藏坐标轴上的刻度线
          },
          axisLabel: {
            textStyle: {
              color: "#66686E"
            }
          },
          axisLine: {
            lineStyle: {
              color: "#dfe4ed"
            }
          }
        },
        yAxis: {
          type: "category",
          data: itemName,
          axisLabel: {
            fontSize: 12,
            align: "right",
            textStyle: {
              color: "#66686E"
            },
            formatter: function (params) {
              if (params.length > 10) {
                return params.substring(0, 10) + "...";
              }
              else {
                return params;
              }
            }
          },
          axisTick: {
            show: false //隐藏坐标轴上的刻度线
          },
          axisLine: {
            lineStyle: {
              color: "#dfe4ed"
            }
          }
        },
        series: [{
          name: "线上环境授权",
          type: "bar",
          barWidth: 16,
          barGap: 30,
          // tooltip: {
          //   trigger: 'none'
          // },
          itemStyle: {
            normal: {
              color: "rgba(41, 133, 247, 1)",
              label: {
                show: true,
                position: 'right',
                textStyle: {
                  color: '#0E1011',
                  fontSize: 14
                }
              }
            }
          },
          data: itemValue
        }]
      };
      myChart.setOption(option);
      //图表自适应
      $(window).on("resize", function () {
        myChart.resize();
      });
    },
    /**
     * 获取各项目线上环境授权TOP10数据并渲染柱状图
     */
    getProjectChartData: function () {
      var self = this;
      homeModel.getProjectTop10().then(function (data) {
        if (tool.checkStatusCode(data.code)) {
          var itemList = data.data.sort(self.projectSort("cnt"));
          var itemName = [],
            itemValue = [];
          itemList.map(function (item) {
            itemName.push(item.project_name);
            itemValue.push(item.cnt);
          });
          self.projectChartRender(itemName, itemValue);
        } else {
          publicApp._toastDialog(data.msg, { "intent": "danger", "position": "top_center" });
        }
      });
    },
    /**
     * 根据各项目线上环境授权TOP10数据升序排列
     */
    projectSort: function (property) {
      return function (obj1, obj2) {
        return obj1[property] - obj2[property];
      };
    },
    /**
     * FP数据库地区分布TOP5正负条形图配置
     */
    fpDatabaseChartRender: function (placeName, testValue, onlineValue) {
      var myGraph = echarts.init(document.getElementById("FPDatabaseChart"));
      var option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow"
          }
        },
        legend: {
          right: 20,
          top: 5,
          data: ["测试环境授权", "线上环境授权"],
          icon: "circle",
          itemHeight: 8,
          itemWidth: 8,
          textStyle: {
            fontSize: 12,
            color: "#5A5E66"
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
          type: "value",
          splitNumber: 10,
          splitLine: {
            lineStyle: {
              type: "dashed",
              color: "#dfe4ed"
            }
          },
          axisTick: {
            show: false //隐藏坐标轴上的刻度线
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
          type: "category",
          data: placeName,
          axisLabel: {
            fontSize: 12,
            align: "right",
            textStyle: {
              color: "#5A5E66"
            }
          },
          axisTick: {
            show: false
          }
        },
        series: [{
          name: "测试环境授权",
          type: "bar",
          // barWidth: 14,
          // barGap: 10,
          itemStyle: {
            normal: {
              color: "rgba(33, 109, 245, 1)",
              label: {
                show: true,
                position: 'right',
                textStyle: {
                  color: '#0E1011',
                  fontSize: 14
                }
              }
            }
          },
          data: testValue
        }, {
          name: "线上环境授权",
          type: "bar",
          // barWidth: 14,
          // barGap: 10,
          itemStyle: {
            normal: {
              color: "rgba(84, 157, 249, 1)",
              label: {
                show: true,
                position: 'right',
                textStyle: {
                  color: '#0E1011',
                  fontSize: 14
                }
              }
            }
          },
          data: onlineValue
        }]
      };
      myGraph.setOption(option);
      //图表自适应
      $(window).on("resize", function () {
        myGraph.resize();
      });
    },
    /**
     * 获取FP数据库地区分布TOP5的数据并渲染条形图
     */
    getDBAreaDistribution: function () {
      var self = this;
      homeModel.getDBAreaDistributionTop5().then(function (data) {
        if (tool.checkStatusCode(data.code)) {
          var placeName = [],
            testValue = [],
            onlineValue = [],
          list = data.data.sort(self.projectSort("citiesCount"));
          list.map(function (item) {
            placeName.push(item.cities);
            testValue.push(item.testCount);
            onlineValue.push(item.productionCount);
          });
          self.fpDatabaseChartRender(placeName, testValue, onlineValue);
        } else {
          publicApp._toastDialog(data.msg, { "intent": "danger", "position": "top_center" });
        }
      });
    },
    /**
     * 开放授权趋势折线图配置
     */
    openAutherizeChartRender: function (month, num) {
      var myLineChart = echarts.init(document.getElementById("openAuthorizeTrend"));
      var option = {
        tooltip: {
          trigger: "axis",
          formatter: function (params) {
            var html = params.length > 0 ? "<div>" + params[0].name + "份授权数量：" + params[0].value + "个</div>" : '';
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
          data: month,

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
          name: "授权数量",
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
      myLineChart.setOption(option);
      //图表自适应
      $(window).on("resize", function () {
        myLineChart.resize();
      });
    },
    /**
     * 获取开放授权趋势的数据并渲染折线图
     */
    getOpenAuthorize: function () {
      var self = this;
      homeModel.getOpenAuthorizeData().then(function (data) {
        if (tool.checkStatusCode(data.code)) {
          var month = [],
            number = [];
          data.data.map(function (item) {
            month.push(item.year+"年"+item.month + "月");
            number.push(item.count);
          });
          self.openAutherizeChartRender(month, number);
        } else {
          publicApp._toastDialog(data.msg, { "intent": "danger", "position": "top_center" });
        }
      });
    }
  };
  return app;
});