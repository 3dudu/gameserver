package com.xuegao.LoginServer.log;

public interface LogConstants {

	/**
	 * 日志类型
	 */
	public enum KindLog {
		addDeviceLog("1001", "新增设备日志"),
		addUserLog("1002", "新增账号日志"),
		deviceLoginLog("1004","设备登录日志"),
		userLoginLog("1005","账号登录日志"),
		;
		private String kindId;
		private String kindName;

		KindLog(String kindId, String kindName) {
			this.kindId = kindId;
			this.kindName = kindName;
		}
		public String getKindId() {
			return kindId;
		}

		public void setKindId(String kindId) {
			this.kindId = kindId;
		}

		public String getKindName() {
			return kindName;
		}

		public void setKindName(String kindName) {
			this.kindName = kindName;
		}
	}


}
