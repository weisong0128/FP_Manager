<div class="form-horizon">
    <form id="operationUserForm" class="form-validate-dom">
        <div class="form-group form-row clearfix">
            <label class="form-label" for="account"><i>*</i>账号</label>
            <input type="text" class="form-control" id="account" name="account" value = "{{$data ? $data.account:''}}" {{if $data }}disabled="disabled" {{/if}} placeholder="请输入11位手机号" />
        </div>
        <div class="form-group form-row clearfix">
            <label class="form-label" for="userName"><i>*</i>用户名</label>
            <input type="text" class="form-control" id="userName" name="userName" value = "{{$data ? $data.userName:''}}" placeholder="用户名为3位及以上，支持字母、数字、下划线" />
        </div>
        <div class="form-group form-row clearfix">
            <label class="form-label" for="role">角色</label>
            <select class="form-control" id="role" name="role" value = "{{$data ? $data.role:''}}">
            </select>
        </div>  
        <div class="form-group form-row clearfix">
            <label class="form-label" for="password"><i>*</i>密码</label>
            <input type="text" class="form-control" id="password" name="password" value = "{{$data ? $data.password:''}}" placeholder="请输入6-16位密码，支持字母、数字、下划线" />
        </div>
        <div class="form-group form-row clearfix">
            <label class="form-label" for="confirmPassword"><i>*</i>确认密码</label>
            <input type="text" class="form-control" id="confirmPassword" name="confirmPassword" value = "{{$data ? $data.password:''}}" placeholder="请再次输入密码" />
        </div>
    </form> 
</div>