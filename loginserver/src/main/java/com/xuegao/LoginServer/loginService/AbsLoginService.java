package com.xuegao.LoginServer.loginService;

import java.util.Date;

import com.xuegao.LoginServer.log.ElkLog;
import com.xuegao.LoginServer.log.LogConstants;
import com.xuegao.LoginServer.log.SLogService;
import com.xuegao.core.redis.JedisUtil;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants.PlatformOption;
import com.xuegao.LoginServer.data.Constants.PlatformOption.AnySdk;
import com.xuegao.LoginServer.data.Constants.PlatformOption.Facebook;
import com.xuegao.LoginServer.data.DBManager;
import com.xuegao.LoginServer.data.GlobalCache;
import com.xuegao.LoginServer.po.PfOptionPo;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.LoginServer.redisData.MemPfOptionBean;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.SnowflakeIdWorker;

public abstract class AbsLoginService {
	public static Logger logger=Logger.getLogger(AbsLoginService.class);
	private String platformName;
	private Object config;

	public AbsLoginService() {
		super();
	}

	public abstract UserPo loginUser(String[] parameters,JSONObject rs);

	@SuppressWarnings("unchecked")
	public <T>T getConfig(){
        try {
            MemPfOptionBean poRedisBean= new MemPfOptionBean(getPlatformName());
            PfOptionPo po=poRedisBean.get();
            if(null==po){
                po = PfOptionPo.findEntityByKeyFromDB(getPlatformName());
            }
            String className=PlatformOption.class.getName()+"$"+getPlatformName();
            Class<?> clazz=Class.forName(className);
            if(po!=null&&clazz!=null){
                config=JSON.parseObject(po.getValue(), clazz);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return (T)config;
    }


	public String getPlatformName() {
		if(platformName==null){
			platformName=GlobalCache.fetchPfNameByLoginService(this);
		}
		return platformName;
	}
	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}
	//平台验证成功后，根据平台的uid获得用户对象，不存在则创建
	public UserPo openUserBase(String uid,String pfName,String channel){
		UserPo userPo=null;
		if(Facebook.class.getSimpleName().equals(pfName)||AnySdk.class.getSimpleName().equals(pfName)){
			String sql="select * from user where pf_user = ?";
			userPo=JSON.toJavaObject(DBManager.getDB().queryForBean(sql, uid),UserPo.class);
		}else{
			String sql="select * from user where pf_user = ? and pf = ?";
			userPo=JSON.toJavaObject(DBManager.getDB().queryForBean(sql, uid, pfName),UserPo.class);
		}
		if(userPo==null){
            JedisUtil redis = JedisUtil.getInstance("LoginServer");
            long isSaveSuc = redis.STRINGS.setnx(uid, uid);
            if (isSaveSuc == 0L) {
                logger.info("同一uid多次请求,无须重复处理:uid:"+uid);
                return userPo;
            }
			//创建平台账号
			//平台创建账号时需要随机创建一个 name和passwd
			userPo=new UserPo();
			userPo.setCreate_time(new Date());
			userPo.setPf(pfName);
			userPo.setPf_user(uid);
			userPo.setIdentety_status(2);
			//Long userId=userPo.insertToDBAndGetId();
			Long userId= SnowflakeIdWorker.getInstance().nextId();
			userPo.setId(userId);
			userPo.setName("p"+Long.toHexString(userId));
			userPo.setPasswd(Misc.md5("123456"));
			if(channel==null){
				channel="0";
			}
			userPo.setChannelCode(channel);
			//userPo.updateToDB_WithSync();
			userPo.insertToDB();
			//清除缓存
            redis.STRINGS.zrem(uid);
			userPo.onRegist();
			ElkLog log = new ElkLog(LogConstants.KindLog.addUserLog,userPo);
			log.setUid(userPo.getId() == null ? "0" : userPo.getId().toString());
			SLogService.new_log_elk((JSONObject)JSON.toJSON(log));
		} else{
			userPo.onLogin();
		}
		return userPo;
	}

    public UserPo insertHasEmailUserPo(String uid,String pfName,String channel,String email){
        UserPo userPo=null;
        if(Facebook.class.getSimpleName().equals(pfName)||AnySdk.class.getSimpleName().equals(pfName)){
            String sql="select * from user where pf_user = ?";
            userPo=JSON.toJavaObject(DBManager.getDB().queryForBean(sql, uid),UserPo.class);
        }else{
            String sql="select * from user where pf_user = ? and pf = ?";
            userPo=JSON.toJavaObject(DBManager.getDB().queryForBean(sql, uid, pfName),UserPo.class);
        }
        if(userPo==null){
            JedisUtil redis = JedisUtil.getInstance("LoginServer");
            long isSaveSuc = redis.STRINGS.setnx(uid, uid);
            if (isSaveSuc == 0L) {
                logger.info("同一uid多次请求,无须重复处理:uid:"+uid);
                return userPo;
            }
            //创建平台账号
            //平台创建账号时需要随机创建一个 name和passwd
            userPo=new UserPo();
            userPo.setCreate_time(new Date());
            userPo.setPf(pfName);
            userPo.setPf_user(uid);
            //Long userId=userPo.insertToDBAndGetId();
            Long userId= SnowflakeIdWorker.getInstance().nextId();
            userPo.setId(userId);
            userPo.setName("p"+Long.toHexString(userId));
            userPo.setPasswd(Misc.md5("123456"));
            if(channel==null){
                channel="0";
            }
            userPo.setChannelCode(channel);
            //userPo.updateToDB_WithSync();
            userPo.setMail_user(email);
            userPo.insertToDB();
            //清除缓存
            redis.STRINGS.zrem(uid);
            userPo.onRegist();
            ElkLog log = new ElkLog(LogConstants.KindLog.addUserLog,userPo);
            log.setUid(userPo.getId() == null ? "0" : userPo.getId().toString());
            SLogService.new_log_elk((JSONObject)JSON.toJSON(log));
        } else{
            userPo.onLogin();
        }
        return userPo;
    }
}
