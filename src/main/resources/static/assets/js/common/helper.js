template.helper('dateFormat', function (date, format) {

    date = new Date(date);

    var map = {
        "M": date.getMonth() + 1, //月份 
        "d": date.getDate(), //日 
        "h": date.getHours(), //小时 
        "m": date.getMinutes(), //分 
        "s": date.getSeconds(), //秒 
        "q": Math.floor((date.getMonth() + 3) / 3), //季度 
        "S": date.getMilliseconds() //毫秒 
    };
    format = format.replace(/([yMdhmsqS])+/g, function (all, t) {
        var v = map[t];
        if (v !== undefined) {
            if (all.length > 1) {
                v = '0' + v;
                v = v.substr(v.length - 2);
            }
            return v;
        }
        else if (t === 'y') {
            return (date.getFullYear() + '').substr(4 - all.length);
        }
        return all;
    });
    return format;
});

template.helper('format_threshold', function (num) {
    var a;
    if (num > 99999999) {
        a = num / 100000000;
        return a.toFixed(2) + "亿";
    } else if (num > 9999) {
        a = num / 10000;
        return a.toFixed(2) + "万";
    }
    return num;
});

template.helper('format_num', function (num) {
    var a;
    if (num > 99999999) {
        a = num / 100000000;
        return a.toFixed(0) + "亿";
    }
    return num;
});

template.helper('format_system_style', function (status) {
    var systemStatus = status === 0 ? 'system-normal' : status === 1 ? 'system-warnning' : 'system-error';
    return systemStatus;
});

template.helper('format_program_status', function (status) {
    var programStatus = status === 0 ? '正常' : status === 1 ? '警告' : '严重';
    return programStatus;
});

template.helper('format_program_style', function (status) {
    var programStatus = status === 0 ? 'state-normal' : status === 1 ? 'state-warnning' : 'state-error';
    return programStatus;
});

template.helper('format_module_style', function (status) {
    var programStatus = status === 0 ? 'text-success' : status === 1 ? 'text-warnning' : 'text-danger';
    return programStatus;
});

template.helper('format_data_status', function (status) {
    var programStatus = status === 0 ? '正常' : '异常';
    return programStatus;
});

template.helper('format_data_style', function (status) {
    var programStatus = status === 0 ? 'text-success' : 'text-danger';
    return programStatus;
});

template.helper('progress_state', function (status) {
    var progressState = status === 0 ? 'progress-bar--success' : status === 1 ? 'progress-bar-warnning' : 'progress-bar-danger';
    return progressState;
});

template.helper('chart_title', function (key) {
    var pieChartTitle = require('/avop/assets/js/common/data.js').pieChartTitle;
    return pieChartTitle[key];
});