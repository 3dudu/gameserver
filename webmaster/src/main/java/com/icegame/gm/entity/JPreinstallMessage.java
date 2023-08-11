package com.icegame.gm.entity;

/**
 * 预设消息
 * @Package: com.icegame.gm.entity 
 * @author: chsterccw   
 * @date: 2018年9月29日 下午8:27:04
 */
public class JPreinstallMessage {
	
	private Long id;
	
	private String content;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getcontent() {
		return content;
	}

	public void setcontent(String content) {
		this.content = content;
	}
	
	public JPreinstallMessage(){
		
	}

	public JPreinstallMessage(String content) {
		super();
		this.content = content;
	}

}
