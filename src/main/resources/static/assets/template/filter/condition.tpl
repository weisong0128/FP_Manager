{{if $data != "" && $data.filterLength != 0}}
{{if $data.pjName}}
{{if $data.pjName.length>0}}
{{each $data.pjName as pjName index}}
{{if pjName.value != "all"}}
<span class="label label-default" title="项目名称" data-value="{{pjName.value}}" data-type="pjName">
  {{pjName.text}}
  <span class="aidicon aidicon-close-line" data-action="closeFilterItem"></span>
</span>
{{/if}}
{{/each}}
{{/if}}
{{/if}}

{{if $data.pjLocation}}
{{if $data.pjLocation.length>0}}
{{each $data.pjLocation as pjLocation index}}
{{if pjLocation.value != "all"}}
<span class="label label-default" title="安装地区" data-value="{{pjLocation.value}}" data-type="pjLocation">
  {{pjLocation.text}}
  <span class="aidicon aidicon-close-line" data-action="closeFilterItem"></span>
</span>
{{/if}}
{{/each}}
{{/if}}
{{/if}}

{{if $data.errorLevel}}
{{if $data.errorLevel.length>0}}
<span class="label label-default" title="错误级别" data-type="errorLevel">
  {{each $data.errorLevel as errorLevel index}}
  {{if errorLevel.value != "all"}}
  {{errorLevel.text}}
  {{if $data.errorLevel.length != (index + 1)}}、{{/if}}
  {{/if}}
  {{/each}}
  <span class="aidicon aidicon-close-line" data-action="closeFilterItem"></span>
</span>
{{/if}}
{{/if}}

{{if $data.tag}}
{{if $data.tag.length>0}}
<span class="label label-default" title="语句属性" data-type="tag">
  {{each $data.tag as tag index}}
  {{if tag.value != "all"}}
  {{tag.text}}
  {{if $data.tag.length != (index + 1)}}、{{/if}}
  {{/if}}
  {{/each}}
  <span class="aidicon aidicon-close-line" data-action="closeFilterItem"></span>
</span>
{{/if}}
{{/if}}

{{if $data.duration}}
{{each $data.duration as duration}}
<span class="label label-default" title="查询耗时" data-value="{{duration.value}}"
      data-type="duration">{{duration.text}}
  <span class="aidicon aidicon-close-line" data-action="closeFilterItem"></span>
</span>
{{/each}}
{{/if}}

{{if $data.errorSqlType}}
{{if $data.errorSqlType.length>0}}
<span class="label label-default" title="不合格sql分类" data-type="errorSqlType">
  {{each $data.errorSqlType as errorSqlType index}}
  {{if errorSqlType.value != "all"}}
  {{errorSqlType.text}}
  {{if $data.errorSqlType.length != (index + 1)}}、{{/if}}
  {{/if}}
  {{/each}}
  <span class="aidicon aidicon-close-line" data-action="closeFilterItem"></span>
</span>
{{/if}}
{{/if}}

{{if $data.environment}}
{{each $data.environment as environment}}
<span class="label label-default" title="环境信息" data-value="{{environment.value}}"
      data-type="environment">{{environment.text}}
  <span class="aidicon aidicon-close-line" data-action="closeFilterItem"></span>
</span>
{{/each}}
{{/if}}

{{if $data.feedback}}
{{each $data.feedback as feedback}}
<span class="label label-default" title="授权反馈" data-value="{{feedback.value}}"
      data-type="feedback">{{feedback.text}}
  <span class="aidicon aidicon-close-line" data-action="closeFilterItem"></span>
</span>
{{/each}}
{{/if}}

{{if $data.role}}
{{each $data.role as role}}
<span class="label label-default" title="角色" data-value="{{role.value}}"
      data-type="role">{{role.text}}
  <span class="aidicon aidicon-close-line" data-action="closeFilterItem"></span>
</span>
{{/each}}
{{/if}}

{{if $data.state}}
{{each $data.state as state}}
<span class="label label-default" title="状态" data-value="{{state.value}}"
      data-type="state">{{state.text}}
  <span class="aidicon aidicon-close-line" data-action="closeFilterItem"></span>
</span>
{{/each}}
{{/if}}

{{if $data.searchTime}}
{{each $data.searchTime as searchTime}}
<span class="label label-default" title="日志日期" data-value="{{searchTime.value}}"
      data-type="searchTime">{{searchTime.text}}
  <span class="aidicon aidicon-close-line" data-action="closeFilterItem"></span>
</span>
{{/each}}
{{/if}}

{{else}}
<span class="no-condition">暂无条件</span>
{{/if}}