package com.xuegao.PayServer.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xuegao.core.util.PropertiesUtil;

public class Constants {
	//监听端口
	public static final int http_port=Integer.parseInt(PropertiesUtil.get("http_port"));
    //GM代充端口
    public static final int http_gm_port=Integer.parseInt(PropertiesUtil.get("http_gm_port"));
	//定时器是否开放 open/close
	public static final String open_scheduled = PropertiesUtil.get("open_scheduled");
	//tapdb开关
	public static final boolean tapdb_open = Boolean.parseBoolean(PropertiesUtil.get("tapdb_open"));
	//tapdb支付统计地址
	public static final String tapdb_url = PropertiesUtil.get("tapdb_url");
	//tapdb的appId
	public static final String tapdb_appid = PropertiesUtil.get("tapdb_appid");
	//tapdb的appId
	public static final String WEBIP = PropertiesUtil.get("webIp");
	/**发送钻石 加密数据*/
	public static final String SYCHPASSWORD="E10ADC3949BA59ABBE56E057F20F883E";
    //归因参数
    public static final String adv_appId = PropertiesUtil.get("adv_appId");
    public static final String adv_order_url = PropertiesUtil.get("adv_order_url");
    //数数科技日志路径
    public static final String SERVER_URI = PropertiesUtil.get("server_uri");
    public static final String THINKING_APP_ID =PropertiesUtil.get("thinking_app_id");
    public static final String THINKING_OPEN =PropertiesUtil.get("thinking_open");

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
		//XPSDK
        public static class XPSDK {
            public String appServerKey;
        }
		//google in  pay
		public static class GoogleInPay {
            public static class Param {
                public String publicKey;//Base64的公钥
                public String packageName;//包名
                public Map<String,String> products;//充值档位
            }
            public Map<String, Param> apps = new HashMap<>();
		}
        //OneStore
        public static class OneStore {
            public static class Param {
                public String client_id;
                public String client_secret;
                public String onestoreBase64EncodedPublicKey;
                public String sanboxOrProduct;//沙盒环境与正式环境切换
                public Map<String,String> products;//充值档位
            }
            public Map<String, Param> apps = new HashMap<>();
        }
		// 微信参数
		public static  class WxPayParam{
            public static  class Param {
                // 合作者ID
                public String partnerId;
                // api秘钥 <商户密钥>
                public String apiKey;
                public String wxpayNotify;
                public boolean isSandBox;// 是否沙盒
                public String appId;
                @Override
                public String toString() {
                    return "Param{" +
                            "partnerId='" + partnerId + '\'' +
                            ", apiKey='" + apiKey + '\'' +
                            ", wxpayNotify='" + wxpayNotify + '\'' +
                            ", isSandBox=" + isSandBox +
                            ", appId='" + appId + '\'' +
                            '}';
                }
            }
            @Override
            public String toString() {
                return "WxPayParam{" +
                        "apps=" + apps +
                        '}';
            }
            //key为appId
            public Map<String, Param> apps = new HashMap<>();
		}
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
        public static class Samsung {
            public Map<String, String> products;//充值档位
        }
        public static class BTSDK{
		    public static class Param{
		        public String bt_pay_key;
		        public String bt_rebate_key;
            }
            public Map<String, BTSDK.Param> apps = new HashMap<>();
        }
        // 微信H5支付参数
        public static  class WxPayH5Pay{
		    public static  class WxPayH5Param{
                //商户号
                public String mch_id;
                //商户密钥
                public String apiKey;
                //微信H5回调地址
                public String wxpayNotify;
                //微信h5appid
                public String app_id;
                //域名
                public String  referer_url;

