<div class="filter-group filter">
  <div class="filter-header">筛选条件<label class="operate" data-action="showAllFilter"><span id="allFilterText">展开</span><i
      class="aidicon aidicon-chevron-down"></i></label></div>
  <form id="{{$data.module}}" class="filter-body">
    {{if $data.module == "log"}}
    <div class="form-group project-name clearfix">
      <label class="labels">项目名称：</label>
      <label class="radio-inline radio-card">
        <input type="radio" name="projectName">
        <span class="text-label">不限</span>
      </label>
      {{each $data.projectName as projectName}}
      <label class="radio-inline radio-card">
        <input type="radio" name="projectName">
        <span class="text-label">{{projectName}}</span>
      </label>
      {{/each}}
      <btn class="operate btn btn-link">收起<i class="aidicon aidicon-chevron-down"></i></btn>
    </div>
    {{/if}}

    {{if $data.module !== "errorLog" && $data.module !== "errorSqlLog" }}
    <div class="form-group project-location clearfix">
      <label class="labels">项目安装地区：</label>
      <label class="checkbox-inline checkbox-card chooseAll">
        <input type="checkbox" name="projectLocation">
        <span class="text-label">不限</span>
      </label>
      {{each $data.projectLocation as projectLocation}}
      <label class="checkbox-inline checkbox-card">
        <input type="checkbox" name="projectLocation">
        <span class="text-label">{{projectLocation}}</span>
      </label>
      {{/each}}
      <btn class="operate btn btn-link">收起<i class="aidicon aidicon-chevron-down"></i></btn>
    </div>
    {{/if}}

    {{if $data.module == "allSql"}}
    <div class="form-group statement-attribute clearfix">
      <label class="labels">语句属性：</label>
      <label class="checkbox-inline checkbox-card chooseAll">
        <input type="checkbox" name="statementAttribute">
        <span class="text-label">不限</span>
      </label>
      <label class="checkbox-inline checkbox-card">
        <input type="checkbox" name="statementAttribute">
        <span class="text-label">简单语句</span>
      </label>
      <label class="checkbox-inline checkbox-card">
        <input type="checkbox" name="statementAttribute">
        <span class="text-label">复杂语句</span>
      </label>
      <label class="checkbox-inline checkbox-card">
        <input type="checkbox" name="statementAttribute">
        <span class="text-label">其他语句</span>
      </label>
    </div>

    <div class="form-group query-duration clearfix">
      <label class="labels">查询耗时：</label>
      <label class="radio-inline radio-card">
        <input type="radio" name="queryDuration">
        <span class="text-label">不限</span>
      </label>
      <label class="radio-inline radio-card">
        <input type="radio" name="queryDuration">
        <span class="text-label"><=10s</span>
      </label>
      <label class="radio-inline radio-card">
        <input type="radio" name="queryDuration">
        <span class="text-label"><=20s</span>
      </label>
      <label class="radio-inline radio-card">
        <input type="radio" name="queryDuration">
        <span class="text-label">20s~60s</span>
      </label>
      <label class="radio-inline radio-card">
        <input type="radio" name="queryDuration">
        <span class="text-label">>=60s</span>
      </label>
    </div>
    {{/if}}

    {{if $data.module == "errorSql" || $data.module == "errorSqlLog"}}
    <div class="form-group error-sql-type clearfix">
      <label class="labels">不合格sql分类：</label>
      <label class="checkbox-inline checkbox-card">
        <input type="checkbox" name="errorSqlType">
        <span class="text-label">不限</span>
      </label>
      <label class="checkbox-inline checkbox-card">
        <input type="checkbox" name="errorSqlType">
        <span class="text-label">未加limit</span>
      </label>
      <label class="checkbox-inline checkbox-card">
        <input type="checkbox" name="errorSqlType">
        <span class="text-label">limit超限制</span>
      </label>
      <label class="checkbox-inline checkbox-card">
        <input type="checkbox" name="errorSqlType">
        <span class="text-label">使用*</span>
      </label>
      <label class="checkbox-inline checkbox-card">
        <input type="checkbox" name="errorSqlType">
        <span class="text-label">使用%</span>
      </label>
    </div>
    {{/if}}

    {{if $data.module == "errorDetail" || $data.module == "errorLog"}}
    <div class="form-group error-sql-type clearfix">
      <label class="labels">错误级别：</label>
      <label class="radio-inline radio-card">
        <input type="radio" name="errorDetailLevel">
        <span class="text-label">不限</span>
      </label>
      <label class="radio-inline radio-card">
        <input type="radio" name="errorDetailLevel">
        <span class="text-label">重度</span>
      </label>
      <label class="radio-inline radio-card">
        <input type="radio" name="errorDetailLevel">
        <span class="text-label">中度</span>
      </label>
      <label class="radio-inline radio-card">
        <input type="radio" name="errorDetailLevel">
        <span class="text-label">轻度</span>
      </label>
      <label class="radio-inline radio-card">
        <input type="radio" name="errorDetailLevel">
        <span class="text-label">环境状态</span>
      </label>
    </div>
    {{/if}}

    <div class="form-group log-date clearfix">
      <label class="labels">日志日期：</label>
      <label class="radio-inline radio-card">
        <input type="radio" name="logDate">
        <span class="text-label">不限</span>
      </label>
      <label class="radio-inline radio-card">
        <input type="radio" name="logDate">
        <span class="text-label">近一天</span>
      </label>
      <label class="radio-inline radio-card">
        <input type="radio" name="logDate">
        <span class="text-label">近七天</span>
      </label>
      <label class="radio-inline radio-card">
        <input type="radio" name="logDate">
        <span class="text-label">近半个月</span>
      </label>
      <label class="radio-inline radio-card">
        <input type="radio" name="logDate">
        <span class="text-label">近一个月</span>
      </label>
      <label class="radio-inline radio-card">
        <input type="radio" name="logDate">
        <span class="text-label">自定义</span>
      </label>
      <div class="inline-block">
        <input type="text" class="form-control margin-top-small-2" id="customLogDate" placeholder="请输入关键字">
      </div>
    </div>

    {{if $data.module !=="log" && $data.module !=="statistics"}}
    <div class="form-group clearfix">
      <label class="labels">筛选：</label>
      <div class="form-cro-expand no-border clearfix">
        <label for="exampleInputName4">内容搜索</label>
        <div class="form-group">
          <input type="text" class="form-control" id="exampleInputName4" placeholder="请输入关键字">
        </div>
      </div>
    </div>
    {{/if}}
    <div class="operation-buttons">
      <button type="button" class="btn btn-primary" data-action="{{$data.module}}Search">查询</button>
      <button type="button" class="btn btn-default" data-action="{{$data.module}}Reset">重置</button>
    </div>
  </form>
  <!--<div class="filter-more">-->
  <!--<div class="more-wrap" >-->
  <!--<btn class="operates btn btn-link"><i class="aidicon aidicon-chevron-down"></i></btn>-->
  <!--</div>-->
  <!--</div>-->
</div>