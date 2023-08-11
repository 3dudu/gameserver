package com.xuegao.PayServer.po;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.util.HelperJson;
import com.xuegao.core.db.DBWrapper;
import com.xuegao.core.db.po.BasePo;
import com.xuegao.core.db.po.PoInfoVo;
import com.xuegao.core.db.po.annotation.Column;
import com.xuegao.core.db.po.annotation.Table;

//@Table(tableName="pay_total",proxoolAlias="proxool.PayServer")
public class Pay_Statis_Ltv  extends BasePo {

	@Column
	private String   createDate;// '流水记录日期',
	@Column
	private String  platform;//
	@Column
	private int   channel;// 
	@Column
	private float   day_1;  // 记录日期当天的新增付费
	@Column
	private float   day_2;// 记录日期 前一天注册的玩家付费
	@Column
	private float   day_3;// 记录日期 前 第2天注册的玩家付费
	@Column
	private float   day_4;// 记录日期 前 第3天注册的玩家付费
	@Column
	private float   day_5;// 记录日期 前 第4天注册的玩家付费
	@Column
	private float   day_6;// 记录日期 前 第5天注册的玩家付费
	@Column
	private float   day_7;// 记录日期 前 第6天注册的玩家付费
	@Column
	private float   day_14;// 记录日期 前 第8-14天注册的玩家流水都记录在此
	@Column
	private float   day_21;// 记录日期 前 第15-21天注册的玩家流水都记录在此
	@Column
	private float   day_30;// 记录日期 前 第22-30天注册的玩家流水都记录在此
	@Column
	private float   day_60;// 记录日期 前 第31-60天注册的玩家流水都记录在此
	@Column
	private float   day_180;// 记录日期 前 第61-180天注册的玩家流水都记录在此
	@Column
	private float   day_360;// 记录日期 前 第181-360天注册的玩家流水都记录在此
	@Column
	private float   day_other;// 记录日期 前>360天注册的玩家流水都记录在此
	
	

	/**
	 * 根据时间和平台,渠道联合查询
	 * */
	public static Pay_Statis_Ltv findRecordByDateAndPaltformAndChannel(String date,String platform,int channel) {
		long start = System.currentTimeMillis();
		DBWrapper dbWrapper = BasePo.fetchDBWrapper(Pay_Statis_Ltv.class);
		Pay_Statis_Ltv ordPo = JSON.toJavaObject(dbWrapper.queryForBean("select * from pay_statis_ltv where createDate=? and platform=? and channel=?", date,platform,channel),
				Pay_Statis_Ltv.class);
		logger.info("findByOrdId:queryCost:"+(System.currentTimeMillis()-start));
		return ordPo;
	}
	
	/**
	 * 根据时间和平台,渠道联合查询
	 * */
	public static float findAmountTotalByRegDay(Pay_Statis_Ltv ltv,int regDay) {
		long start = System.currentTimeMillis();
		float amountTotal = 0.0f;
		switch(regDay){
		case 1:// 今日注册的 
			amountTotal = ltv.getDay_1();
			break;
		case 2:
			amountTotal = ltv.getDay_2();
			break;
		case 3:
			amountTotal = ltv.getDay_3();
			break;
		case 4:
			amountTotal = ltv.getDay_4();
			break;
		case 5:
			amountTotal = ltv.getDay_5();
			break;
		case 6:
			amountTotal = ltv.getDay_6();
			break;
		case 7:
			amountTotal = ltv.getDay_7();
			break;
		case 14:
			amountTotal = ltv.getDay_14();
			break;
		case 21:
			amountTotal = ltv.getDay_21();
			break;
		case 30:
			amountTotal = ltv.getDay_30();
			break;
		case 60:
			amountTotal = ltv.getDay_60();
			break;
		case 180:
			amountTotal = ltv.getDay_180();
			break;
		case 360:
			amountTotal = ltv.getDay_360();
			break;
		case 361:// >360 都记录这个地方
			amountTotal = ltv.getDay_other();
			break;
		}
		logger.info("findByOrdId:queryCost:"+(System.currentTimeMillis()-start));
		return amountTotal;
	}
	
	
	
