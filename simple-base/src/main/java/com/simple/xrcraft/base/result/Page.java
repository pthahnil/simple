package com.simple.xrcraft.base.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lixiaorong on 2017/8/30.
 */
public class Page<E> implements Serializable,Cloneable {

	private static final long serialVersionUID = 1L;

	private int pageIndex = 1;

	private int pageSize = 10;

	private int totalPage;
	private int totalCount;

	private int currentResult;
	private List<E> result;

	public Page() {

	}

	public Page(int pageNo, int showCount) {
		this.pageIndex = pageNo + 1;
		this.pageSize = showCount;
	}

	public int getTotalPage() {

		if(this.totalCount % this.pageSize == 0) {
			this.totalPage = this.totalCount / this.pageSize;
		} else {
			this.totalPage = this.totalCount / this.pageSize + 1;
		}

		return this.totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalCount() {
		return this.totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageIndex() {
		if(this.pageIndex <= 0) {
			this.pageIndex = 1;
		}

		if(this.getTotalPage() != 0 && this.pageIndex > this.getTotalPage()) {
			this.pageIndex = this.getTotalPage();
		}

		return this.pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentResult() {
		this.currentResult = (this.getPageIndex() - 1) * this.getPageSize();
		if(this.currentResult < 0) {
			this.currentResult = 0;
		}

		return this.currentResult;
	}

	public void setCurrentResult(int currentResult) {
		this.currentResult = currentResult;
	}

	public List<E> getResult() {
		return this.result;
	}

	public void setResult(List<E> result) {
		this.result = result;
	}
}

