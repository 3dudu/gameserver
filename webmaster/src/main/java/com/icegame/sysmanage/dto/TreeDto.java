package com.icegame.sysmanage.dto;

/**
 * 鏍戝舰缁撴瀯瀵硅薄鐨勭埗绫�
 * @Description: TODO
 * @Package com.icegame.framework.dto
 * @author chesterccw
 * @date 2018骞�1鏈�15鏃�
 */
public class TreeDto {

	private Long id;
 	private String name;
 	private Long parentId;
 	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
 	
 	
}