	/**
	 * 根据主键 更新 
	 * @param regDay 玩家注册天数
	 * @parma 更新数字 
	 * @param 主键
	 * 
	 * 根据注册天数 来决定 update 语句
	 * */
	public  static int updatePayStatisticsById(int regDay,float updateNum,long priKeyId){
		PoInfoVo poInfoVo=PoInfoVo.getPoInfo(OrderSynchPo.class);
		StringBuffer  updateSql =  new StringBuffer();
		updateSql.append(" update pay_statis_ltv set ");
		switch(regDay){
		case 1:// 今日注册的 
			updateSql.append("`day_1` = ? ");
			break;
		case 2:
			updateSql.append("`day_2` = ? ");
			break;
		case 3:
			updateSql.append("`day_3` = ? ");
			break;
		case 4:
			updateSql.append("`day_4` = ? ");
			break;
		case 5:
			updateSql.append("`day_5` = ? ");
			break;
		case 6:
			updateSql.append("`day_6` = ? ");
			break;
		case 7:
			updateSql.append("`day_7` = ? ");
			break;
		case 14:
			updateSql.append("`day_14` = ? ");
			break;
		case 21:
			updateSql.append("`day_21` = ? ");
			break;
		case 30:
			updateSql.append("`day_30` = ? ");
			break;
		case 60:
			updateSql.append("`day_60` = ? ");
			break;
		case 180:
			updateSql.append("`day_180` = ? ");
			break;
		case 360:
			updateSql.append("`day_360` = ? ");
			break;
		case 361:// >360 都记录这个地方
			updateSql.append("`day_other` = ? ");
			break;
		}
		updateSql.append("  where `id`=? ");
		int rs = DBWrapper.getInstance(poInfoVo.getProxoolAlias()).execute(updateSql.toString(),updateNum,priKeyId);
		
		return rs;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public float getDay_1() {
		return day_1;
	}

	public void setDay_1(float day_1) {
		this.day_1 = day_1;
	}

	public float getDay_2() {
		return day_2;
	}

	public void setDay_2(float day_2) {
		this.day_2 = day_2;
	}

	public float getDay_3() {
		return day_3;
	}

	public void setDay_3(float day_3) {
		this.day_3 = day_3;
	}

	public float getDay_4() {
		return day_4;
	}

	public void setDay_4(float day_4) {
		this.day_4 = day_4;
	}

	public float getDay_5() {
		return day_5;
	}

	public void setDay_5(float day_5) {
		this.day_5 = day_5;
	}

	public float getDay_6() {
		return day_6;
	}

	public void setDay_6(float day_6) {
		this.day_6 = day_6;
	}

	public float getDay_7() {
		return day_7;
	}

	public void setDay_7(float day_7) {
		this.day_7 = day_7;
	}

	public float getDay_14() {
		return day_14;
	}

	public void setDay_14(float day_14) {
		this.day_14 = day_14;
	}

	public float getDay_21() {
		return day_21;
	}

	public void setDay_21(float day_21) {
		this.day_21 = day_21;
	}

	public float getDay_30() {
		return day_30;
	}

	public void setDay_30(float day_30) {
		this.day_30 = day_30;
	}

	public float getDay_60() {
		return day_60;
	}

	public void setDay_60(float day_60) {
		this.day_60 = day_60;
	}

	public float getDay_180() {
		return day_180;
	}

	public void setDay_180(float day_180) {
		this.day_180 = day_180;
	}

	public float getDay_360() {
		return day_360;
	}

	public void setDay_360(float day_360) {
		this.day_360 = day_360;
	}

	public float getDay_other() {
		return day_other;
	}

	public void setDay_other(float day_other) {
		this.day_other = day_other;
	}


	
	
	
	
}
