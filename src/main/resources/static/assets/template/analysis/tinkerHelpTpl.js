/*TMODJS:{"version":1,"md5":"98855f36a040bf4fa065fd73d76830ec"}*/
define(["../template"],function(a){return a("analysis/tinkerHelpTpl",function(a){"use strict";var b=this,c=(b.$helpers,b.$escape),d="";return d+='<div class="right-modal-content error-sql"> <div class="title">\u9519\u8bef\u5173\u952e\u8bcd</div> <div class="content">',d+=c(a.errKeyWord),d+='</div> <div class="title">\u9519\u8bef\u539f\u56e0</div> <div class="content">',d+=c(a.reason),d+='</div> <div class="title">\u5904\u7406\u65b9\u6cd5</div> <div class="content">',d+=c(a.solution),d+="</div> </div>",new String(d)})});