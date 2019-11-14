<div class="form-horizon" style="padding-bottom: 0px;">
    <form id="createCheckForm" class="form-validate-dom">
        <div class="form-group form-row clearfix">
            <label class="form-label tag" for="pjName"><i>*</i>项目名称</label>
            <select class="form-control" style="width: calc(100% - 115px);" id="pjName" name="pjName" value = "">
            </select>
        </div>
        <div class="form-group form-row clearfix">
            <label class="form-label tag" for="pjLocation"><i>*</i>项目安装地市</label>
            <input type="text" class="form-control tag-content" id="pjLocation" name="pjLocation" value = "" placeholder="如：江苏南京" />
        </div>
        <div class="form-group form-row clearfix" style="margin: 0px !important;">
            <label class="form-label tag" for="uploadLog"><i>*</i>上传日志</label>
            <div id="uploadLog" class="form-control  tag-content dragUpload" required>
                <i id="choseFile" class="aidicon aidicon-inbox"></i>
                <div class="dragUpload-dragInfo">点击或将文件拖拽到这里上传</div>
                <div class="dragUpload-suffixInfo">支持扩展名： cl.log*，文件最大不超过6G，支持批量上传最多10个</div>
            </div>
            <div class="upload-list">
                <div class="del-all" data-action="delete-all">全部删除</div>
                <div id="filesList"></div>
            </div>
        </div>
    </form>
</div>