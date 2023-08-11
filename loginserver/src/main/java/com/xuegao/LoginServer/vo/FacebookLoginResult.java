package com.xuegao.LoginServer.vo;

public class FacebookLoginResult {
	private SuccessDate data;
	
	public SuccessDate getData() {
		return data;
	}

	public void setData(SuccessDate data) {
		this.data = data;
	}

	public class SuccessDate{
		private String app_id;
		private boolean is_valid;
		private String application;
		private String type;
		private String user_id;
		private String issued_at;
		private String expires_at;
		private String data_access_expires_at;
		public String getApp_id() {
			return app_id;
		}
		public void setApp_id(String app_id) {
			this.app_id = app_id;
		}
		public boolean isIs_valid() {
			return is_valid;
		}
		public void setIs_valid(boolean is_valid) {
			this.is_valid = is_valid;
		}
		public String getApplication() {
			return application;
		}
		public void setApplication(String application) {
			this.application = application;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getUser_id() {
			return user_id;
		}
		public void setUser_id(String user_id) {
			this.user_id = user_id;
		}
		public String getIssued_at() {
			return issued_at;
		}
		public void setIssued_at(String issued_at) {
			this.issued_at = issued_at;
		}
		public String getExpires_at() {
			return expires_at;
		}
		public void setExpires_at(String expires_at) {
			this.expires_at = expires_at;
		}
		public String getData_access_expires_at() {
			return data_access_expires_at;
		}
		public void setData_access_expires_at(String data_access_expires_at) {
			this.data_access_expires_at = data_access_expires_at;
		}
		
		
	}
	
}
