    <li class="item all" data-value="all" data-action="selectFilter">
        <a href="javascript:void(0)">全部</a>
    </li>
    {{each $data.list as item}}
    <li class="item" data-value="{{item}}" data-action="selectFilter">
        <a href="javascript:void(0)">{{item}}</a>
    </li>
    {{/each}}