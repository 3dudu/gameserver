package com.xuegao.PayServer.executor;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


public class PayExecutor {

	/** LOGGER */
	private static Logger logger = Logger.getLogger(PayExecutor.class);

	private static final int POOL_SIZE = 4;

	private final static ExecutorService exe = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), Executors.defaultNamedThreadFactory("ReportThread"));

    private final static RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000)// 设置连接主机服务超时时间
            .setConnectionRequestTimeout(2000)// 设置连接请求超时时间
            .setSocketTimeout(3000)// 设置读取数据连接超时时间
            .build();


	public void tapdbPayReport(String userId, String orderId, float amount ,String currency_type) {
		exe.execute(new tapdbThread(userId, orderId, amount,currency_type));
	}
    public void advPayReport(String userId, String orderId, float amount, String currencyType, Integer serverId, Integer roleId, Integer status, String channelCode,String appId,Long finishTime) {
        exe.execute(new advThread(orderId, amount, channelCode,userId,serverId,roleId,status,finishTime,appId,currencyType));
    }

	class tapdbThread implements Runnable {
		private String userId;
		private String orderId;
		private float amount;//重置金额*100
		private String currency_type;

		public tapdbThread(String userId, String orderId, float amount ,String currency_type) {
			super();
			this.userId = userId;
			this.orderId = orderId;
			this.amount = amount;
			this.currency_type=currency_type;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		public float getAmount() {
			return amount;
		}

		public void setAmount(float amount) {
			this.amount = amount;
		}

		public String getCurrency_type() {
			return currency_type;
		}

		public void setCurrency_type(String currency_type) {
			this.currency_type = currency_type;
		}

		@Override
        public void run() {
			logger.info("userId:"+ userId +",orderId:"+ orderId +"amount:" + amount);
			CloseableHttpClient httpClient  = HttpClients.createMinimal();
			CloseableHttpResponse response = null;
			try {
				JSONObject object = new JSONObject();
				object.put("module", "GameAnalysis");
				object.put("name", "charge");
				object.put("index", Constants.tapdb_appid);
				object.put("identify",userId);
				JSONObject properties = new JSONObject();
				properties.put("order_id",orderId);
				properties.put("currency_type",currency_type);
				properties.put("virtual_currency_amount",0);
				properties.put("amount",(int)amount);
				object.put("properties", properties);
				HttpPost post = new HttpPost(Constants.tapdb_url);
                post.setConfig(requestConfig);
				post.addHeader("Content-Type", "application/json");
				HttpEntity entity = new StringEntity(URLEncoder.encode(object.toString(),"utf-8"), "UTF-8");
				post.setEntity(entity);
				response = httpClient.execute(post);
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
                try {
                    if (response != null) {
                        response.close();
                    }
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
		}
	}
    public static void main(String[] args) {
        ExcelReader reader = ExcelUtil.getReader("D:/qq_document/1067066952/FileRecv/test02.xlsx");
        List<Map<String,Object>> readAll = reader.readAll();
        for (Map<String,Object> map: readAll) {
            CloseableHttpClient httpClient  = HttpClients.createDefault();
            CloseableHttpResponse response;
            try {
                JSONObject object = new JSONObject();
                object.put("module", "GameAnalysis");
                object.put("name", "charge");
                object.put("index", "dip080tseglnk6q6");
                object.put("identify",map.get("user_id"));
                Date date = new Date(Long.valueOf((String) map.get("finish_time")));
                object.put("timestamp", DateUtil.format(date,"yyyy-MM-dd'T'HH:mm:ss.SSSZZ"));
                JSONObject properties = new JSONObject();
                properties.put("order_id",map.get("order_id"));
                properties.put("virtual_currency_amount",0);
                properties.put("amount",Integer.valueOf((String) map.get("pay_price"))*100);
                object.put("properties", properties);
                HttpPost post = new HttpPost("https://e.tapdb.net/event");
                post.setConfig(requestConfig);
                post.addHeader("Content-Type", "application/json");
                HttpEntity entity = new StringEntity(URLEncoder.encode(object.toString(),"utf-8"), "UTF-8");
                post.setEntity(entity);
                response = httpClient.execute(post);
                response.close();
                httpClient.close();
            } catch (Exception e) {
                System.out.println(map);
            }
        }
    }

    class advThread implements Runnable {
        private String orderId;
        private float amount;//重置金额*100
        private String channelCode;
        private String userId;
        private Integer serverId;
        private Integer roleId;
        private Integer payState;
        private Long finishTime;
        private String appId;
        private String currencyType;

        public advThread(String orderId, float amount, String channelCode, String userId, Integer serverId, Integer roleId, Integer payState, Long finishTime, String appId, String currencyType) {
            this.orderId = orderId;
            this.amount = amount;
            this.channelCode = channelCode;
            this.userId = userId;
            this.serverId = serverId;
            this.roleId = roleId;
            this.payState = payState;
            this.finishTime = finishTime;
            this.appId = appId;
            this.currencyType = currencyType;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }

        public String getChannelCode() {
            return channelCode;
        }

        public void setChannelCode(String channelCode) {
            this.channelCode = channelCode;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Integer getServerId() {
            return serverId;
        }

        public void setServerId(Integer serverId) {
            this.serverId = serverId;
        }

        public Integer getRoleId() {
            return roleId;
        }

        public void setRoleId(Integer roleId) {
            this.roleId = roleId;
        }

        public Integer getPayState() {
            return payState;
        }

        public void setPayState(Integer payState) {
            this.payState = payState;
        }

        public Long getFinishTime() {
            return finishTime;
        }

        public void setFinishTime(Long finishTime) {
            this.finishTime = finishTime;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getCurrencyType() {
            return currencyType;
        }

        public void setCurrencyType(String currencyType) {
            this.currencyType = currencyType;
        }

        @Override
        public void run() {
            logger.info("userId:"+ userId +",orderId:"+ orderId +"amount:" + amount+",appId:"+ appId);
            CloseableHttpClient httpClient  = HttpClients.createMinimal();
            CloseableHttpResponse response=null;
            try {
                JSONObject object = new JSONObject();
                object.put("orderId", orderId);
                object.put("userId",userId);
                object.put("appId", appId);
                object.put("currencyType",currencyType);
                object.put("serverId",serverId);
                object.put("roleId",roleId);
                object.put("payState",payState);
                object.put("amount",amount);
                object.put("createTime",finishTime);
                object.put("channel",channelCode);
                HttpPost post = new HttpPost(Constants.adv_order_url);
                post.setConfig(requestConfig);
                post.addHeader("Content-Type", "application/json");
                HttpEntity entity = new StringEntity(object.toString(), "UTF-8");
                post.setEntity(entity);
                response = httpClient.execute(post);
            } catch (Exception e) {
                logger.error(e.getMessage());
            } finally {
                try {
                    if (response != null) {
                        response.close();
                    }
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
