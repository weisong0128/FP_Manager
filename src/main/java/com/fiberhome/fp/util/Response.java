package com.fiberhome.fp.util;

/**
 * 以JSON数据格式响应Http请求，所有标记为@ResponseBody的Controller方法都必须返回此对象
<pre>
{
  code: 0,
  msg: '操作成功',
  data: []/{},
  page: {pagesize:5, page: 10, count: 1000}
}
</pre>
 * 
 */
public class Response{

	private static final int		CODE_SUCCESS		= 0;
	private static final int		CODE_ERROR			= -1;
	private static final String MSG_SUCCESS			= "操作成功";
	private static final String MSG_ERROR			= "操作失败";

	private int	code = CODE_SUCCESS;

	private String msg;

	private Object data;

	private Page page;

	private Response() {}

	private Response(int code, String msg, Object data, Page page) {
		this.code = code;
		this.msg = msg;
		this.data = data;
		this.page = page;
	}


	//成功接口
	public static Response ok() {
		return new Response(CODE_SUCCESS,MSG_SUCCESS,null,null);
	}

	public static Response ok(Object data) {
		return new Response(CODE_SUCCESS,MSG_SUCCESS,data,null);
	}

	public static Response ok(Object data, Page page) {
		return new Response(CODE_SUCCESS,MSG_SUCCESS,data,page);
	}

	public static Response ok(String message,Object data, Page page) {
		return new Response(CODE_SUCCESS,message,data,page);
	}


	//失败接口
	public static Response error() {
		return new Response(CODE_ERROR,MSG_ERROR,null,null);
	}

	public static Response error(Object data) {
		return new Response(CODE_ERROR,MSG_ERROR,data,null);
	}

	public static Response error(Object data, Page page) {
		return new Response(CODE_ERROR,MSG_ERROR,data,page);
	}

	public static Response error(String message,Object data, Page page) {
		return new Response(CODE_ERROR,message,data,page);
	}


	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

}
