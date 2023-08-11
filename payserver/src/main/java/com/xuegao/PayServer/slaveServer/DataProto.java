package com.xuegao.PayServer.slaveServer;

public class DataProto {

	//  请求-应答
	//  请求-应答
	public static class BaseRQMsg {};

	public static class BaseRSMsg {}

	// 通知-推送
	public static class BaseQMsg {}


	///--------------------------通知-推送 模式----

	//推送看广告信息 给 slave
	public static class Q_Advertisement extends BaseQMsg {
		public Long playerId;
		public int serverId;
		public String tId;  //广告流水ID
		public String rewardId; //奖励ID
		public String sign;// MD5(playerId+serverId+tId+rewardId+SYCHPASSWORD)
		public String platform;

		@Override
		public String toString() {
			return "Q_Advertisement{" +
					"playerId=" + playerId +
					", serverId=" + serverId +
					", tId='" + tId + '\'' +
					", rewardId='" + rewardId + '\'' +
					", sign='" + sign + '\'' +
					", platform='" + platform + '\'' +
					'}';
		}
	}

	//Slave 推送看广告处理结果
	public static class Q_AdvertisementRslt extends BaseQMsg {
		public int status;
		public String msg;
		public String tId;  //广告流水ID
		public Long playerId;
		public int serverId;
		public String rewardId;
	}

	//推送payserver 充值信息 给 slave
	public static class Q_TopUp extends BaseQMsg {
		public Long playerId;
		public int serverId;
		public int proIdx;
		public float proPrice;
		public String ext;
		public String sign;// MD5(playerId+serverId+proIdx+proPrice+ext+SYCHPASSWORD)

		public String platform;
		public String trade_no;
		public String order_type;
		public boolean sanbox;
		public int bonus;

		@Override
		public String toString() {
			return "Q_TopUp{" +
					"playerId=" + playerId +
					", serverId=" + serverId +
					", proIdx=" + proIdx +
					", proPrice=" + proPrice +
					", ext='" + ext + '\'' +
					", sign='" + sign + '\'' +
					", platform='" + platform + '\'' +
					", trade_no='" + trade_no + '\'' +
					", order_type='" + order_type + '\'' +
					", sanbox=" + sanbox +
					", bonus=" + bonus +
					'}';
		}
	}
	//推送payserver  返利信息 给 slave
	public static class Q_Rebate extends BaseQMsg {
		public Long playerId;
		public int serverId;
		public String ext;
		public String sign;// MD5(bonus + playerId+serverId+SYCHPASSWORD)
		public String platform;
		public String trade_no;
		public String order_type;
		public int bonus;

		@Override
		public String toString() {
			return "Q_Rebate{" +
					"playerId=" + playerId +
					", serverId=" + serverId +
					", ext='" + ext + '\'' +
					", sign='" + sign + '\'' +
					", platform='" + platform + '\'' +
					", trade_no='" + trade_no + '\'' +
					", order_type='" + order_type + '\'' +
					", bonus=" + bonus +
					'}';
		}
	}
	//Slave 推送处理结果给 Payserver
	public static class Q_TopUpRslt extends BaseQMsg {
		public int status;
		public String msg ;
		public Long playerId;
		public int serverId;
		public int proIdx;
		public float proPrice;
		public String ext;
		public String trade_no;
		@Override
		public String toString() {
			return "Q_TopUpRslt [status=" + status + ", msg=" + msg + ", playerId=" + playerId + ", serverId="
					+ serverId + ", proIdx=" + proIdx + ", proPrice=" + proPrice + ", ext=" + ext + ", trade_no="
					+ trade_no + "]";
		}


	}

	public static class Q_SendAward extends BaseQMsg {
		public Long playerId;
		public int serverId;
		public String rewardId; // 奖励ID
		public String sign;// MD5(playerId+serverId+rewardId+ext+SYCHPASSWORD)
		public String platform;
		public String ext;
		public int type;
		public  int is_all;//0=否，1=是 2向已有角色发放
		public String title;//邮件标题 不传用默认
		public String content;//邮件内容 不传用默认

		@Override
		public String toString() {
			return "Q_SendAward{" +
					"playerId=" + playerId +
					", serverId=" + serverId +
					", rewardId='" + rewardId + '\'' +
					", sign='" + sign + '\'' +
					", platform='" + platform + '\'' +
					", ext='" + ext + '\'' +
					", type='" + type + '\'' +
					", is_all=" + is_all +
					", title='" + title + '\'' +
					", content='" + content + '\'' +
					'}';
		}
	}
	// Slave 推送发送奖励处理结果
	public static class Q_SendAwardRslt extends BaseQMsg {
		public int status;
		public String msg;
		public Long playerId;
		public int serverId;
		public String rewardId;

	}

	///---------------------------请求-响应 模式----------

	//充值回调使用 请求
	public static class RQ_TopUp extends BaseRQMsg {
		public Long playerId;
		public int serverId;
		public int proIdx;
		public float proPrice;
		public String ext;
		public String sign;// MD5(playerId+serverId+proIdx+proPrice+ext+SYCHPASSWORD)

		public String platform;
		public String trade_no;
		@Override
		public String toString() {
			return "RQ_TopUp [playerId=" + playerId + ", serverId=" + serverId + ", proIdx=" + proIdx + ", proPrice="
					+ proPrice + ", ext=" + ext + ", sign=" + sign + ", platform=" + platform + ", trade_no=" + trade_no
					+ "]";
		}

	}
	//充值回调使用 响应
	public static class RS_TopUp extends BaseRSMsg {
		public int status;
		public String msg ;
		public Long playerId;
		public int serverId;
		public int proIdx;
		public float proPrice;
		public String ext;
		public String trade_no;
	}
}
