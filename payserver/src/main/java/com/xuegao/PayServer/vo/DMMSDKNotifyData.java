package com.xuegao.PayServer.vo;

import com.alibaba.fastjson.JSON;

import java.util.Arrays;
import java.util.Iterator;

public class DMMSDKNotifyData {
    public static class DMMSDKNotifyDataBody {
        public Item[] ITEMS; //
        public String PAYMENT_ID; //第三方订单号
        public String ORDERED_TIME; //时间
        public String PAYMENT_TYPE; //类型
        public String AMOUNT; //价格


        public static class Item {
            public String SKU_ID; //我们的订单号
            public String NAME; //订单描述
            public String PRICE; //价格
            public String COUNT; //数量
            public String IMAGE_URL;
            public String DESCRIPTION; //元宝数
        }


        @Override
        public String toString() {
            return "DMMSDKNotifData{" +
                    "PAYMENT_ID='" + PAYMENT_ID + '\'' +
                    ", ORDERED_TIME='" + ORDERED_TIME + '\'' +
                    ", PAYMENT_TYPE='" + PAYMENT_TYPE + '\'' +
                    ", AMOUNT='" + AMOUNT + '\'' +
                    ", ITEMS='" + JSON.toJSONString(ITEMS) + '\'' +
                    '}';
        }

    }
    public static class DMMDMMSDKNotifyDataParam{
        public String opensocial_app_id;
        public String opensocial_app_url;
        public String opensocial_viewer_id;
        public String opensocial_owner_id;
        public String payment_id;
        public String status;

        @Override
        public String toString() {
            return "DMMDMMSDKNotifyDataParam{" +
                    "opensocial_app_id='" + opensocial_app_id + '\'' +
                    ", opensocial_app_url='" + opensocial_app_url + '\'' +
                    ", opensocial_viewer_id='" + opensocial_viewer_id + '\'' +
                    ", opensocial_owner_id='" + opensocial_owner_id + '\'' +
                    ", payment_id='" + payment_id + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
    }

}