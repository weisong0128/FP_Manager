define([], function () {
  var dataApp = {
      baseUrl: '../',
      pageObj: [
        {
          name: "系统首页",
          path: "../home/index.html",
          active: false,
          show: true
        },
        {
          name: "业务分析",
          path: "../analysis/index.html",
          active: false,
          show: true
        },
        {
          name: "日志检测",
          path: "../log/index.html",
          active: false,
          show: true
        },
        {
          name: "FP建表",
          path: "../building/index.html",
          active: false,
          show: true
        },
        {
          name: "授权管理",
          path: "../authorization/index.html",
          active: false,
          show: true
        },
        {
          name: "用户管理",
          path: "../user/index.html",
          active: false,
          show: true
        }
      ],
      filterObj: {},
      filterGroups: {},
      allFilterData: [],
      allSqlParams: {
        pjName: ""
      },
      errorSqlParams: {
        pjName: ""
      },
      errorDetailParams: {
        pjName: ""
      },
      tableUseDetailParams: {
        pjName: ""
      },
      statisticsParams: {
        pjName: ""
      },
      errorDetailLogParams: {},
      errorSqlLogParams: {},
      filedTableParams: {},
      isBuilding: false,
      urlDomain: "http://172.16.108.6:8084",
      // urlDomain: "http://10.0.65.4:8084",
      tableTotal: 30000
    }
  ;
  return dataApp;
});