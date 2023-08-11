package com.xuegao.PayServer.po;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.DBManager;
import com.xuegao.core.db.po.BasePo;
import com.xuegao.core.db.po.annotation.Column;

import java.util.Date;

//@Table(proxoolAlias="proxool.LoginServer",tableName="user")
public class UserPo extends BasePo {

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

    @Override
    public String toString() {
        return "UserPo{" +
                "name='" + name + '\'' +
                ", passwd='" + passwd + '\'' +
                ", create_time=" + create_time +
                ", pf_user='" + pf_user + '\'' +
                ", pf='" + pf + '\'' +
                ", mail_user='" + mail_user + '\'' +
                ", bind_time=" + bind_time +
                ", channelCode='" + channelCode + '\'' +
                ", lastLoginDate='" + lastLoginDate + '\'' +
                ", idCard='" + idCard + '\'' +
                ", realName='" + realName + '\'' +
                ", userStatus=" + userStatus +
                '}';
    }

    /**
	 * 通过用户名ID获得UserPo
	 * @param userId
	 * @return
	 */
	public static UserPo findByUserId(long userId){
		JSONObject object=DBManager.getDB().queryForBean("select * from user where id=?", userId);
		return JSON.toJavaObject(object, UserPo.class);
	}


    /**
     * 通过第三方用户id查询UserPo
     * @param pfUid
     * @param pf
     * @return
     */
    public static UserPo findByPfUser(String pfUid,String pf) {
        String sql="select * from user where pf_user = ? and pf = ?";
        final JSONArray array = DBManager.getDB().queryForList(sql, pfUid,pf);
        return  array.size()>0 ?  JSON.toJavaObject(array.getJSONObject(0),UserPo.class) : new UserPo();
    }
}
