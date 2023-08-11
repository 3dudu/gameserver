package com.xuegao.core.util;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestUtil {
	static Logger logger = Logger.getLogger(RequestUtil.class);

	/**
	 * @param url             请求地址
	 * @param requestMethod   GET/POST
	 * @param requestProperty Content-Type：text/xml; charset=utf-8   Content-Length:100
	 * @param bs              传输的数据  没有则为null
	 * @param isNeedHead      是否需要返回头部信息
	 * @param isNeedBody      是否需要返回主体信息
	 * @param readEncodeType  返回信息的编码格式 GBK/UTF-8
	 * @param connectTimeout  连接时间超时 60000ms
	 * @param readTimeout     读取返回信息超时 60000ms
	 * @return 请求结果
	 * @throws IOException
	 */
	public static List<String> requestForText(String url, String requestMethod,
	                                          Map<String, String> requestProperty, byte[] bs, boolean isNeedHead,
	                                          boolean isNeedBody, String readEncodeType, int connectTimeout, int readTimeout) throws IOException {
		List<String> result = null;
		URL u = new URL(url);
//		System.out.println("POST: \"" + u.toString() + "\"");

		HttpURLConnection conn = (HttpURLConnection) u.openConnection();

		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		conn.setDoInput(true);
		conn.setDoOutput(bs == null ? false : true);
//		conn.setUseCaches(false);
//		conn.setDefaultUseCaches(false);
//		conn.setInstanceFollowRedirects(true);
		conn.setRequestMethod(requestMethod);

		if (requestProperty != null) {
			for (String s : requestProperty.keySet()) {
				conn.setRequestProperty(s, requestProperty.get(s));
			}
		}

//		conn.connect();
//		Map<String, List<String>> a=conn.getRequestProperties();
//		for(String key:a.keySet()){
//			Logger.info(key+":"+a.get(key));
//		}
		if (bs != null) {
			OutputStream output = conn.getOutputStream();
			output.write(bs);
			output.flush();
			output.close();
		}

		int rcode = conn.getResponseCode();

		InputStream input = (rcode == 500 ? conn.getErrorStream() : conn.getInputStream());
		if (isNeedHead) {
			result = result == null ? new ArrayList<String>() : result;
			Map<String, List<String>> headfieldsmap = conn.getHeaderFields();
			for (String s : headfieldsmap.keySet()) {
				if (s == null) {
					result.add(headfieldsmap.get(s).toString());
					continue;
				}
				result.add(s + ":" + headfieldsmap.get(s));
			}
		}
		if (isNeedBody) {
			result = result == null ? new ArrayList<String>() : result;
			List<String> list = FileUtil.readFile(input, new ArrayList<String>(), readEncodeType);
			for (String s : list) {
				result.add(s);
			}
		} else {
			input.close();
		}
		conn.disconnect();
		return result;
	}

	public static String getRealUrl(String url) {
		String rs = url;
		try {

			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setConnectTimeout(2000);
			conn.setReadTimeout(2000);
			conn.setDoInput(true);
			conn.setDoOutput(false);
//			conn.setUseCaches(false);
//			conn.setDefaultUseCaches(false);
//			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod("GET");
//			conn.connect();
			int code = conn.getResponseCode();
			if (code == 302) {
				Map<String, List<String>> headfieldsmap = conn.getHeaderFields();
				List<String> list = headfieldsmap.get("Location");
				if (list != null && list.size() == 1) {
					rs = list.get(0);
				}
			}
			conn.disconnect();

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return rs;
	}

	/**
	 * 获取url返回码 -1表示打不开
	 *
	 * @param url
	 * @return
	 */
	public static int getResponseCode(String url) {
		int code = -1;
		try {
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setConnectTimeout(2000);
			conn.setReadTimeout(2000);
			conn.setDoInput(true);
			conn.setDoOutput(false);
//			conn.setUseCaches(false);
//			conn.setDefaultUseCaches(false);
//			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod("GET");
//			conn.connect();
			code = conn.getResponseCode();
			conn.disconnect();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return code;
	}

	public static InputStream requestForStream(String url, String requestMethod, Map<String, String> requestProperty, byte[] bs, int connectTimeout, int readTimeout) throws IOException {
		URL u = new URL(url);
//		System.out.println("POST: \"" + u.toString() + "\"");

		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		conn.setDoInput(true);
		conn.setDoOutput(bs == null ? false : true);
//		conn.setUseCaches(false);
//		conn.setDefaultUseCaches(false);
//		conn.setInstanceFollowRedirects(true);
		conn.setRequestMethod(requestMethod);
		if (requestProperty != null) {
			for (String s : requestProperty.keySet()) {
				conn.setRequestProperty(s, requestProperty.get(s));
			}
		}
//		conn.connect();
//		Map<String, List<String>> a=conn.getRequestProperties();
//		for(String key:a.keySet()){
//			Logger.info(key+":"+a.get(key));
//		}
		if (bs != null) {
			OutputStream output = conn.getOutputStream();
			output.write(bs);
			output.flush();
			output.close();
		}
		InputStream input = conn.getInputStream();
		return input;
	}

	public static List<String> uploadFiles(String url, List<File> files) throws IOException {
		String BOUNDARY = "---------7d4a6d158c9"; // 定义数据分隔线
		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		// 发送POST请求必须设置如下两行
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

		OutputStream out = new DataOutputStream(conn.getOutputStream());
		byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线
		int leng = files.size();
		for (int i = 0; i < leng; i++) {
			File file = files.get(i);
			StringBuilder sb = new StringBuilder();
			sb.append("--");
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"file" + i + "\";filename=\"" + file.getName() + "\"\r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");

			byte[] data = sb.toString().getBytes();
			out.write(data);
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			out.write("\r\n".getBytes()); //多个文件时，二个文件之间加入这个
			in.close();
		}
		out.write(end_data);
		out.flush();
		out.close();

		// 定义BufferedReader输入流来读取URL的响应
		List<String> rs = FileUtil.readFile(conn.getInputStream(), new ArrayList<String>(), "UTF-8");
		return rs;
	}

	public static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有

				@Override
				public boolean isTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}

	/**
	 * 请求3次,GET,5秒超時
	 *
	 * @param url
	 * @return
	 */
	public static String request(String url, Map<String, String> para) throws URISyntaxException {
		String result = null;
		String respContent = null;
		String request = "";
			try {
				CloseableHttpClient httpClient = HttpClients.createDefault();

				CloseableHttpResponse response;
				URIBuilder builder = new URIBuilder(url);
				Set<String> set = para.keySet();
				for (String key : set) {
					builder.setParameter(key, para.get(key));
					request += key + para.get(key);
				}
				logger.info("request:" + request);
				HttpGet get = new HttpGet(builder.build());
				HttpResponse httpResponse = httpClient.execute(get);
				HttpEntity he = httpResponse.getEntity();
				respContent = EntityUtils.toString(he, "UTF-8");
				if (respContent != null && respContent.length() > 0) {
					result = respContent;
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		logger.info("Http GET:url=" + url + ",response=" + result);
		return result;
	}

	public static String requestWithPost(String url,Map<String, String> header, String param) throws Exception {
		logger.info("Http post url" + url +"Header" + header +"param" +param);
		String result = null;
		String respContent = null;
		StringEntity entity=null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(2000)
				.setSocketTimeout(2000).setConnectTimeout(2000).build();
		entity =  new StringEntity(param, ContentType.APPLICATION_JSON);
		CloseableHttpResponse response;
		HttpPost post = new HttpPost(url);
		post.setConfig(requestConfig);
		post.setEntity(entity);
		for (Map.Entry<String, String> entry : header.entrySet()) {
			post.setHeader(entry.getKey(), entry.getValue());
		}
		response = httpClient.execute(post);
		HttpEntity he = response.getEntity();
		respContent = EntityUtils.toString(he, "UTF-8");
		if (respContent != null && respContent.length() > 0) {
			result = respContent;
		}
		logger.info("Http POST:url=" + url + ",response=" + result);
		return result;
	}

	public static String requestWithPost(String url,Map<String, String> header, String param,ContentType type) throws Exception {
		String result = null;
		String respContent = null;
		StringEntity entity=null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(2000)
				.setSocketTimeout(2000).setConnectTimeout(2000).build();
		entity =  new StringEntity(param, ContentType.APPLICATION_JSON);
		CloseableHttpResponse response;
		HttpPost post = new HttpPost(url);
		post.setConfig(requestConfig);
		post.setEntity(entity);
		for (Map.Entry<String, String> entry : header.entrySet()) {
			post.setHeader(entry.getKey(), entry.getValue());
		}
		response = httpClient.execute(post);
		HttpEntity he = response.getEntity();
		respContent = EntityUtils.toString(he, "UTF-8");
		if (respContent != null && respContent.length() > 0) {
			result = respContent;
		}
		logger.info("Http POST:url=" + url + ",response=" + result);
		return result;
	}

	public static String requestWithGet(String url,Map<String, String> header,Map<String, String> para) throws URISyntaxException {
		String result = null;
		String respContent = null;
		String request ="";
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			CloseableHttpResponse response;
			URIBuilder builder = new URIBuilder(url);
			Set<String> set = para.keySet();
			for (String key : set) {
				builder.setParameter(key, para.get(key));
				request += key + para.get(key);
			}
			HttpGet get = new HttpGet(builder.build());
			for (Map.Entry<String, String> entry : header.entrySet()) {
				get.setHeader(entry.getKey(), entry.getValue());
			}
			response = httpClient.execute(get);
			HttpEntity he = response.getEntity();
			respContent = EntityUtils.toString(he, "UTF-8");
			if (respContent != null && respContent.length() > 0) {
				result = respContent;
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("Http GET:url=" + url + ",response=" + result);
		return result;
	}

	/**
	 * 请求3次,GET,5秒超時
	 *
	 * @param url
	 * @return
	 */
	public static String request(String url) {
		String result = null;
		try {
			List<String> list = RequestUtil.requestForText(url, "GET", null, null, false, true, "UTF-8", 5000, 5000);
			if (list != null && list.size() > 0) {
				result = StringUtil.toString(list);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("Http GET:url=" + url + ",response=" + result);
		return result;
	}

	/**
	 * 请求3次,GET,5秒超時
	 *
	 * @param url
	 * @return
	 */
	public static String requestWithPost(String url, byte[] bs) {
		String result = null;
		try {
			List<String> list = RequestUtil.requestForText(url, "POST", null, bs, false, true, "UTF-8", 5000, 5000);
			if (list != null && list.size() > 0) {
				result = StringUtil.toString(list);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("Http Post:url=" + url + ",response=" + result);
		return result;
	}

    /**
     * 设置请求头setRequestProperty
     * @param url
     * @return
     */
    public static String requestSetRp(String url,Map<String, String> requestProperty,String requestMethod,byte[] bs){
        String result=null;
        for(int i=0;i<3;i++){
            try {
                List<String> list=RequestUtil.requestForText(url, requestMethod, requestProperty, bs, false, true, "UTF-8", 5000, 5000);
                if(list!=null&&list.size()>0){
                    result=StringUtil.toString(list);
                    break;
                }
            } catch (IOException e) {
                logger.error(url);
                logger.error(e.getMessage(),e);
            }
        }
        return result;
    }

	static HostnameVerifier hv = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			logger.info("Warning: URL Host: " + hostname + " vs ." + session.getPeerHost());
			return true;
		}
	};

	static class miTM implements TrustManager, X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	private static void trustAllHttpsCertificates() {
		try {
			TrustManager[] trustAllCerts = new TrustManager[]{new miTM()};
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	static {
		trustAllHttpsCertificates();
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}
}