                @Override
                public String toString() {
                    return "WxPayH5Param{" +
                            "mch_id='" + mch_id + '\'' +
                            ", apiKey='" + apiKey + '\'' +
                            ", wxpayNotify='" + wxpayNotify + '\'' +
                            ", app_id='" + app_id + '\'' +
                            ", referer_url='" + referer_url + '\'' +
                            '}';
                }
            }
            //key为channel_code
		    public Map<String, WxPayH5Param> apps = new HashMap<>();
            @Override
            public String toString() {
                return "WxPayH5Pay{" +
                        "apps=" + apps +
                        '}';
            }
        }
		public  static class  AliPayParam{
            public static  class Param {
                // 支付宝帐号
                public String alipayAccount;
                //合作者ID
                public String partnerId;
                // 商户私钥
                public String merchantKey;
                // 商户公钥  <支付宝公钥验证签名>
                public String merchantPublicKey;
                // 支付宝公钥
                public String aliPublicKey;
                // 支付宝回调地址
                public String alipayNotify;
                //支付宝支付成功之 跳转页面
                public String return_url;
                public boolean isSandBox;// 是否沙盒
                public String appId;
                @Override
                public String toString() {
                    return "Param{" +
                            "alipayAccount='" + alipayAccount + '\'' +
                            ", partnerId='" + partnerId + '\'' +
                            ", merchantKey='" + merchantKey + '\'' +
                            ", merchantPublicKey='" + merchantPublicKey + '\'' +
                            ", aliPublicKey='" + aliPublicKey + '\'' +
                            ", alipayNotify='" + alipayNotify + '\'' +
                            ", return_url='" + return_url + '\'' +
                            ", isSandBox=" + isSandBox +
                            ", appId='" + appId + '\'' +
                            '}';
                }
            }
            //key为appId
            public Map<String, Param> apps = new HashMap<>();
            @Override
            public String toString() {
                return "AliPayParam{" +
                        "apps=" + apps +
                        '}';
            }
        }

        public  static class  AliPayH5Param{
            public static  class Param {
                // 支付宝帐号
                public String alipayAccount;
                //合作者ID
                public String partnerId;
                // 商户私钥
                public String merchantKey;
                // 商户公钥  <支付宝公钥验证签名>
                public String merchantPublicKey;
                // 支付宝公钥
                public String aliPublicKey;
                // 支付宝回调地址
                public String alipayNotify;
                //支付宝支付成功之 跳转页面
                public String return_url;
                public boolean isSandBox;// 是否沙盒
                public String appId;
                @Override
                public String toString() {
                    return "Param{" +
                            "alipayAccount='" + alipayAccount + '\'' +
                            ", partnerId='" + partnerId + '\'' +
                            ", merchantKey='" + merchantKey + '\'' +
                            ", merchantPublicKey='" + merchantPublicKey + '\'' +
                            ", aliPublicKey='" + aliPublicKey + '\'' +
                            ", alipayNotify='" + alipayNotify + '\'' +
                            ", return_url='" + return_url + '\'' +
                            ", isSandBox=" + isSandBox +
                            ", appId='" + appId + '\'' +
                            '}';
                }
            }
            //key为appId
            public Map<String, Param> apps = new HashMap<>();
            @Override
            public String toString() {
                return "AliPayParam{" +
                        "apps=" + apps +
                        '}';
            }
        }

		// 52Ywan支付的参数
		public static  class YWan{
			public static class YWanData {
				public String j_pay_sign;
				public String j_game_secret;
                public Map<String,String> products;
				@Override
				public String toString() {
					return "YWanData [j_pay_sign=" + j_pay_sign + ", j_game_secret=" + j_game_secret + "]";
				}

			}
			public Map<String, YWanData> apps = new HashMap<>();
		}
        //32位支付验证KEY，统一由手盟提供，txt参数文件,聚合包和手盟包的支付验证KEY是同一个
        public static class SMengSDK {
            public String Secret;
        }
        //苹果内购参数
        public static  class IOSPay{
            public static class Param {
                public List<String> bundleId;
                public Map<String,String> products;//充值档位
            }
            public Map<String, Param> apps = new HashMap<>();
        }
        //安锋支付的参数
        public static  class AFengSDK{
            public static class Param {
                public String sign_key_login;
                public String sign_key_pay;
                public String login_url;
            }
            public Map<String, Param> apps = new HashMap<>();
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
                public Map<String,String > products;
            }
            //key为app_id
            public Map<String, Param> apps = new HashMap<>();
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
        //ANHMSDK
        public static  class ANHMSDK{
            public static class Param {
                public String PayKey;
            }
            //key为gid
            public Map<String, Param> apps = new HashMap<>();
        }
        //YWanH5
        public static  class YWanH5{
            public static class Param {
                public String j_game_secret;
                public String j_pay_sign;
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
        //冰鸟H5
        public static  class IceBirdH5{
            public String game_id;
            public String app_secret;
        }
        public static  class CSJSDK{
            //key为 advertisement_id
            public Map<String, String> apps = new HashMap<>();
        }

