package com.icegame.framework.dto;

/**
 * 接受页面传输的分页对象
 * @Description: TODO
 * @Package com.icegame.framework.dto
 * @author chesterccw
 * @date 2018年1月15日
 */
public class PageParam {
	//第几页
	private Integer pageNo=1;
	//每页多少条
	private Integer pageSize=16;
	
	public Integer getPageNo() {
		return pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

}
