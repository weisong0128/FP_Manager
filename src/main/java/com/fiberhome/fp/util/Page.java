package com.fiberhome.fp.util;

import java.io.Serializable;

/**
 * 分页对象
 * @date 2017-6-21上午10:02:12
 */
public class Page implements Serializable{
	private static final long serialVersionUID = 1L;

	private int totalRows;
	private int pageSize;
	private int pageNo;

	private static  final int NUMBER_20=20;
	public static Page createNotPaging() {
		return new Page(-1, -1, -1);
	}

	public Page() {
		this.totalRows = 0;
		this.pageSize = NUMBER_20;
		this.pageNo = 1;
	}

	public Page(int pn, int ps) {
		this.totalRows = 0;
		this.pageSize = ps;
		this.pageNo = pn;
	}

	public Page(int pn, int ps, int tr) {
		this.totalRows = tr;
		this.pageSize = ps;
		this.pageNo = pn;
	}

	public int getTotalRows() {
		return this.totalRows;
	}

	public void setTotalRows(Integer totalRows) {
		this.totalRows = totalRows == null ? 0 : totalRows.intValue();
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setRows(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return this.pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public void setPage(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getRowStart() {
		return (this.pageNo > 1 ? this.pageNo - 1 : 0) * this.pageSize;
	}

	public int getRowEnd() {
		return (this.pageNo > 1 ? this.pageNo : 1) * this.pageSize;
	}
}
