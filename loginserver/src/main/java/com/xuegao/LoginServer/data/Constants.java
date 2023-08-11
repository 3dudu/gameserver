package com.xuegao.LoginServer.data;

import com.xuegao.core.util.PropertiesUtil;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Constants {
	//监听端口
	public static final int port=Integer.parseInt(PropertiesUtil.get("port"));


	//各平台配置 结构体定义
	public static class PlatformOption {
		//AnySdk
		public static class AnySdk{
			public String merchantKey;//商户私钥
			public String enhancedMerchantKey;//增强商户私钥
			public String loginCheckUrl; //anysdk统一登录地址
		}
        //XGSDK
        public static class XGSDK {
            public static class Param {
                public String gameKey;
            }
            //key为gameId
            public Map<String, Param> apps = new HashMap<>();
        }
		public static class Facebook{
		    public static class Param{
                public String fbCheckUrl;
                //AppId
                public String fbAppId;
                //AppSecret
                public String fbAppKey;
            }
            //key为gameId
            public Map<String, Facebook.Param> apps = new HashMap<>();
		}
		//喜扑SDK
        public static class XPSDK{
            public String appServerKey;
        }
        //52Ywan登录的参数
 		public static  class YWan{
            public static class YWanData {
                public String j_game_secret;
                public String j_pay_sign;
                public String j_request_uri;
                public Map<String,String> products;
            }
            public Map<String, YWanData> apps = new HashMap<>();
        }
        //32位支付验证KEY，统一由手盟提供，txt参数文件,聚合包和手盟包的支付验证KEY是同一个
        public static class SMengSDK {
            public String Secret;
        }
        //6kwSDK
        public static class Game6kwSDK{
            public static class Param{
                public String sign_key_login;
                public String sign_key_pay;
                public String login_url;
                public String pay_url;
                public String notify_url;
            }
            public Map<String, Param> apps = new HashMap<>();
        }
        //SimoSDK
        public static class SimoSDK{
            public static class Param{
                public String appid;
                public String appkey;
                public String paykey;
            }
            public Map<String, Param> apps = new HashMap<>();
        }
        //AiWan
        public static class AiWanSDK{
            public static class Param{
                public String app_id;
                public String app_key;
                public String pay_key;
            }
            public Map<String, Param> apps = new HashMap<>();
        }
        //安锋SDK
        public static  class AFengSDK{
            public static class Param {
                public String sign_key_login;
                public String sign_key_pay;
                public String login_url;
            }
            //key为app_id
            public Map<String, Param> apps = new HashMap<>();
        }

        //QuickSDK
        public static class QuickSDK{
		        public String  productCode;
		        public String  productKey;
		        public String  callbackKey;
		        public String  md5Key;
        }
        //Quick7751SDK
        public static class Quick7751SDK{
            public String  productCode;
            public String  callbackKey;
            public String  md5Key;
        }
        //UCSDK
        public static  class UCSDK{
            public static class Param {
                public String apikey;
                public String notifyUrl;
            }
            //key为game_id
            public Map<String, Param> apps = new HashMap<>();
        }
        //HuaWeiSDK
        public static  class HuaWeiSDK{
            public static class Param {
                public String appId;
                public String cpId;
                public String SecretKey;
                public String payPublicKey;
                public String payPrivateKey;
                public String CP_AUTH_SIGN_PRIVATE_KEY;
            }
            //key为app_id
            public Map<String, Param> apps = new HashMap<>();
        }
        //ANHMSDK
        public static  class ANHMSDK{
            public static class Param {
                public String PayKey;
            }
            //key为app_id
            public Map<String, Param> apps = new HashMap<>();
        }
        public static class KeleSDK{
            public static class Param{
                public String gameid;
                public String clientkey;
                public String appkey;
            }
            //key为gameId
            public Map<String , KeleSDK.Param> apps = new HashMap<>();
        }
        //OPPOSDK
        public static  class OPPOSDK{
            public static class Param {
                public String appkey;
                public String appsecret;
            }
            //key为app_id
            public Map<String, Param> apps = new HashMap<>();
        }
        //YWanH5
        public static  class YWanH5{
            public static class Param {
                public String j_game_secret;
                public String j_pay_sign;
                public String j_request_uri;
            }
            //key为game_id
            public Map<String, Param> apps = new HashMap<>();
        }
        //冰鸟
        public static  class IceBirdSDK{
		    public String game_id;
		    public String app_secret;
        }
        //VIVOSDK
        public static  class VIVOSDK{
            public static class Param {
                public String appKey;
                public String notifyUrl;
            }
            //key为appId
            public Map<String, Param> apps = new HashMap<>();
        }
        //Twitter
        public static  class Twitter{
            public String consumerKey;
            public String consumerSecret;
            public String callback_url;
            public String access_token;
            public String access_token_secret;
        }
        //冰鸟H5
        public static  class IceBirdH5{
            public String game_id;
            public String app_secret;
        }
        //安锋H5
        public static  class AFengH5{
            public static class Param {
                public String sign_key;
                public String login_url;
            }
            //key为app_id
            public Map<String, Param> apps = new HashMap<>();
        }
        //喜扑H5
        public static class XiPuH5{
            public String appServerKey;
        }
        //手盟H5
        public static class SMengH5 {
            public String Secret;
        }
        //猫耳SDK
        public static class MRSDK{
            public static class Param {
                public String mr_game_secret;
            }
            //key为channelCode
            public Map<String, Param> apps = new HashMap<>();
        }
        //熊貓玩
        public static class XMWSDK{
            public static class Param {
                public String client_secret;
            }
            public Map<String, Param> apps=new HashMap<>();
        }
        //小米SDK
        public static class XiaoMiSDK{
            public static class Param {
                public String appKey;
                public String appSecret;
            }
            //key为AppID
            public Map<String, Param> apps = new HashMap<>();
        }
        //应用宝SDK
        public static class YSDK{
            public static class Param {
                public String LoginAppkey;//登录appKey
                public String PayAppkey;//支付appKey
                public String qqLoginUrl;//QQ登录url
                public String wxLoginUrl;//微信登录url
                public String getBalanceUrl;//查询余额url
                public String payUrl;//扣款url
            }
            public Map<String, Param> apps=new HashMap<>();
        }
        //TwitterSDK
        public static class TwitterSDK{
            public String consumerKey;
            public String consumerSecret;
            public String access_token;
            public String access_token_secret;


        }
        public static class AppleLogin{
		    public static class Param {
		        public String appId;
//		        public String privateKey;
//		        public String teamId;
//		        public String bundleId;
//		        public String kid;//秘钥ID
            }
		    public Map<String, Param> apps=new HashMap<>();
        }
        public static class FirebaseLogin{
		    public String appId;
        }

        public static class DMMSDK{
            public static class Param {
                public String consumer_key;
                public String consumer_secret;
                public String pc_url;
                public String sp_url;
            }
            //key是appid
            public Map<String, Param> apps = new HashMap<>();
        }
        //优点SDK
        public static  class YDSDK {
            public static class Param {
                public String openkey;
            }
            //key为appId
            public Map<String, Param> apps = new HashMap<>();
        }
        //WxXcxSDK
        public static  class WxXcxSDK {
            public  static  class Param{
                public String offerId;
                public String appSecret;
                public String appKey;
            }
            public  Map<String ,Param> apps =new HashMap<>();
        }
        //优点SDK苹果包参数
        public static  class YDSDKApple {
            public static class Param {
                public String appId;
                public String openkey;
                public Map<String,String> products;//充值档位
            }
            //key为channelCode
            public Map<String, Param> apps = new HashMap<>();
        }
        //优点-手q SDK
        public static  class YDSqSDK {
            public static class Param {
                public String openkey;
                public Map<String ,String> Items; //商品id
            }
            //key为appId
            public Map<String, Param> apps = new HashMap<>();
        }
        //SqXcxSDK
        public static  class SqXcxSDK {
            public String appId;
            public String appSecret;

        }
        //小七SDK
        public static class X7GameSDK{
		    public static  class Param{
                public String appKey;
                public String publicKey;
            }
            //key为channelCode
            public Map<String,Param > apps = new HashMap<>();
        }
        //哔哩哔哩SDK
        public static class BilibiliSDK{
		    public static class Param{
		        public String appKey;
		        public String secretKey;
            }
            //key为appId
            public Map<String , Param> apps = new HashMap<>();
        }
        //BingChengSDK
        public static class BingChengSDK{
            public static class Param{
                public String game_id;
                public String charge_key;
                public String product_key;
            }
            //key为gameId
            public Map<String , Param> apps = new HashMap<>();
        }
        //ffsdk
        public static class FFSDK{
            public static class Param {
                public String loginKey;//登录Key
                public String paykey;//支付Key
                public Map<String,String> products;//充值档位
            }
            public Map<String, Param> apps = new HashMap<>();
        }
        //4399SDK
        public static class FTNNSDK {
            public String secretKey;
        }
        //GOSUSDK
        public static class GOSUSDK {
            public String private_key;
        }
        //ZTSDK
        public static class ZTSDK{
		    public static class Param{
		        public String  appid;
		        public String  appkey;
		        public String  login_key;
		        public String  pay_key;
            }
            public Map<String,Param> apps = new HashMap<>();
        }
        //WanXinH5
        public static class WanXinH5{
		    public static class Param{
               public String  xx_game_id;
               public String  xx_game_secret;
               public String  xx_pay_sign;
               public String  j_channel_id;
               public String  call_back_url;
            }
            public Map<String, Param> apps = new HashMap<>();
        }
        //WanXinKeYi
        public static class WanXinKeYi{
            public static class Param{
                public String  xx_game_id;
                public String  xx_game_secret;
                public String  xx_pay_sign;
                public String  j_channel_id;
                public String  call_back_url;
            }
            public Map<String, Param> apps = new HashMap<>();
        }
        //WanXinRongYao
        public static class WanXinRongYao{
            public static class Param{
                public String  xx_game_id;
                public String  xx_game_secret;
                public String  xx_pay_sign;
                public String  j_channel_id;
                public String  call_back_url;
            }
            public Map<String, Param> apps = new HashMap<>();
        }
        //WanXinXcxSDK
        public static class WanXinXcxSDK{
            public static class Param{
                public String  xx_game_id;
                public String  xx_game_secret;
                public String  xx_pay_sign;
                public String  j_channel_id;
                public String  call_back_url;
            }
            public Map<String, Param> apps = new HashMap<>();
        }
        //Q1
        public static  class Q1SDK{
            public static class Param {
                public String appId;
                public String buyKey;
                public String payKey;
            }
            //key为channel
            public Map<String, Param> apps = new HashMap<>();
        }
        //LeWanSDK
        public static  class LeWanSDK{
            public static class Param {
                public String game;
                public String PaySecret;
            }
            //key为channel
            public Map<String, Param> apps = new HashMap<>();
        }
        //YOUYISDK
        public static class YOUYISDK{
		    public static class Param{
		        public String game_id;
		        public String pay_key;
            }
            public Map<String, Param> apps = new HashMap<>();
        }
        //Tianfu微信
        public static class TianfuWxSDK{
		    public static class Param{
		        public String secret;
            }
            public Map<String, TianfuWxSDK.Param> apps = new HashMap<>();
        }

	}

	//错误码
	public static enum ErrorCode{

		NAME_EXIST(101),	//用户名已存在
		ACCOUNT_NOT_EXIST(102),	//账户不存在
		WRONG_PASSWORD(103),	//密码错误
		SYSTEM_ERROR(104),		//系统异常
		LOGIN_VALID_FAIL(105),	//登录认证失败
		PARAMETER_ERROR(106),//参数错误
        VERIFIED(107)//实名认证失败
        ;

		public int type;
		public int getType(){
			return type;
		}
		private ErrorCode(int type) {
			this.type = type;
		}

		public static ErrorCode parse(int type){
			ErrorCode[] arr=ErrorCode.values();
			for(ErrorCode st:arr){
				if(st.type==type){
					return st;
				}
			}
			return null;
		}
	}

	/**
	 * 日志类型
	 */
	public enum KindLog {

		GuideLog(1, "新手引导日志");

		private int kindId;
		private String kindName;

		private KindLog(int kindId, String kindName) {
			this.kindId = kindId;
			this.kindName = kindName;
		}
		public int getKindId() {
			return kindId;
		}

		public void setKindId(int kindId) {
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
