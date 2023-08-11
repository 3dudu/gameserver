package com.xuegao.LoginServer.po;

import java.util.Calendar;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.DBManager;
import com.xuegao.LoginServer.redisData.MemLoginTokenBean;
import com.xuegao.LoginServer.redisData.MemUserTokenBean;
import com.xuegao.LoginServer.vo.IdPlatform;
import com.xuegao.LoginServer.vo.UserStatu;
import com.xuegao.core.db.po.BasePo;
import com.xuegao.core.db.po.annotation.Column;
import com.xuegao.core.db.po.annotation.Table;
import com.xuegao.core.util.StringUtil;

@Table(proxoolAlias="proxool.GateWay",tableName="user")
public class UserPo extends BasePo{

	@Column
	private String name;//账号
	@Column
	private String passwd;//MD5(passwd)
	@Column
	private Date create_time;//注册时间
	@Column
	private String pf_user;//平台账号(平台创建账号时需要随机创建一个 name和passwd)
	@Column
	private String pf;//所属平台
	@Column
	private String mail_user;//邮箱账号
	@Column
	private Date bind_time;//绑定邮箱的时间
	@Column
	private String channelCode;//channelCode
	@Column
	private String lastLoginDate;//最近一次登录的日期
	@Column
	private String idCard;//身份证号
	@Column
	private String realName; //姓名
	@Column
	private Integer userStatus;; //用户状态0-游客 1-非游客
	@Column
	private int identety_status;; //实名认证状态 0:认证成功,1:认证中
	@Column
	private String pi;; //实名唯一标识
	@Column
	private boolean loginOff;//注销状态 0 未注销 1注销

	private String ext; //额外字段


