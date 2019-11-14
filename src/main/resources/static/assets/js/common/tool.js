define(['data'], function (dataApp) {
  var tool = {

    /**
     * 判断接口状态码
     **/
    checkStatusCode: function (code) {
      var checkRes;
      if (Number(code) === 0) {
        checkRes = true;
      } else {
        checkRes = false;
      }
      return checkRes;
    },

    //获取路由参数
    getUrlParams: function () {
      var hashDeatail = location.href.split("?"),
          hashName = hashDeatail[0].split("#")[1],//路由地址
          params = hashDeatail[1] ? hashDeatail[1].split("&") : [],//参数内容
          query = {};
      for (var i = 0; i < params.length; i++) {
        var item = params[i].split("=");
        query[item[0]] = item[1];
      }
      return {
        path: hashName,
        query: query
      };
    },

    /**
     * 判断是否为对象
     **/
    isObject: function (obj) {
      return Object.prototype.toString.call(obj) === '[object Object]';
    },

    /**
     * 判断是否为数据
     **/
    isArray: function (obj) {
      return Object.prototype.toString.call(obj) === '[object Array]';
    },

    /**
     * datetime转yyyy-mm-dd hh:mm:ss
     */
    _timeToString: function (_time) {
      var year = _time.getFullYear();
      var month = _time.getMonth() < 9 ? '0' + (_time.getMonth() - 0 + 1) : _time.getMonth() - 0 + 1;
      var day = _time.getDate() < 10 ? '0' + _time.getDate() : _time.getDate();
      var hour = _time.getHours() < 10 ? '0' + _time.getHours() : _time.getHours();
      var min = _time.getMinutes() < 10 ? '0' + _time.getMinutes() : _time.getMinutes();
      var sec = _time.getSeconds() < 10 ? '0' + _time.getSeconds() : _time.getSeconds();
      return year + '-' + month + '-' + day + ' ' + hour + ':' + min + ':' + sec;
    },

    /**
     * datetime转毫秒
     **/
    getAbsoluteMilisecond: function () {
      return Math.round(new Date().getTime());
    },

    /**
     * datetime转绝对秒
     **/
    getAbsoluteSecond: function (date) {
      if (this.isObject(date)) {
        var seconds = date.seconds ? date.seconds : '00';
        var d = (date.year + '-' + date.month + '-' + date.date + ' ' + date.hours + ':' + date.minutes + ':' + seconds);
        return Date.parse(d) / 1000;
      } else {
        return Date.parse(date) / 1000;
      }
    },

    /**
     * data obj 转日期字符串
     **/
    _dateObjToString: function (date) {
      if (this.isObject(date) && date.year) {
        var fmt = 'yyyy-MM-dd hh:mm:ss';
        var o = {};
        o.yyyy = date.year;
        o.MM = date.month;
        o.dd = date.date;
        o.hh = date.hours;
        o.mm = date.minutes;
        o.ss = date.seconds;

        if (/(y+)/.test(fmt)) {
          fmt = fmt.replace(RegExp.$1, (date.year + "").substr(4 - RegExp.$1.length));
        }

        for (var k in o) {
          if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
          }
        }

        return fmt;
      } else {
        return "";
      }
    },

    /**
     * 设置时间
     *
     */
    setDateTime: function (day, fm) {
      var today = new Date();
      var targetday_milliseconds = today.getTime() + 1000 * 60 * 60 * 24 * day;

      today.setTime(targetday_milliseconds); //这行是关键代码

      var fmt = fm ? fm : 'yyyy-MM-dd hh:mm:ss';
      var m = this.formatDateTime(fmt, today);

      return m;
    },

    /**
     * 时间格式化
     *
     */
    formatDateTime: function (fmt, day) {
      var that = day ? day : new Date();
      var o = {
        "M+": that.getMonth() + 1,                 //月份
        "d+": that.getDate(),                    //日
        "h+": that.getHours(),                   //小时
        "m+": that.getMinutes(),                 //分
        "s+": that.getSeconds(),                 //秒
        "q+": Math.floor((that.getMonth() + 3) / 3), //季度
        "S": that.getMilliseconds()             //毫秒
      };
      if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (that.getFullYear() + "").substr(4 - RegExp.$1.length));
      }

      for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
          fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
      }

      return fmt;
    },

    /**
     * 删除数组中的空值
     **/
    delNullInArray: function (arr) {
      var res = [],
          j = 0;
      for (var i = 0; i < arr.length; i++) {
        if (arr[i] !== '') {
          res[j] = arr[i];
          ++j;
        }
      }
      return res;
    },

    /**
     * 去除数字前面的0
     **/
    trimZero: function (str) {
      var _index = -1;

      for (var i = 0; i < str.length; i++) {
        if (str[i] === '0') {
          _index = i;
        } else {
          break;
        }
      }

      if (_index < 0) {
        return str;
      }

      if (str.length > 1) {
        if (tool.isAllZero(str)) {
          return '0';
        } else {
          return str.slice(_index + 1);
        }
      } else {
        return str.slice(_index);
      }

    },

    /**
     * 判断字符串是否全0
     **/
    isAllZero: function (str) {
      for (var i = 0; i < str.length; i++) {
        if (str[i] !== 0) {
          return false;
        }
      }
      return true;
    },

    /**
     * 防注入
     **/
    escapeChars: function (str) {
      str = str.replace(/&/g, '&amp;');
      str = str.replace(/</g, '&lt;');
      str = str.replace(/>/g, '&gt;');
      str = str.replace(/'/g, '&acute;');
      str = str.replace(/"/g, '&quot;');
      str = str.replace(/\|/g, '&brvbar;');
      str = str.replace(/{/g, '&#123;');
      str = str.replace(/}/g, '&#125;');
      return str;
    },

    /**
     * 数据解析
     *
     * **/
    dataResolve: function (data) {
      var resolve = [];

      if (!this.isObject(data)) {
        return resolve;
      }

      for (var d in data) {
        var obj = {};

        obj.key = d;

        if (this.isObject(data[d])) {
          obj.value = this.dataResolve(data[d]);
        } else {
          obj.value = data[d];
        }

        resolve.push(obj);
      }

      return resolve;
    },

    /**
     * 数组转chart数据
     *
     * **/
    dataResolveChart: function (data, type) {
      var xAxisData = [], series = [];

      for (var i = 0, len = data.length; i < len; i++) {
        var obj = {};


        obj.name = dataApp.pieChartTitle[data[i].key];
        obj.type = type;
        obj.data = [];

        data[i].value.map(function (d) {
          if (i === 0) {
            xAxisData.push(d.key);
          }
          obj.data.push(d.value);
        });

        series.push(obj);
      }

      return {xAxis: {data: xAxisData}, series: series};
    },

    /**
     * 返回状态图标和文字
     *
     * **/
    getStateIconText: function (state) {
      switch (state + '') {
        case '0':
          return '<i class="text-success aidicon aidicon-checkbox-marked-circle vertical-align-middle margin-right-small-3"></i>正常';
          break;
        case '1':
          return '<i class="text-warning aidicon aidicon-alert-circle vertical-align-middle margin-right-small-3"></i>警告';
          break;
        case '2':
          return '<i class="text-danger aidicon aidicon-close-circle vertical-align-middle margin-right-small-3"></i>严重';
          break;
        default:
          return '<i class="text-success aidicon aidicon-checkbox-marked-circle vertical-align-middle margin-right-small-3"></i>正常';
      }
    }
  };
  return tool;
});