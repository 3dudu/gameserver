------------------1.0.0.0版本----------------
1.订单添加channelcode
------------------1.0.0.1版本----------------
1.悦玩,手盟,喜扑支付接入
2.暂时移除流水统计的功能
3.支付宝支付苹果，安卓兼容
------------------1.0.0.2版本----------------
1.bi日志
2.打印用户uid日志 Long 类型变更为 String （处理在elk存在丢失精度问题）
------------------1.0.0.3版本----------------
1.bi系统elk配置添加
------------------1.0.0.4版本----------------
1.ios内购
2.安锋sdk接入+ios内购
------------------1.0.0.5版本----------------
1.华为接入
2.oppo接入
3.微信h5接入
4.九游接入
5.阿纳海姆接入
------------------1.0.0.6版本----------------
1.华为接入
2.oppo接入
3.微信h5接入
4.九游接入
5.阿纳海姆接入
------------------1.0.0.7版本----------------
1.冰鸟SDK接入
------------------1.0.0.8版本----------------
1.添加version.txt文件表示包的版本号
2.冰鸟分转元比例错误bugg修复
------------------1.0.0.9版本----------------
1.ios支付缺少jar包修复
------------------1.0.0.10版本----------------
1.统一下单接口添加返回苹果产品id
------------------1.0.0.11版本----------------
1.悦玩H5接入
------------------1.0.0.12版本----------------
1.应冰鸟需求,配置参数修改为仅支持一套
------------------1.0.0.13版本----------------
1.VIVO SDK接入
2.谷歌支付增加支持档位映射功能
------------------1.0.0.14版本----------------
1.调整使用Ant命令打包后pay.sql和README.md生成的位置
2.冰鸟H5接入
3.安锋H5接入
4.喜扑H5接入
------------------1.0.0.15版本----------------
1.猫耳SDK接入
2.熊猫玩SDK接入
3.手盟H5接入
4.穿山甲广告接入
5.OneStore接入
------------------1.0.0.16版本----------------
1.添加oneStore支付沙盒环境与正式环境切换功能
2.支持华为参数可配置功能
3.小米SDK接入
------------------1.0.0.17版本----------------
1.支付同步数据加base64加密
------------------1.0.0.18版本----------------
1.应用宝SDK接入
------------------1.0.0.19版本----------------
1.修复IOSPay重复使用票据导致的刷单问题
2.MyCardSDK接入[未验证]
------------------1.0.0.20版本----------------
1.MyCardSDK接入[未验证]
------------------1.0.0.21版本----------------
1.MyCardSDK接入[未验证]
2.支持微信和支付宝多套参数配置[未验证]
------------------1.0.0.22版本----------------
1.MyCardSDK接入[QA待验证]
2.华为SDK修复支付宝不到账问题[QA待验证]
------------------1.0.0.23版本----------------
1.MyCardSDK接入
2.兼容一代苹果支付与谷歌支付档位映射表
3.兼容旧版本苹果票据返回的格式
------------------1.0.0.24版本----------------
1.支持苹果支付单个channelCode下配置多个bundleId
2.支持雪糕SDK多套参数配置
------------------1.0.0.25版本----------------
1.订单上报到归因服务器
------------------1.0.0.26版本----------------
1.优点SDk接入
------------------1.0.0.27版本----------------
1.微信小程序sdk接入
------------------1.0.0.28版本----------------
1.手Q小游戏sdk接入
2.优点IosSDK接入[待修复]
3.优点手Qsdk接入
------------------1.0.0.29版本----------------
1.anysdk代充问题修复
------------------1.0.0.30版本----------------
1.修复28版本优点IosSDK[已修复]
------------------1.0.0.31版本----------------
1.anysdk代充修复导致谷歌充值无法到账问题修复[已修复]
------------------1.0.0.32版本----------------
1.新增支付宝H5回调接口,与支付宝原生回调区分开(以前版本是同一个回调接口)
------------------1.0.0.33版本----------------
1.修复IOSPay充值掉单问题
------------------1.0.0.34版本----------------
1.兼容master配置内网ip
------------------1.0.0.36版本----------------
1.优点手Q看广告视频得奖励接入
------------------1.0.0.37版本----------------
1.fastjson 1.2.8重大漏洞，jar包升级
------------------1.0.0.38版本----------------
1.小7sdk接入
2.B站sdk接入
------------------1.0.0.39版本----------------
2.增加内部充值标识和沙盒充值标识
------------------1.0.0.40版本----------------
1.GM代充功能接入
2.统一下单接口修改 【如果没传channelCode字段则通过UserPo获取】
------------------1.0.0.41版本----------------
1.tapdb上报问题修复
------------------1.0.0.42版本----------------
1.支付宝，微信，手盟，谷歌,AnySdk,雪糕sdk,喜扑 发货统一方法
------------------1.0.0.43版本----------------
1.ios支付新增一个retcode
------------------1.0.0.44版本----------------
1.小米sdk支付回调参数修改
------------------1.0.0.46版本----------------
1.穿山甲支持多广告
------------------1.0.0.47版本----------------
1.贪玩sdk
2.合并vivosdk升级
------------------1.0.0.48版本----------------
1.jar包升级
------------------1.0.0.49版本----------------
1.合并vivosdk升级
------------------1.0.0.50版本----------------
1.添加6kwsdk 生成订单及充值回调接口
2.4399 回调接口接入
------------------1.0.0.51版本----------------
1.升级悦玩  悦玩ios添加配置档位
------------------1.0.0.52版本----------------
1.eyou接入
2.实名认证
------------------1.0.0.53版本----------------
1.eyou额外接口
2.kwsdk接入
------------------1.0.0.54版本----------------
1.华为sdk支付的升级
2.同和支付回调的接入
-------------------1.0.0.55版本----------------
1.同和sdk回调返回参数的修改
2.eyou广告添加字段
3.赞钛sdk的回调接入
-------------------1.0.0.56版本----------------
1.eyou网页充值
-------------------1.0.0.57版本----------------
1.优亮汇接入
-------------------1.0.0.59版本----------------
1.华为支付调起适配新老版本
-------------------1.0.0.60版本----------------
1.玩心小游戏sdk接入
2.数数sdk接入
-------------------1.0.0.61版本----------------
1.数数订单完成上报修改
-------------------1.0.0.62版本----------------
1.悦玩广告回调接入
-------------------1.0.0.63版本----------------
1.数数科技结算订单channelCode修改为客户端上报
-------------------1.0.0.64版本----------------
1.玩心可以sdk的接入
-------------------1.0.0.65版本----------------
1.微信h5替换appid为channel_code,预支付新增referer_url
-------------------1.0.0.66版本----------------
1.微信原生替换appid为channel_code
-------------------1.0.0.67版本----------------
1.酷玩sdk添加档位映射
2.q1SDK支付加签以及回调的接入
3.youyi支付回调的接入
4.quick7751sdk支付的接入
-------------------1.0.0.68--------------------
1.微信小程序支付完成订单金额不对问题的解决
-------------------1.0.0.69--------------------
1.越南支付的接入
2.google，onestore 获取档位的修改
3.三星sdk的接入
-------------------1.0.0.70--------------------
1.BT奇葩sdk支付功能的接入
2.SimoSDK支付功能的接入
3.WanXinRongYaoSDK支付的接入
-------------------1.0.0.71--------------------
1.Bt 奇葩返利接口的接入
2.冰橙SDK支付的接入
-------------------1.0.0.72--------------------
1.优点-小七sdk支付回调的添加
2.爱玩sdk 的接入
-------------------1.0.0.73--------------------
1.启动类禁用部分ip
-------------------1.0.0.74--------------------
1.猫耳 1510 H5 支付的接入
-------------------1.0.0.75--------------------
1.ios只处理第一笔订单问题的解决 
2.修改三星订单没有三方订单号以平台名称的问题
-------------------1.0.0.76--------------------
1.掌娱趣支付的接入
-------------------1.0.0.77--------------------
1.玩心小程序支付的接入
2.gosusdk 网页支付及内购支付的接入
-------------------1.0.0.78--------------------
1.乐玩sdk支付的接入
-------------------1.0.0.79--------------------
1.优点快游戏支付的接入
-------------------1.0.0.81--------------------
1.可乐sdk的支付接入
-------------------1.0.0.82--------------------
1.dmm支付的添加
-------------------1.0.0.83--------------------
1.dmm未支付也能到账问题的修复