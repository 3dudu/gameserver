package com.xuegao.core.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class RequestUtil {
	static Logger logger=Logger.getLogger(RequestUtil.class);
	/**
	 * 
	 * @param url 请求地址
	 * @param requestMethod GET/POST
	 * @param requestProperty Content-Type：text/xml; charset=utf-8   Content-Length:100
	 * @param bs 传输的数据  没有则为null
	 * @param isNeedHead 是否需要返回头部信息
	 * @param isNeedBody 是否需要返回主体信息
	 * @param readEncodeType 返回信息的编码格式 GBK/UTF-8
	 * @param connectTimeout 连接时间超时 60000ms
	 * @param readTimeout 读取返回信息超时 60000ms
	 * @return 请求结果
	 * @throws IOException
	 */
	public static  List<String> requestForText(String url, String requestMethod,
			Map<String, String> requestProperty, byte[] bs, boolean isNeedHead,
			boolean isNeedBody, String readEncodeType,int connectTimeout,int readTimeout) throws IOException {
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
		
		int rcode=conn.getResponseCode();
		
		InputStream input =(rcode==500?conn.getErrorStream():conn.getInputStream());
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
			List<String> list = FileUtil.readFile(input,new ArrayList<String>(), readEncodeType);
			for (String s : list) {
				result.add(s);
			}
		}else {
			input.close();
		}
		conn.disconnect();
		return result;
	}
	
	public static  String getRealUrl(String url) {
		String rs=url;
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
			int code=conn.getResponseCode();
			if(code==302){
				Map<String, List<String>> headfieldsmap = conn.getHeaderFields();
				List<String> list=headfieldsmap.get("Location");
				if(list!=null&&list.size()==1){
					rs=list.get(0);
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
	 * @param url
	 * @return
	 */
	public static int getResponseCode(String url) {
		int code=-1;
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
			code=conn.getResponseCode();
			conn.disconnect();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return code;
	}
	
	public static InputStream requestForStream(String url, String requestMethod,Map<String, String> requestProperty, byte[] bs,int connectTimeout,int readTimeout) throws IOException {
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
	
	public static List<String> uploadFiles(String url,List<File> files) throws IOException{
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
		for(int i=0;i<leng;i++){
			File file =files.get(i);
			StringBuilder sb = new StringBuilder();  
			sb.append("--");  
			sb.append(BOUNDARY);  
			sb.append("\r\n");  
			sb.append("Content-Disposition: form-data;name=\"file"+i+"\";filename=\""+ file.getName() + "\"\r\n");  
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
		List<String> rs=FileUtil.readFile(conn.getInputStream(), new ArrayList<String>(), "UTF-8");
		return rs;
	}
	
	/**
	 * 请求3次,GET,5秒超時
	 * @param url
	 * @return
	 */
	public static String request3Times(String url){
		String result=null;
		for(int i=0;i<3;i++){
			try {
				List<String> list=RequestUtil.requestForText(url, "GET", null, null, false, true, "UTF-8", 5000, 5000);
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
	/**
	 * 请求3次,GET,5秒超時
	 * @param url
	 * @return
	 */
	public static String request3TimesWithPost(String url,byte[] bs){
	
		String result=null;
		for(int i=0;i<3;i++){
			try {
				List<String> list=RequestUtil.requestForText(url, "POST", null, bs, false, true, "UTF-8", 5000, 5000);
				if(list!=null&&list.size()>0){
					result=StringUtil.toString(list);
					break;
				}
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
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

	/**
	 * post请求 请求参数为json格式
	 * @param strURL
	 * @param params json
	 * @return
	 */
	public static String post(String strURL, String params) {
		BufferedReader reader = null;
		try {
			URL url = new URL(strURL);// 创建连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod("POST"); // 设置请求方式
			// connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
			connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
			connection.connect();
			//一定要用BufferedReader 来接收响应， 使用字节来接收响应的方法是接收不到内容的
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
			out.append(params);
			out.flush();
			out.close();
			// 读取响应
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;
			String res = "";
			while ((line = reader.readLine()) != null) {
				res += line;
			}
			reader.close();

			//如果一定要使用如下方式接收响应数据， 则响应必须为: response.getWriter().print(StringUtils.join("{\"errCode\":\"1\",\"errMsg\":\"", message, "\"}")); 来返回
//            int length = (int) connection.getContentLength();// 获取长度
//            if (length != -1) {
//                byte[] data = new byte[length];
//                byte[] temp = new byte[512];
//                int readLen = 0;
//                int destPos = 0;
//                while ((readLen = is.read(temp)) > 0) {
//                    System.arraycopy(temp, 0, data, destPos, readLen);
//                    destPos += readLen;
//                }
//                String result = new String(data, "UTF-8"); // utf-8编码
//                System.out.println(result);
//                return result;
//            }
			return res;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error"; // 自定义错误信息
	}

}
