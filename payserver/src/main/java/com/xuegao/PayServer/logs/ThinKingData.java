package com.xuegao.PayServer.logs;

import cn.thinkingdata.tga.javasdk.ThinkingDataAnalytics;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.po.PlayerPo;
import com.xuegao.core.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


public class ThinKingData {

    static Logger logger=Logger.getLogger(ThinKingData.class);
    public ThinkingDataAnalytics ta;


    public ThinKingData(){
        if (Constants.SERVER_URI == null || "".equals(Constants.SERVER_URI) || Constants.THINKING_APP_ID ==null || "".equals(Constants.THINKING_APP_ID)) {
            logger.info("数数科技 SERVER_URI 或 THINKING_APP_ID 未配置");
            return;
        }
        //BatchConsumer
        ThinkingDataAnalytics.BatchConsumer.Config batchConfig = new ThinkingDataAnalytics.BatchConsumer.Config();
        //可配置压缩方式为：gzip，lzo，lz4，none，默认压缩方式为gzip，内网可以使用none
        batchConfig.setCompress("gzip");
        //可配置连接超时时间,单位ms
        batchConfig.setTimeout(10000);//10s
        try {
           logger.info("数数科技配置的SERVER_URI："+Constants.SERVER_URI +" THINKING_APP_ID:"+Constants.THINKING_APP_ID);
           this.ta = new ThinkingDataAnalytics(new ThinkingDataAnalytics.BatchConsumer(Constants.SERVER_URI,  Constants.THINKING_APP_ID,batchConfig));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public Map<String,Object>  setSuperProperties(String userId , String sid ,String channelCode){
        Map<String, Object> superProperties = new HashMap<String,Object>();
        //查询角色相关数据
        PlayerPo playerPo = PlayerPo.findByUidAndSid(Long.parseLong(userId),Integer.parseInt(sid));
        //设置公共属性
        superProperties.put("account_id", userId);
        superProperties.put("channel_id", channelCode);
        superProperties.put("role_id", playerPo.getPid());
        superProperties.put("role_name", playerPo.getName() == null ? "" : playerPo.getName());
        superProperties.put("server_id", playerPo.getSid());
        superProperties.put("level", playerPo.getLevel());
        superProperties.put("vip_level", playerPo.getVip());
        superProperties.put("total_fight", String.valueOf(playerPo.getBattle_power()));
        superProperties.put("coins_amount",String.valueOf(playerPo.getMoney()));
        superProperties.put("diamond_amount",playerPo.getDiamond());

        return superProperties;
    }



}
