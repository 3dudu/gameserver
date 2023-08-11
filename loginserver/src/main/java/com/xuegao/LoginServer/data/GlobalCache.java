package com.xuegao.LoginServer.data;

import com.xuegao.LoginServer.data.Constants.PlatformOption.*;
import com.xuegao.LoginServer.loginService.*;
import com.xuegao.LoginServer.redisData.MemLoginTokenBean;
import com.xuegao.core.concurrent.FixedThreadPool;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 全局缓存，缓存所有的静态数据 提供相关查询接口
 */
public class GlobalCache {

	static Logger logger=Logger.getLogger(GlobalCache.class);
	private static Map<String, AbsLoginService> serviceMap=new HashMap<>();

	public static FixedThreadPool threadPool=new FixedThreadPool(16);//16个线程

	/**
	 * 刷新最新数据 到redis中 可以不用实现
	 * 因为在 payServer 中已经实现 刷新了
	 * */
	public static synchronized void refreshRedisData(){
	}


	public static void reload(){
        logger.info("------缓存加载开始------");
        //注册各渠道登录服务类
        Map<String, AbsLoginService> serviceMapNew=new HashMap<>();
		serviceMapNew.put(AnySdk.class.getSimpleName(), new AnySdkService());
		serviceMapNew.put(XGSDK.class.getSimpleName(), new XGSDKService());
		serviceMapNew.put(Facebook.class.getSimpleName(), new FacebookService());
        serviceMapNew.put(XPSDK.class.getSimpleName(),new XPSDKService());
        serviceMapNew.put(YWan.class.getSimpleName(),new YWanService());
        serviceMapNew.put(SMengSDK.class.getSimpleName(),new SMengSDKService());
        serviceMapNew.put("AFengSDK",new AnFengSDKService());
        serviceMapNew.put(UCSDK.class.getSimpleName(),new UCSDKService());
        serviceMapNew.put(HuaWeiSDK.class.getSimpleName(),new HuaWeiSDKService());
        serviceMapNew.put(ANHMSDK.class.getSimpleName(),new ANHMSDKService());
        serviceMapNew.put(OPPOSDK.class.getSimpleName(),new OPPOSDKService());
        serviceMapNew.put(VIVOSDK.class.getSimpleName(),new VIVOSDKService());
		serviceMapNew.put(IceBirdSDK.class.getSimpleName(),new IceBirdService());
		serviceMapNew.put(YWanH5.class.getSimpleName(),new YWanH5Service());
        serviceMapNew.put(Twitter.class.getSimpleName(),new TwitterService());
        serviceMapNew.put(IceBirdH5.class.getSimpleName(),new IceBirdH5Service());
        serviceMapNew.put(AFengH5.class.getSimpleName(),new AnFengH5Service());
        serviceMapNew.put(XiPuH5.class.getSimpleName(),new XiPuH5Service());
        serviceMapNew.put(MRSDK.class.getSimpleName(),new MRSDKService());
        serviceMapNew.put(XMWSDK.class.getSimpleName(),new XMWSDKService());
        serviceMapNew.put(SMengH5.class.getSimpleName(),new SMengH5Service());
        serviceMapNew.put(XiaoMiSDK.class.getSimpleName(),new XiaoMiSDKService());
		serviceMapNew.put(YSDK.class.getSimpleName(),new YSDKService());
		serviceMapNew.put(TwitterSDK.class.getSimpleName(),new TwitterSDKService());
		serviceMapNew.put(AppleLogin.class.getSimpleName(),new AppleLoginService());
		serviceMapNew.put(YDSDK.class.getSimpleName(),new YDSDKService());
		serviceMapNew.put(WxXcxSDK.class.getSimpleName(),new WxXcxService());
		serviceMapNew.put(SqXcxSDK.class.getSimpleName(),new SqXcxLoginService());
		serviceMapNew.put(YDSDKApple.class.getSimpleName(),new YDSDKAppleService());
		serviceMapNew.put(YDSqSDK.class.getSimpleName(),new YDSqSDKService());
		serviceMapNew.put(X7GameSDK.class.getSimpleName(),new X7GameSDKLoginService());
		serviceMapNew.put(BilibiliSDK.class.getSimpleName(),new BilibiliSDKLoginService());
		serviceMapNew.put(FFSDK.class.getSimpleName(),new FFSDKService());
		serviceMapNew.put("YueNanSDK",new YueNanSDKLoginService());
		serviceMapNew.put(FirebaseLogin.class.getSimpleName(),new FirebaseLoginService());

		serviceMapNew.put("KWSDK",new KWSDKService());
		serviceMapNew.put("EyouSDK",new EyouSDKService());
		serviceMapNew.put(KeleSDK.class.getSimpleName(),new KeleSDKLoginService());
		serviceMapNew.put("BTSDK",new BTSDKLoginService());
		serviceMapNew.put(QuickSDK.class.getSimpleName() ,new QuickSDKLoginService());
		serviceMapNew.put(Quick7751SDK.class.getSimpleName() ,new Quick7751SDKLoginService());
		serviceMapNew.put(ZTSDK.class.getSimpleName() ,new ZTSDKLoginService());
		serviceMapNew.put(SimoSDK.class.getSimpleName(),new SimoSDKLoginService());

		serviceMapNew.put(FTNNSDK.class.getSimpleName(), new FTNNSDKLoginService());
		serviceMapNew.put(Game6kwSDK.class.getSimpleName(),new Game6kwSdkService());
		serviceMapNew.put(WanXinH5.class.getSimpleName(),new WanXinGameH5SDKService());
		serviceMapNew.put(WanXinKeYi.class.getSimpleName(), new WanXinKeYiGameSDKLoginService());
		serviceMapNew.put(WanXinRongYao.class.getSimpleName(),new WanXinRongYaoGameSDKLoginService());
		serviceMapNew.put("Q1SDK",new Q1LoginService());
		serviceMapNew.put(YOUYISDK.class.getSimpleName(),new YOUYILoginService());
		serviceMapNew.put(BingChengSDK.class.getSimpleName(),new BingChengLoginService());
		serviceMapNew.put(AiWanSDK.class.getSimpleName(),new AiWanLoginService());
		serviceMapNew.put(WanXinXcxSDK.class.getSimpleName(), new WanXinXcxSDKLoginService());
		serviceMapNew.put("TapTapSDK",new TapTapLoginService());
		serviceMapNew.put(GOSUSDK.class.getSimpleName(),new GOSULoginService());
		serviceMapNew.put(LeWanSDK.class.getSimpleName(),new LeWanLoginService());
		serviceMapNew.put("YDSDKKuaiWan",new YDSDKKuaiWanService());
		serviceMapNew.put(TianfuWxSDK.class.getSimpleName(),new TianfuWxLoginService());
		serviceMapNew.put("GoogleLogin",new GoogleLoginService());
		serviceMapNew.put("AppleNewLogin",new AppleNewLoginService());
		serviceMapNew.put(DMMSDK.class.getSimpleName(),new DMMLoginService());

		serviceMap=serviceMapNew;

		logger.info("------redis连接检查 开始------");
		new MemLoginTokenBean("").get();
		logger.info("------redis连接检查 完成------");
	}

	public static AbsLoginService fetchLoginService(String pfName){
		return serviceMap.get(pfName);
	}


	public static String fetchPfNameByLoginService(AbsLoginService service){
		for(Entry<String, AbsLoginService> entry:serviceMap.entrySet()){
			if(entry.getValue() == service){
				return entry.getKey();
			}
		}
		return null;
	}
}
