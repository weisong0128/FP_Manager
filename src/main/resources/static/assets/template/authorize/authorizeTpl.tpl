<div class="form-horizon">
    <form id="operationAuthorizeForm" class="form-validate-dom">
        <div class="form-group form-row clearfix">
            <label class="form-label tag" for="name"><i>*</i>项目名称</label>
            <input type="text" class="form-control tag-content" id="name" name="name" required="" value = "{{$data ? $data.projectName:''}}" placeholder="如：标签系统" />
        </div>
        <div class="form-group form-row clearfix">
            <label class="form-label tag" for="provinces"><i>*</i>项目安装地市</label>
            <div id = "provinces" name="provinces" required=""></div>
        </div>
        <div class="form-group form-row clearfix">
            <label class="form-label tag" for="downloadTime"><i>*</i>下载证书日期</label>
            <input type="text" class="form-control tag-content" id="downloadTime" required="" name="downloadTime" value = "{{$data ? $data.downloadTime:''}}" placeholder="请选择日期" />
            <i class="aidicon aidicon-calendar calendar-icon"></i>
        </div>
        <div class="form-group form-row clearfix">
            <label class="form-label tag" for="officer"><i>*</i>负责人</label>
            <input type="text" class="form-control tag-content" id="officer" required="" name="officer" value = "{{$data ? $data.envirHead:''}}" placeholder="如：张三" />
        </div>
        <div class="form-group form-row clearfix">
            <label class="form-label tag" for="tel"><i>*</i>手机号</label>
            <input type="text" class="form-control tag-content" id="tel" name="tel" value = "{{$data ? $data.phone:''}}" placeholder="请输入手机号码" />
        </div>
        <div class="form-group form-row clearfix">
            <label class="form-label tag" for="mac"><i>*</i>MAC地址</label>
            <input type="text" class="form-control tag-content" id="mac" name="mac" value = "{{$data ? $data.mac:''}}" required="" mac="true" placeholder="如：68:91:D0:60:8F:0E" />
        </div>
        <div class="form-group form-row clearfix">
            <label class="form-label tag" for="ip"><i>*</i>主节点ip</label>
            <input type="text" class="form-control tag-content" id="ip" name="ip" value = "{{$data ? $data.masterIp:''}}" required="" ip="true" placeholder="如：15.80.201.208" />
        </div>
        <div class="form-group form-row clearfix">
            <label class="form-label tag" for="environmentInfo"><i>*</i>环境信息</label>
            <select class="form-control tag-content inline-block" id="environmentInfo"  value = "{{$data ? $data.envirNote:''}}" required="" name="environmentInfo">
            </select>
        </div>
        <div class="form-group form-row clearfix">
            <label class="form-label tag" for="snFile"><i>*</i>对应sn文件</label>
            <input type="text" class="form-control tag-content" id="snFile" name="snFile" required="" value = "{{$data ? $data.snFile:''}}" placeholder="请输入SN文件" />
        </div>
        <div class="form-group form-row clearfix">
            <label class="form-label tag" for="feedback"><i>*</i>授权反馈</label>
            <select class="form-control tag-content inline-block" id="feedback" name="feedback" value = "{{$data ? $data.feedback:''}}" placeholder="请选择">
            </select>
        </div>
        <div class="form-group form-row clearfix">
            <label class="form-label tag" for="remark">备注信息</label>
            <textarea class="form-control tag-content" id="remark" name="remark" maxLength="50" row = "2" placeholder="至多50字">{{$data ? $data.note:''}}</textarea>
        </div>
    </form> 
</div>