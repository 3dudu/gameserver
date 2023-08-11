package com.xuegao.LoginServer.vo;

public class UserStatu {

	public UserStatu() {
		super();
	}

	public UserStatu(String token,Long time) {
		super();
		this.token = token;
		this.time = time;
	}

	/** id */
	public String token;

	/** time */
	public Long time;

}