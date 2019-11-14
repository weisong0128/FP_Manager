/*TMODJS:{"version":1,"md5":"1ccef8de94786049c66ef6ae3588f762"}*/
define(["../template"],function(a){return a("filter/index_back",function(a){"use strict";var b=this,c=(b.$helpers,b.$escape),d=b.$each,e=(a.projectName,a.$index,a.projectLocation,"");return e+='<div class="filter-group filter"> <div class="filter-header">\u7b5b\u9009\u6761\u4ef6<label class="operate" data-action="showAllFilter"><span id="allFilterText">\u5c55\u5f00</span><i class="aidicon aidicon-chevron-down"></i></label></div> <form id="',e+=c(a.module),e+='" class="filter-body"> ',"log"==a.module&&(e+=' <div class="form-group project-name clearfix"> <label class="labels">\u9879\u76ee\u540d\u79f0\uff1a</label> <label class="radio-inline radio-card"> <input type="radio" name="projectName"> <span class="text-label">\u4e0d\u9650</span> </label> ',d(a.projectName,function(a){e+=' <label class="radio-inline radio-card"> <input type="radio" name="projectName"> <span class="text-label">',e+=c(a),e+="</span> </label> "}),e+=' <btn class="operate btn btn-link">\u6536\u8d77<i class="aidicon aidicon-chevron-down"></i></btn> </div> '),e+=" ","errorLog"!==a.module&&"errorSqlLog"!==a.module&&(e+=' <div class="form-group project-location clearfix"> <label class="labels">\u9879\u76ee\u5b89\u88c5\u5730\u533a\uff1a</label> <label class="checkbox-inline checkbox-card chooseAll"> <input type="checkbox" name="projectLocation"> <span class="text-label">\u4e0d\u9650</span> </label> ',d(a.projectLocation,function(a){e+=' <label class="checkbox-inline checkbox-card"> <input type="checkbox" name="projectLocation"> <span class="text-label">',e+=c(a),e+="</span> </label> "}),e+=' <btn class="operate btn btn-link">\u6536\u8d77<i class="aidicon aidicon-chevron-down"></i></btn> </div> '),e+=" ","allSql"==a.module&&(e+=' <div class="form-group statement-attribute clearfix"> <label class="labels">\u8bed\u53e5\u5c5e\u6027\uff1a</label> <label class="checkbox-inline checkbox-card chooseAll"> <input type="checkbox" name="statementAttribute"> <span class="text-label">\u4e0d\u9650</span> </label> <label class="checkbox-inline checkbox-card"> <input type="checkbox" name="statementAttribute"> <span class="text-label">\u7b80\u5355\u8bed\u53e5</span> </label> <label class="checkbox-inline checkbox-card"> <input type="checkbox" name="statementAttribute"> <span class="text-label">\u590d\u6742\u8bed\u53e5</span> </label> <label class="checkbox-inline checkbox-card"> <input type="checkbox" name="statementAttribute"> <span class="text-label">\u5176\u4ed6\u8bed\u53e5</span> </label> </div> <div class="form-group query-duration clearfix"> <label class="labels">\u67e5\u8be2\u8017\u65f6\uff1a</label> <label class="radio-inline radio-card"> <input type="radio" name="queryDuration"> <span class="text-label">\u4e0d\u9650</span> </label> <label class="radio-inline radio-card"> <input type="radio" name="queryDuration"> <span class="text-label"><=10s</span> </label> <label class="radio-inline radio-card"> <input type="radio" name="queryDuration"> <span class="text-label"><=20s</span> </label> <label class="radio-inline radio-card"> <input type="radio" name="queryDuration"> <span class="text-label">20s~60s</span> </label> <label class="radio-inline radio-card"> <input type="radio" name="queryDuration"> <span class="text-label">>=60s</span> </label> </div> '),e+=" ",("errorSql"==a.module||"errorSqlLog"==a.module)&&(e+=' <div class="form-group error-sql-type clearfix"> <label class="labels">\u4e0d\u5408\u683csql\u5206\u7c7b\uff1a</label> <label class="checkbox-inline checkbox-card"> <input type="checkbox" name="errorSqlType"> <span class="text-label">\u4e0d\u9650</span> </label> <label class="checkbox-inline checkbox-card"> <input type="checkbox" name="errorSqlType"> <span class="text-label">\u672a\u52a0limit</span> </label> <label class="checkbox-inline checkbox-card"> <input type="checkbox" name="errorSqlType"> <span class="text-label">limit\u8d85\u9650\u5236</span> </label> <label class="checkbox-inline checkbox-card"> <input type="checkbox" name="errorSqlType"> <span class="text-label">\u4f7f\u7528*</span> </label> <label class="checkbox-inline checkbox-card"> <input type="checkbox" name="errorSqlType"> <span class="text-label">\u4f7f\u7528%</span> </label> </div> '),e+=" ",("errorDetail"==a.module||"errorLog"==a.module)&&(e+=' <div class="form-group error-sql-type clearfix"> <label class="labels">\u9519\u8bef\u7ea7\u522b\uff1a</label> <label class="radio-inline radio-card"> <input type="radio" name="errorDetailLevel"> <span class="text-label">\u4e0d\u9650</span> </label> <label class="radio-inline radio-card"> <input type="radio" name="errorDetailLevel"> <span class="text-label">\u91cd\u5ea6</span> </label> <label class="radio-inline radio-card"> <input type="radio" name="errorDetailLevel"> <span class="text-label">\u4e2d\u5ea6</span> </label> <label class="radio-inline radio-card"> <input type="radio" name="errorDetailLevel"> <span class="text-label">\u8f7b\u5ea6</span> </label> <label class="radio-inline radio-card"> <input type="radio" name="errorDetailLevel"> <span class="text-label">\u73af\u5883\u72b6\u6001</span> </label> </div> '),e+=' <div class="form-group log-date clearfix"> <label class="labels">\u65e5\u5fd7\u65e5\u671f\uff1a</label> <label class="radio-inline radio-card"> <input type="radio" name="logDate"> <span class="text-label">\u4e0d\u9650</span> </label> <label class="radio-inline radio-card"> <input type="radio" name="logDate"> <span class="text-label">\u8fd1\u4e00\u5929</span> </label> <label class="radio-inline radio-card"> <input type="radio" name="logDate"> <span class="text-label">\u8fd1\u4e03\u5929</span> </label> <label class="radio-inline radio-card"> <input type="radio" name="logDate"> <span class="text-label">\u8fd1\u534a\u4e2a\u6708</span> </label> <label class="radio-inline radio-card"> <input type="radio" name="logDate"> <span class="text-label">\u8fd1\u4e00\u4e2a\u6708</span> </label> <label class="radio-inline radio-card"> <input type="radio" name="logDate"> <span class="text-label">\u81ea\u5b9a\u4e49</span> </label> <div class="inline-block"> <input type="text" class="form-control margin-top-small-2" id="customLogDate" placeholder="\u8bf7\u8f93\u5165\u5173\u952e\u5b57"> </div> </div> ',"log"!==a.module&&"statistics"!==a.module&&(e+=' <div class="form-group clearfix"> <label class="labels">\u7b5b\u9009\uff1a</label> <div class="form-cro-expand no-border clearfix"> <label for="exampleInputName4">\u5185\u5bb9\u641c\u7d22</label> <div class="form-group"> <input type="text" class="form-control" id="exampleInputName4" placeholder="\u8bf7\u8f93\u5165\u5173\u952e\u5b57"> </div> </div> </div> '),e+=' <div class="operation-buttons"> <button type="button" class="btn btn-primary" data-action="',e+=c(a.module),e+='Search">\u67e5\u8be2</button> <button type="button" class="btn btn-default" data-action="',e+=c(a.module),e+='Reset">\u91cd\u7f6e</button> </div> </form>      </div>',new String(e)})});