package com.xuegao.PayServer;

import cn.hutool.core.lang.Dict;
import com.xuegao.PayServer.handler.*;
import com.xuegao.PayServer.vo.DMMSDKNotifyData;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.core.netty.PooledCmdHandler;
import com.xuegao.core.netty.User;

import io.netty.channel.ChannelHandlerAdapter;

public class MainHandler extends PooledCmdHandler {

	private static Logger logger = Logger.getLogger(MainHandler.class);
	private static MainHandler instance = new MainHandler();

	public static MainHandler getInstance() {
		return instance;
	}

	private MainHandler() {
		super(8, 16, 20000);
	}

	@Override
	public void init() {


		// ----------------------------拦截器注册-------------------------

		// ----------------------------消息处理类注册--------------------------
		addRequestHandler("/p/buy", OrderHandler.class);
		addRequestHandler("/p/xuegaoSDK/cb", OrderHandler.class);
		addRequestHandler("/p/anySDK/cb", OrderHandler.class);
		addRequestHandler("/p/googleInPay/cb", OrderHandler.class);
		addRequestHandler("/p/xipuSDK/cb", OrderHandler.class);
		//悦玩JSDK 【ios和Android通用】
		addRequestHandler("/p/52YwanPayNotify/cb", YwanHandler.class);
        //手盟SDK
        addRequestHandler("/p/SMengSDK/cb", OrderHandler.class);
        //穿山甲广告回调
        addRequestHandler("/ad/CSJ/cb", AdHandler.class);
        //悦玩穿山甲广告回调
		addRequestHandler("/ad/YWANCSJ/cb", AdHandler.class);
		//优点手Q广告回调
		addRequestHandler("/ad/YDSQ/cb", AdHandler.class);
		//优亮汇广告回调
		addRequestHandler("/ad/ylh/cb", AdHandler.class);
		//IOS内购
        addRequestHandler("/p/IOSPay/cb", IOSPayHandler.class);
        //安锋
        addRequestHandler("/p/AnFeng/cb", AnFengHandler.class);
        //UCSDK(九游)
        addRequestHandler("/p/UCSDKPayNotify/cb", UCSDKHandler.class);
        addRequestHandler("/p/UCSDKAddSign/cb", UCSDKHandler.class);
        //华为SDK
        addRequestHandler("/p/HuaWeiSDKPayNotify/cb", HuaWeiSDKHandler.class);
        addRequestHandler("/p/HuaWeiSDKAddSign/cb", HuaWeiSDKHandler.class);
        addRequestHandler("/p/verifyToken/cb", HuaWeiSDKHandler.class);
        //OPPOSDK
        addRequestHandler("/p/OPPOSDKPayNotify/cb", OPPOSDKHandler.class);
        addRequestHandler("/p/getPublicKey/cb",OPPOSDKHandler.class);
        //阿纳海姆
        addRequestHandler("/p/ANHMSDKPayNotify/cb", ANHMSDKHandler.class);
        //VIVOSDK
        addRequestHandler("/p/VIVOSDKPushOrder/cb", VIVOSDKHandler.class);
        addRequestHandler("/p/VIVOSDKPayNotify/cb", VIVOSDKHandler.class);
        addRequestHandler("/p/vivosign", VIVOSDKHandler.class);
        //悦玩H5支付
        addRequestHandler("/p/YWanH5PayNotify/cb", YWanH5Handler.class);
        //冰鸟
        addRequestHandler("/p/IceBirdPayNotify/cb", IceBirdHandler.class);
        //冰鸟H5
        addRequestHandler("/p/IceBirdH5PayNotify/cb", IceBirdH5Handler.class);
        //安锋H5
        addRequestHandler("/p/AnFengH5/cb", AnFengH5Handler.class);
        //喜扑H5
        addRequestHandler("/p/XPH5/cb", XPH5ServiceHandler.class);
        //手盟H5
        addRequestHandler("/p/SMengH5/cb", SMengH5Handler.class);
        //猫耳SDK
        addRequestHandler("/p/MRSDKPayNotify/cb", MRSDKServiceHandler.class);
        //熊猫玩SDK
        addRequestHandler("/p/XMWSDKPayNotify/cb", XMWSDKServiceHandler.class);
		addRequestHandler("/p/CreateOrderXMWSDK/cb", XMWSDKServiceHandler.class);
        //OneStore
        addRequestHandler("/p/OneStoreNotify/cb", OneStoreServiceHandler.class);
		//小米SDK
		addRequestHandler("/p/XiaoMiSDKPayNotify/cb",XiaoMiServiceHandler.class);
		//应用宝SDK
		addRequestHandler("/p/YSDKCheckBalance/cb",YSDKServiceHandle.class);
		addRequestHandler("/p/YSDKDeduct/cb",YSDKServiceHandle.class);
        //MyCardSDK
        addRequestHandler("/p/MyCardSDKAuthGlobal/cb",MyCardSDKServiceHandle.class);
        addRequestHandler("/p/MyCardSDKTradeQuery/cb",MyCardSDKServiceHandle.class);
        addRequestHandler("/p/MyCardCompare", MyCardSDKServiceHandle.class);//mycard交易成功資料之差異比對
        addRequestHandler("/p/MyCardSDKForStorage/cb", MyCardSDKServiceHandle.class);//补储接口
		//优点SDK
		addRequestHandler("/p/YDSDKPayNotify/cb",YDSDKHandler.class);
		addRequestHandler("/p/YDX7SDKPayNotify/cb",YDSDKHandler.class);
		//微信小程序
		addRequestHandler("/p/WxXcxDeduct/cb",WxXcxHandler.class);
		addRequestHandler("/p/code2accessToken/cb",WxXcxHandler.class);
		//手Q小程序
		addRequestHandler("/p/code2Session/cb",SqXcxHandler.class);
		addRequestHandler("/p/sqPlaceOrder/cb",SqXcxHandler.class);
		addRequestHandler("/p/sqCallbackNotify/cb",SqXcxHandler.class);
		//优点SDK ios
		addRequestHandler("/p/YDSDKApplePayNotify/cb",YDSDKApplePayServiceHandler.class);
		//优点SDK 手q
		addRequestHandler("/p/YDSqSDKPayNotify/cb",YDSqSDKHandler.class);
		addRequestHandler("/p/getItems/cb",YDSqSDKHandler.class);
		//优点快游戏
		addRequestHandler("/p/YDSDKKuaiWanPayNotify/cb",YDSDKKuaiWanHandler.class);
		//小七SDK
		addRequestHandler("/p/X7GameSDKAddSign/cb",X7GameSDKServiceHandler.class);
		addRequestHandler("/p/X7GameSDKPayNotify/cb",X7GameSDKServiceHandler.class);
		//BilibiliSDK
		addRequestHandler("/p/BilibiliSDKAddSign/cb",BilibiliSDKHandler.class);
		addRequestHandler("/p/BilibiliSDKPayNotify/cb",BilibiliSDKHandler.class);
		//4399SDK
		addRequestHandler("/p/FTNNSDKPayNotify/cb", FTNNSDKHandler.class);
		//6kwsdk
		addRequestHandler("/p/game6kwPlaceOrder/cb",Game6kwSDKServiceHandler.class);
		addRequestHandler("/p/game6kwNotify/cb",Game6kwSDKServiceHandler.class);
		//quicksdk
		addRequestHandler("/p/quickCallbackNotify/cb",QuickSDKServiceHandler.class);
		//zantaisdk
		addRequestHandler("/p/zantaiNotify/cb",ZTHandler.class);
		//wanXinH5SDK
		addRequestHandler("/p/WanXinH5PayNotify/cb",WanXinH5SDKHandler.class);

		//wanXinKeYiH5SDK
		addRequestHandler("/p/WanXinKeYiH5PayNotify/cb",WanXinKeYiSDKServiceHandler.class);
		//荣耀
		addRequestHandler("/p/WanXinRongYaoPayNotify/cb", WanXinRongYaoSDKServiceHandler.class);
		//玩心小程序
		addRequestHandler("/p/WanXinXcxPayNotify/cb",WanXinXcxSDKServiceHandler.class);
		//Simosdk
		addRequestHandler("/p/SimoPayNotify/cb",SimoSDKHandler.class);

		//q1sdk
		addRequestHandler("/p/Q1SDKPayNotify/cb",Q1PayHandler.class);
		addRequestHandler("/p/Q1SDKAddSign/cb",Q1PayHandler.class);
		//youyisdk
		addRequestHandler("/p/YOUYISDKPayNotify/cb",YOUYISDKHandler.class);
		//quick7751
		addRequestHandler("/p/quick7751CallbackNotify/cb",Quick7751SDKServiceHandler.class);
		//yuenansdk
		addRequestHandler("/p/ate_payment/cb" , YueNanSDKHandler.class);
		//samsung sdk
		addRequestHandler("/p/SamsungNotify/cb",SamsungHandler.class);
		//BtSDK
		addRequestHandler("/p/btNotify/cb",BTPayHandler.class);
		addRequestHandler("/p/btRebate/cb",BTPayHandler.class);
        //bingcheng
		addRequestHandler("/p/BingChengSDKPayNotify/cb",BingChengSDKHandler.class);
		//AiWanSDK
		addRequestHandler("/p/AiWanPayNotify/cb",AiWanPayHandler.class);
		//掌娱趣
		addRequestHandler("/p/ZhangYuQuPayNotify/cb",ZhangYuQuSDKHandler.class);
		//GOSU
		addRequestHandler("/p/GOSUSDKPayNotify/cb",GOSUPayHandler.class);
		//leWanSDK
		addRequestHandler("/p/leWanNotify/cb",LeWanSDKServiceHandler.class);
		//kelesdk
		addRequestHandler("/p/KeLeSDKPayNotify/cb", KeLeHandler.class);
		//DMMsdk
		addRequestHandler("/p/DmmNotify/cb", DMMSDKHandle.class);
		addRequestHandler("/p/DmmSp/buy", DMMSDKHandle.class);
		/**提供外网接口更新配置数据*/
		addRequestHandler("/api/open/pay/sycnData", OpenHandler.class);
		addRequestHandler("/api/open/pay/reloadData", OpenHandler.class);

		// 刷新所有redis(slave_server和pf_option)缓存
		addRequestHandler("/api/open/pay/refreshAllRedisData", OpenHandler.class);

		/**批量支付测试
		 * */
//		addRequestHandler("/testPatchPay", PayHandler.class);
//		// 并发创建订单
//		addRequestHandler("/testPatchMakeOrder", PayHandler.class);
//		// 请求次数
//		addRequestHandler("/reqCnt", PayHandler.class);
//
//		// 批量测试 接口
//		addRequestHandler("/makeTradeAndPay", PayHandler.class);

		// 单接微信和支付
		addRequestHandler("/p/wxWebPrePlaceOrder", WxPayHandler.class);
		addRequestHandler("/p/wxPaycallbackNotify/cb", WxPayHandler.class);
		//微信H5支付
		addRequestHandler("/p/wxPayH5/cb", WxPayHandler.class);
        //微信H5支付回调接口
        addRequestHandler("/p/wxPayH5callbackNotify/cb", WxPayHandler.class);

		addRequestHandler("/p/aliWebPrePlaceOrder", AliPayHandler.class);
		addRequestHandler("/p/aliwapPrePlaceOrder", AliPayHandler.class);
		addRequestHandler("/p/alipayRSA2Notify/cb", AliPayHandler.class);
		addRequestHandler("/p/alipayH5RSA2Notify/cb", AliPayHandler.class);
		addRequestHandler("/p/ffsdknotify/cb", FFSDKHandler.class);
		addRequestHandler("/p/ffsdk/gift", FFSDKHandler.class);
		addRequestHandler("/p/kwsdknotify/cb", KWHandler.class);
		//Eyou
		addRequestHandler("/p/eyou/gift", EyouHandler.class);
		addRequestHandler("/p/eyounotify/cb1", EyouHandler.class);
		addRequestHandler("/p/eyounotify/cb2", EyouHandler.class);
	}

	/**
	 * 消息入口
	 */
	@Override
	public void handleRequest(User sender, String cmd, JSONObject params) {
		logger.info("-------收到http消息:cmd=" + cmd + ",params=" + params + "-------");
		if ("/favicon.ico".equals(cmd)) {
			sender.sendAndDisconnect(null);
			return;
		}
		super.handleRequest(sender, cmd, params);
	}

	@Override
	public void handleOutOfMaxTaskSize(User sender, String cmd, JSONObject params) {
		logger.error("系统繁忙,params=" + params.toString());
		sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("系统繁忙"));
	}

	@Override
	public void caughtLogicException(User sender, String cmd, JSONObject params, Throwable e) {
		logger.error(e.getMessage(), e);
		sender.send(MsgFactory.getDefaultErrorMsg(e.getMessage()));
	}

	@Override
	public void sessionCreated(User sender, ChannelHandlerAdapter channelHandlerAdapter) {
		// logger.info("-----session create:"+sender.getChannel().remoteAddress()+"------");
	}

	@Override
	public void sessionRemoved(User sender, ChannelHandlerAdapter channelHandlerAdapter) {
		// logger.info("-----session remove:"+sender.getChannel().remoteAddress()+"------");
	}
}
