<div class="form-horizon">
    <form id="editAbnormalForm" class="form-validate-dom">
    {{each $data as item i }}
      <div class="form-group clearfix" style="margin-bottom:20px;">
          <label class="form-label tar error-label" style="margin-bottom:0px;" for="error{{ i }}"><i>*</i>异常{{ i+1 }}:</label>
          <div>
              <p>{{item.msg}}</p>
              <input type="text" class="form-control" id="error{{ i }}" name="error{{ i }}" style="margin-left:100px;" required="" placeholder="请输入修改内容" />
          </div>
      </div>
    {{/each}}
    </form> 
</div>