	public String getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(String lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public String getPf_user() {
		return pf_user;
	}
	public void setPf_user(String pf_user) {
		this.pf_user = pf_user;
	}
	public String getPf() {
		return pf;
	}
	public void setPf(String pf) {
		this.pf = pf;
	}
	public String getMail_user() {
		return mail_user;
	}
	public void setMail_user(String mail_user) {
		this.mail_user = mail_user;
	}
	public Date getBind_time() {
		return bind_time;
	}
	public void setBind_time(Date bind_time) {
		this.bind_time = bind_time;
	}
	public String getChannelCode() {
		return channelCode;
	}
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public Integer getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(Integer userStatus) {
		this.userStatus = userStatus;
	}
	public int getIdentety_status() {
		return identety_status;
	}
	public void setIdentety_status(int identety_status) {
		this.identety_status = identety_status;
	}
	public String getPi() {
		return pi;
	}
	public void setPi(String pi) {
		this.pi = pi;
	}
	public boolean getLoginOff(){
		return loginOff;
	}
	public void setLoginOff(boolean loginOff){
		this.loginOff =loginOff;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}

	/**
	 * 通过第三方用户id查询UserPo
	 * @param pfUid
	 * @param pf
	 * @return
	 */
	public static UserPo findByPfUid(String pfUid,String pf) {
		String sql="select * from user where pf_user = ? and pf = ?";
		final JSONArray array = DBManager.getDB().queryForList(sql, pfUid,pf);
		return  array.size()>0 ?  JSON.toJavaObject(array.getJSONObject(0),UserPo.class) : new UserPo();
	}
	/**
	 * 通过用户名获得UserPo
	 * @param uname
	 * @return
	 */
	public static UserPo findByUname(String uname){
		JSONObject object=DBManager.getDB().queryForBean("select * from user where name=?", uname);
		return JSON.toJavaObject(object, UserPo.class);
	}
	/**
	 * 通过用户名获得UserPo
	 * @param uid
	 * @return
	 */
	public static UserPo findByUId(Long uid){
		JSONObject object=DBManager.getDB().queryForBean("select * from user where id=?", uid);
		return JSON.toJavaObject(object, UserPo.class);
	}
	/**
	 * 设置登录凭证，有效期24小时
	 * @param token
	 */
	public long saveToken(String token){
		//1.清除原token
		Calendar cal = Calendar.getInstance();
		MemUserTokenBean memUserTokenBean=new MemUserTokenBean(this.getId()+""+cal.get(Calendar.YEAR)+cal.get(Calendar.MONTH)+cal.get(Calendar.DAY_OF_MONTH));
		UserStatu userStatu=memUserTokenBean.get();
		MemLoginTokenBean memLoginTokenBean=new MemLoginTokenBean(token);
		IdPlatform idPlatform = memLoginTokenBean.get(); //获取当天用户在线时间
		if(userStatu!=null){
			new MemLoginTokenBean(userStatu.token).clearKey();
		}
		//2.设置新token
		memLoginTokenBean.save(new IdPlatform(this.getId(), this.getPf(),this.getPf_user()));
		memLoginTokenBean.setExpireTime(24*60*60);//设置一天有效期
		memUserTokenBean.save(new UserStatu(token,userStatu == null ? 0:userStatu.time));
		memUserTokenBean.setExpireTime(24*60*60);//设置一天有效期
		return userStatu == null ? 0:userStatu.time;
	}

	/**
	 * 设置登录时间
	 */
	public String logout(long time){
		Calendar cal = Calendar.getInstance();
		MemUserTokenBean memUserTokenBean=new MemUserTokenBean(this.getId()+""+cal.get(Calendar.YEAR)+cal.get(Calendar.MONTH)+cal.get(Calendar.DAY_OF_MONTH));
		UserStatu userStatu=memUserTokenBean.get();
		if (userStatu==null){
			memUserTokenBean.save(new UserStatu(StringUtil.fetchUniqStr_32(),time)); //跨天特殊处理
		}else {
			memUserTokenBean.save(new UserStatu(userStatu.token,userStatu.time+time));
		}
		memUserTokenBean.setExpireTime(24*60*60);//设置一天有效期
		return userStatu.token;
	}
	@Override
	public String toString() {
		return "UserPo [name=" + name + ", passwd=" + passwd + ", create_time=" + create_time + ", pf_user=" + pf_user
				+ ", pf=" + pf + ", mail_user=" + mail_user + ", bind_time=" + bind_time + "]";
	}


	//注册时触发
	public void onRegist(){
//		GlobalCache.threadPool.execute(new Runnable() {
//			@Override
//			public void run() {
//				//注册日期
//				String createDateStr=new SimpleDateFormat("yyyy-MM-dd").format(create_time);
//				//login_total表
//				Long id=DBManager.getDB().queryForLong("select id from `login_total` where `createDate`=? and `platform`=? and `channelCode`=?", createDateStr,pf,channelCode);
//				if(id!=null){
//					String sql="update `login_total` set `day_1` = `day_1` + 1 where `id`=?";
//					DBManager.getDB().execute(sql, id);
//				}else{
//					String sql="insert into `login_total`(`createDate`,`platform`,`channelCode`,`day_1`)values(?,?,?,?)";
//					DBManager.getDB().execute(sql, createDateStr,pf,channelCode,1);
//				}
//				//login_count表
//				id=DBManager.getDB().queryForLong("select id from `login_count` where `createDate`=? and `platform`=? and `channelCode`=?", createDateStr,pf,channelCode);
//				if(id!=null){
//					String sql="update `login_count` set `newcount` = `newcount` + 1 where `id`=?";
//					DBManager.getDB().execute(sql, id);
//				}else{
//					String sql="insert into `login_count`(`createDate`,`platform`,`channelCode`,`newcount`,`actcount`)values(?,?,?,?,?)";
//					DBManager.getDB().execute(sql, createDateStr,pf,channelCode,1,0);
//				}
//			}
//		}, (""+this.pf+this.channelCode).hashCode());
	}
	//登录时触发,加到登录统计表中
	public void onLogin(){
//		final Date nowDate=new Date();
//		GlobalCache.threadPool.execute(new Runnable() {
//			@Override
//			public void run() {
//				//计算当前是注册日期的第几天
//				int diffDays=DateUtil.diffDays(create_time,nowDate);
//				if(diffDays<=0){
//					//当天注册的,直接返回
//					return;
//				}
//				String nowDateStr=new SimpleDateFormat("yyyy-MM-dd").format(nowDate);
//				String sql="select `lastLoginDate` from `user` where `id` = ?";
//				JSONObject data=DBManager.getDB().queryForBean(sql, getId());
//				if(data==null){
//					return;
//				}
//				String lastLoginDateStr=data.getString("lastLoginDate");
//				//如果非今日首次登录则返回
//				if(nowDateStr.equals(lastLoginDateStr)){
//					return;
//				}
//				//修改最近登录日期
//				sql="update `user` set `lastLoginDate` = ? where `id`=?";
//				DBManager.getDB().execute(sql, nowDateStr,getId());
//				//今日首次登录，则更新统计表数据
//				//注册日期
//				String createDateStr=new SimpleDateFormat("yyyy-MM-dd").format(create_time);
//				//login_total表
//				Long id=DBManager.getDB().queryForLong("select id from `login_total` where `createDate`=? and `platform`=? and `channelCode`=?", createDateStr,pf,channelCode);
//				String column=fetchColumn1(diffDays);//待修改的列名
//				if(column==null){
//					return;
//				}
//				if(id!=null){
//					sql="update `login_total` set `"+column+"` = `" + column+"` + 1 where `id`=?";
//					DBManager.getDB().execute(sql, id);
//				}else{
//					sql="insert into `login_total`(`createDate`,`platform`,`channelCode`,`"+column+"`)values(?,?,?,?)";
//					DBManager.getDB().execute(sql, createDateStr,pf,channelCode,1);
//				}
//				//login_count表
//				id=DBManager.getDB().queryForLong("select id from `login_count` where `createDate`=? and `platform`=? and `channelCode`=?", nowDateStr,pf,channelCode);
//				if(id!=null){
//					sql="update `login_count` set `actcount` = `actcount` + 1 where `id`=?";
//					DBManager.getDB().execute(sql, id);
//				}else{
//					sql="insert into `login_count`(`createDate`,`platform`,`channelCode`,`newcount`,`actcount`)values(?,?,?,?,?)";
//					DBManager.getDB().execute(sql, nowDateStr,pf,channelCode,0,1);
//				}
//
//			}
//		}, (""+this.pf+this.channelCode).hashCode());
	}

//	private String fetchColumn1(int diffDay){
//		if(diffDay>=0&&diffDay<=6){
//			return "day_"+(diffDay+1);
//		}else if(diffDay>=7&&diffDay<=13){
//			return "day_14";
//		}else if(diffDay>=14&&diffDay<=20){
//			return "day_21";
//		}else if(diffDay>=21&&diffDay<=29){
//			return "day_30";
//		}else if(diffDay>=30&&diffDay<=59){
//			return "day_60";
//		}else if(diffDay>=60&&diffDay<=179){
//			return "day_180";
//		}else if(diffDay>=180&&diffDay<=359){
//			return "day_360";
//		}
//		return null;
//	}

}
