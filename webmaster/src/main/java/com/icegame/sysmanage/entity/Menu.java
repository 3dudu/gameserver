package com.icegame.sysmanage.entity;

import com.icegame.sysmanage.dto.TreeDto;

/**
 * 功能菜单表实体类
 * 
 * @Description: TODO
 * @Package com.icegame.sysmanage.entity
 * @author chesterccw
 * @date 2018年1月22日
 */
public class Menu extends TreeDto implements java.io.Serializable {

	private static final long serialVersionUID = -5177134212031885106L;

	private String parentName;
	private Integer sort;
	private String href;
	private String target;
	private String icon;
	private String isShow;
	private String todo;

	public Menu() {
		
	}
	
	
	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}


	public String getTodo() {
		return todo;
	}


	public void setTodo(String todo) {
		this.todo = todo;
	}
	
	
}