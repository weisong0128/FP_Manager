<div class="navbar-container updown">
  <nav class="navbar navbar-basic navbar-dark">
    <span class="navbar-icon"></span>
    <ul class="nav nav-toggle-tabs nav-tabs-lg">
      {{each $data as page}}
      {{if page.show}}
      <li class="nav-top {{if page.active}}active{{/if}}" data-action="chooseMenuEvent"><a href="javascript:void(0)">{{page.name}}</a>
      </li>
      {{/if}}
      {{/each}}
    </ul>
    <div class="navbar-infos-content">
      <span class="info-item">{{$data.userName}}</span>
      <span class="info-item login-out" data-action="loginOut">退出</span>
    </div>
  </nav>
</div>