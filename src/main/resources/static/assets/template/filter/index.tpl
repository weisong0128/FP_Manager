<div class="form-cro-expand clearfix">
    <form id="{{$data.module}}" class="form-validate clearfix">

        {{if $data.module !=="log" && $data.module !=="statistics"}}
        <div class="form-group has-feedback">
            <input type="text" class="form-control keyword" data-module="{{$data.module}}"
                   placeholder="{{$data.placeholder}}">
            <span class="aidicon aidicon-magnify font-color-weak form-control-feedback"></span>
        </div>
        {{/if}}

        {{if $data.module == "log" || $data.module == "authorization"}}
        <!--项目名称-->
        <div class="form-group">
            <div class="dropdownSelect" style="width: 150px">
                <div class="pjNameSelect" data-target="#"></div>
                <div class="dropdownSelect-menu" data-type="pjName" data-multiple="true" role="menu"
                     aria-labelledby="dLabel">
                    <div class="dropdownSelect-search">
                        <input type="text" class="form-control" data-action="dropdownSelectSearch"
                               placeholder="请输入安装地址">
                    </div>
                    <div class="dropdownSelect-no-data">
                        <span>未发现数据</span>
                    </div>
                    <ul class="dropdownSelect-menu-list pjNameList">
                        <li class="item all" data-value="all" data-action="selectFilter">
                            <a href="javascript:void(0)">全部</a>
                        </li>
                        {{each $data.pjNames as pjName}}
                        <li class="item" data-value="{{pjName}}" data-action="selectFilter">
                            <a href="javascript:void(0)">{{pjName}}</a>
                        </li>
                        {{/each}}

                    </ul>
                </div>
            </div>
        </div>
        {{/if}}

        {{if $data.module !== "errorLog" && $data.module !== "errorSqlLog" && $data.module !== "user"}}
        <!--安装地区-->
        <div class="form-group">
            <div class="dropdownSelect" style="width: 150px">
                <div class="pjLocationSelect" data-target="#"></div>
                <div class="dropdownSelect-menu" data-type="pjLocation" data-multiple="true" role="menu"
                     aria-labelledby="dLabel">
                    <div class="dropdownSelect-search">
                        <input type="text" class="form-control" data-action="dropdownSelectSearch"
                               placeholder="请输入安装地址">
                    </div>
                    <div class="dropdownSelect-no-data">
                        <span>未发现数据</span>
                    </div>
                    <ul class="dropdownSelect-menu-list pjLocationList">
                        <li class="item all" data-value="all" data-action="selectFilter">
                            <a href="javascript:void(0)">全部</a>
                        </li>
                        {{each $data.pjLocation as pjLocation}}
                        <li class="item" data-value="{{pjLocation}}" data-action="selectFilter">
                            <a href="javascript:void(0)">{{pjLocation}}</a>
                        </li>
                        {{/each}}
                    </ul>
                </div>
            </div>
        </div>
        {{/if}}

        {{if $data.module == "allSql"}}
        <!--语句属性-->
        <div class="form-group">
            <div class="dropdownSelect" style="width: 135px">
                <div class="tagSelect" data-target="#"></div>
                <ul class="dropdownSelect-menu" data-type="tag" data-multiple="true" role="menu"
                    aria-labelledby="dLabel">
                    <li class="item all" data-value="all" data-action="selectFilter">
                        <a href="javascript:void(0)">全部</a>
                    </li>
                    <li class="item" data-value="comp" data-action="selectFilter">
                        <a href="javascript:void(0)">复杂语句</a>
                    </li>
                    <li class="item" data-value="easy" data-action="selectFilter">
                        <a href="javascript:void(0)">简单语句</a>
                    </li>
                    <li class="item" data-value="ins|exp" data-action="selectFilter">
                        <a href="javascript:void(0)">导入导出语句</a>
                    </li>
                    <li class="item" data-value="else" data-action="selectFilter">
                        <a href="javascript:void(0)">其他语句</a>
                    </li>
                </ul>
            </div>
        </div>

        <!--查询时长-->
        <div class="form-group">
            <div class="dropdownSelect" style="width: 120px">
                <div class="durationSelect" data-target="#"></div>
                <ul class="dropdownSelect-menu" data-type="duration" data-multiple="" role="menu"
                    aria-labelledby="dLabel">
                    <!--          <li class="item all" data-value="all" data-action="selectFilter">-->
                    <!--            <a href="javascript:void(0)">全部</a>-->
                    <!--          </li>-->
                    <li class="item" data-value="1" data-action="selectFilter">
                        <a href="javascript:void(0)"><=10s</a>
                    </li>
                    <li class="item" data-value="2" data-action="selectFilter">
                        <a href="javascript:void(0)"><=20s</a>
                    </li>
                    <li class="item" data-value="3" data-action="selectFilter">
                        <a href="javascript:void(0)">20s~60s</a>
                    </li>
                    <li class="item" data-value="4" data-action="selectFilter">
                        <a href="javascript:void(0)">>=60s</a>
                    </li>
                </ul>
            </div>
        </div>
        {{/if}}

        {{if $data.module == "errorSql" || $data.module == "errorSqlLog"}}
        <!--不合格sql分类-->
        <div class="form-group">
            <div class="dropdownSelect" style="width: 150px">
                <div class="errorSqlTypeSelect" data-target="#"></div>
                <ul class="dropdownSelect-menu" data-type="errorSqlType" data-multiple="true" role="menu"
                    aria-labelledby="dLabel">
                    <li class="item all" data-value="all" data-action="selectFilter">
                        <a href="javascript:void(0)">全部</a>
                    </li>
                    <li class="item" data-value="查询未加limit" data-action="selectFilter">
                        <a href="javascript:void(0)">查询未加limit</a>
                    </li>
                    <li class="item" data-value="limit超过30000" data-action="selectFilter">
                        <a href="javascript:void(0)">limit超过30000</a>
                    </li>
                    <li class="item" data-value="使用select *" data-action="selectFilter">
                        <a href="javascript:void(0)">使用select *</a>
                    </li>
                    <li class="item" data-value="使用partition like %" data-action="selectFilter">
                        <a href="javascript:void(0)">使用partition like %</a>
                    </li>
                </ul>
            </div>
        </div>
        {{/if}}

        {{if $data.module == "errorDetail" || $data.module == "errorLog"}}
        <!--错误级别-->
        <div class="form-group">
            <div class="dropdownSelect" style="width: 120px">
                <div class="errorLevelSelect" data-target="#"></div>
                <ul class="dropdownSelect-menu" data-type="errorLevel" data-multiple="true" role="menu"
                    aria-labelledby="dLabel">
                    <li class="item all" data-value="all" data-action="selectFilter">
                        <a href="javascript:void(0)">全部</a>
                    </li>
                    <li class="item" data-value="CRIT" data-action="selectFilter">
                        <a href="javascript:void(0)">重度</a>
                    </li>
                    <li class="item" data-value="ERRO" data-action="selectFilter">
                        <a href="javascript:void(0)">中度</a>
                    </li>
                    <li class="item" data-value="WARN" data-action="selectFilter">
                        <a href="javascript:void(0)">轻度</a>
                    </li>
                    <li class="item" data-value="INFO" data-action="selectFilter">
                        <a href="javascript:void(0)">环境状态</a>
                    </li>
                </ul>
            </div>
        </div>
        {{/if}}

        {{if $data.module == "authorization" }}
        <!--环境信息-->
        <div class="form-group">
            <div class="dropdownSelect" style="width: 140px">
                <div class="environmentSelect" data-target="#"></div>
                <ul class="dropdownSelect-menu" data-type="environment" data-multiple="" role="menu"
                    aria-labelledby="dLabel">
                    <li class="item" data-value="0" data-action="selectFilter">
                        <a href="javascript:void(0)">线上生产环境</a>
                    </li>
                    <li class="item" data-value="1" data-action="selectFilter">
                        <a href="javascript:void(0)">研发测试环境</a>
                    </li>
                    <li class="item" data-value="3" data-action="selectFilter">
                        <a href="javascript:void(0)">已停用</a>
                    </li>
                </ul>
            </div>
        </div>

        <!--授权反馈-->
        <div class="form-group">
            <div class="dropdownSelect" style="width: 120px">
                <div class="feedbackSelect" data-target="#"></div>
                <ul class="dropdownSelect-menu" data-type="feedback" data-multiple="" role="menu"
                    aria-labelledby="dLabel">
                    <li class="item" data-value="1" data-action="selectFilter">
                        <a href="javascript:void(0)">未反馈</a>
                    </li>
                    <li class="item" data-value="0" data-action="selectFilter">
                        <a href="javascript:void(0)">已反馈</a>
                    </li>
                </ul>
            </div>
        </div>

        <!--选择下载证书日期-->
        <div class="form-group clearfix form-icon" style="width: 300px;">
            <input type="text" class="form-control" id="downTimeTag" name="downTimeTag" autocomplete="off"
                   placeholder="下载证书日期">
            <i class="aidicon aidicon-calendar cal-icon"></i>
        </div>
        {{/if}}


        {{if $data.module == "user" }}
        <!--用户角色-->
        <div class="form-group">
            <div class="dropdownSelect" style="width: 140px">
                <div class="roleSelect" data-target="#"></div>
                <ul class="dropdownSelect-menu" data-type="role" data-multiple="" role="menu" aria-labelledby="dLabel">
                    <li class="item" data-value="0" data-action="selectFilter">
                        <a href="javascript:void(0)">管理员</a>
                    </li>
                    <li class="item" data-value="1" data-action="selectFilter">
                        <a href="javascript:void(0)">运维</a>
                    </li>
                    <li class="item" data-value="2" data-action="selectFilter">
                        <a href="javascript:void(0)">普通用户</a>
                    </li>
                </ul>
            </div>
        </div>

        <!--用户状态-->
        <div class="form-group">
            <div class="dropdownSelect" style="width: 140px">
                <div class="stateSelect" data-target="#"></div>
                <ul class="dropdownSelect-menu" data-type="state" data-multiple="" role="menu" aria-labelledby="dLabel">
                    <li class="item" data-value="0" data-action="selectFilter">
                        <a href="javascript:void(0)">启用</a>
                    </li>
                    <li class="item" data-value="1" data-action="selectFilter">
                        <a href="javascript:void(0)">停用</a>
                    </li>
                </ul>
            </div>
        </div>
        {{/if}}


        {{if $data.module != "user" && $data.module != "authorization" && $data.module != "tableUseDetail" }}
        <!--日志日期-->
        <div class="form-group">
            <div class="dropdownSelect" style="width: 120px">
                <div class="timeTagSelect" data-target="#"></div>
                <ul class="dropdownSelect-menu" data-type="searchTime" data-multiple="" role="menu"
                    aria-labelledby="dLabel">
                    {{if $data.module != "statistics"}}
                    <li class="item" data-value="today" data-action="selectFilter">
                        <a href="javascript:void(0)">当天</a>
                    </li>
                    <li class="item" data-value="seven" data-action="selectFilter">
                        <a href="javascript:void(0)">近7天</a>
                    </li>
                    <li class="item" data-value="halfMonth" data-action="selectFilter">
                        <a href="javascript:void(0)">近15天</a>
                    </li>
                    {{else}}
                    <li class="item" data-value="one" data-action="selectFilter">
                        <a href="javascript:void(0)">一个月</a>
                    </li>
                    <li class="item" data-value="three" data-action="selectFilter">
                        <a href="javascript:void(0)">三个月</a>
                    </li>
                    <li class="item" data-value="half" data-action="selectFilter">
                        <a href="javascript:void(0)">半年</a>
                    </li>
                    <li class="item" data-value="year" data-action="selectFilter">
                        <a href="javascript:void(0)">一年</a>
                    </li>
                    {{/if}}
                    <li class="item" data-value="custom" data-action="selectFilter">
                        <a href="javascript:void(0)">自定义</a>
                    </li>
                </ul>
            </div>
        </div>

        <!--自定义日志日期-->
        <div class="form-group clearfix form-icon custom-log-date">
            <input type="text" class="form-control customTimeTag" name="customtimeTag"
                   placeholder="请选择日志日期">
        </div>
        {{/if}}

        <div class="form-group">
            {{if $data.module == "errorDetail" || $data.module == "errorSql" || $data.module == "allSql" || $data.module
            == "errorLog" || $data.module == "errorSqlLog"}}
            <label class="checkbox-beauty margin-horizontal-small-2">
                <input class="isDelDitto" type="checkbox">
                <span class="text-label">去重</span>
            </label>
            {{/if}}
            <button type="button" class="btn btn-primary" data-action="{{$data.module}}Search">查询</button>
            <button type="button" class="btn btn-default" data-action="filterReset">重置</button>
        </div>
    </form>

    <div class="condition-groups">
        <label>已选条件：</label>
        <div class="condition-list"><span class="no-condition">暂无条件</span></div>
    </div>
</div>