package com.xuegao.LoginServer.vo;

public class IdPlatform {
	
	public IdPlatform() {
		super();
	}

	public IdPlatform(Long id, String platform ,String  pf_user) {
		super();
		this.id = id;
		this.platform = platform;
		this.pf_user = pf_user;
	}

	/** id */
	public Long id;

	/** platform */
	public String platform;

	/** pf_user */
	public String pf_user;

}