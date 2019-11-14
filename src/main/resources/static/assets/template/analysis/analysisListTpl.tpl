{{each $data as project}}
<div class="analysis-card">
  <div class="panel" data-pjName="{{project.pjName}}" data-action="analysisDetail">
    <div class="panel-heading">
      <span class="icon-analysis"></span>
      <h3 class="panel-title">{{project.pjName}}</h3>
    </div>
    <div class="panel-body">
      <ul>
        {{each project.pjLocationList as pjLocationList}}
        <li class="ellipsis" title="{{pjLocationList}}">{{pjLocationList}}</li>
        {{/each}}
      </ul>
    </div>
  </div>
</div>
{{/each}}