        public static  class YLHSDK{
            //key为 advertisement_id
            public Map<String, String> apps = new HashMap<>();
        }
        //安锋H5
        public static  class AnFengH5{
            public static class Param {
                public String sign_key;
                public String login_url;
            }
            //key为app_id
            public Map<String, Param> apps = new HashMap<>();
        }
        //喜扑H5
        public static class XPH5{
            public String appServerKey;
        }
        //手盟H5
        public static class SMengH5 {
            public String Secret;
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
        //猫耳SDK
        public static class MRSDK{
            public static class Param {
                public String mr_game_secret;
            }
            //key为channelCode
            public Map<String, Param> apps = new HashMap<>();
        }
        //掌娱趣
        public static class ZhangYuQuSDK{
            public static class Param {
                public String recharge_token;
                public String call_back_url;
            }
            //key为 channel_code
            public Map<String, Param> apps = new HashMap<>();
        }
        //熊貓玩
        public static class XMWSDK{
            public static class Param {
                public String client_secret;
                public String callback_url;
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
        //MyCardSDK
        public static class MyCardSDK {
		      public String AuthGlobalUrl;//向MyCard要求交易授權碼url
		      public String TradeQueryUrl;//驗證MyCard交易結果url
		      public String PaymentConfirmUrl;//確認MyCard交易並進行請款url
              public String FacServiceID;
              public String appServerKey;
		}
        //优点SDK
        public static  class YDSDK {
            public static class Param {
                public String openkey;
                public String appSecret;
            }
            //key为appId
            public Map<String, Param> apps = new HashMap<>();
        }
        //优点快游戏SDK
        public static  class YDSDKKuaiWan {
            public static class Param {
                public String openkey;
                public String appSecret;
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
        //ffsdk
        public static class FFSDK{
            public String giftkey;//支付Key
            public String callBackUrl;
            public String signkey;
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
            public Map<String,String > products;
        }
        //KWsdk 酷玩
        public static class KWSDK{
            public String gameId;//支付Key
            public String paykey;
            public Map<String,String> products;
        }
        //EyouSDK
        public static class EyouSDK{
            public String secretKey;
            public String signKey;
            public String callBackUrl;
            public static class Param {
                public Map<String,String> products;//充值档位
            }
            public Map<String, EyouSDK.Param> apps = new HashMap<>();
        }
        //ZTSDK
        public static class ZTSDK{
            public static class Param{
                public String  appid;
                public String  appkey;
                public String  login_key;
                public String  pay_key;
                public String callBackUrl;
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
                public Map<String,String > products;
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
                public Map<String,String > products;
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
        public static class KeLeSDK{
            public static class Param{
                public String gameid;
                public String clientkey;
                public String appkey;
            }
            //key为gameId
            public Map<String , KeLeSDK.Param> apps = new HashMap<>();
        }
        //WanRongYao
        public static class WanXinRongYao{
            public static class Param{
                public String  xx_game_id;
                public String  xx_game_secret;
                public String  xx_pay_sign;
                public String  j_channel_id;
                public String  call_back_url;
                public Map<String,String > products;
            }
            public Map<String, Param> apps = new HashMap<>();
        }
        //WanXinXcx
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
        //YOUYISDK
        public static class YOUYISDK{
            public static class Param{
                public String game_id;
                public String pay_key;
            }
            public Map<String, Param> apps = new HashMap<>();
        }
        //yueNanSDK
        public static class YueNanSDK{
		    public String appSecretKey;
		    public Map<String, String> products;
        }
        //GOSUSDK
        public static class GOSUSDK{
            public String  private_key;
            public static class Param{
                public Map<String,String > products;
            }
            public Map<String, GOSUSDK.Param> apps = new HashMap<>();
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
    }

	public final static String SUCCESS = "000000";

	// 未知原因的异常
	public final static String FAIL = "100001";


	public static String ALISERVICE="alipay.wap.create.direct.pay.by.user";

	// 字符编码格式 目前支持  utf-8
	public static String INPUT_CHARSET = "utf-8";
	public static String  ALI_PAYMENT_TYPE="1";
	/**
	 * 支付宝消息验证地址
	 */
	public static final String HTTPS_VERIFY_URL = "https://mapi.alipay.com/gateway.do?";
}
