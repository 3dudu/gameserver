package com.xuegao.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class FileUtil {
	static Logger logger=Logger.getLogger(FileUtil.class);
	/**
	 * 
	 * @param <T>
	 * @param filePath
	 *            文件路径
	 * @param container
	 *            读文件后放数据的容器，可以为list,map,byte[],int[],用map接收相当于解析properties文件
	 * @param encode
	 *            读文件的编码
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T readFile(String filePath, T container, String encode) {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				logger.info("file " + filePath + " does not exists.");
				return container;
			}
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis,
					encode == null ? "GBK" : encode);
			BufferedReader br = new BufferedReader(isr);
			if (container instanceof List) {
				List list = (List) container;
				String string = null;
				while ((string = br.readLine()) != null) {
					list.add(string);
				}
			} else if (container instanceof String[]) {
				String string = null;
				List<String> list = new ArrayList<String>();
				while ((string = br.readLine()) != null) {
					list.add(string);
				}
				int count = list.size();
				String[] strings = new String[count];
				for (int i = 0; i < count; i++) {
					strings[i] = list.get(i);
				}
				container = (T) strings;
			} else if (container instanceof Map) {
				String string = null;
				Map map = (Map) container;
				while ((string = br.readLine()) != null) {
					int a = string.indexOf("=");
					if (a != -1) {
						map.put(string.substring(0, a).trim(), string.substring(a + 1).trim());
					}
				}
			} else if (container instanceof byte[]) {
				byte[] bs = getInputStreamBytes(fis);
				container = (T) bs;
			}
			br.close();
			isr.close();
			fis.close();
		} catch (Exception e1) {
			logger.error("",e1);
		}
		return container;
	}

	/**
	 * 
	 * @param <T>
	 * @param is
	 *            文件输入流
	 * @param container
	 *            数据容器
	 * @param encode
	 *            编码
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T readFile(InputStream is, T container, String encode) {
		try {
			InputStream fis = is;
			InputStreamReader isr = new InputStreamReader(fis,
					encode == null ? "GBK" : encode);
			BufferedReader br = new BufferedReader(isr);
			if (container instanceof List) {
				List list = (List) container;
				String string = null;
				while ((string = br.readLine()) != null) {
					list.add(string);
				}
			} else if (container instanceof String[]) {
				String string = null;
				List<String> list = new ArrayList<String>();
				while ((string = br.readLine()) != null) {
					list.add(string);
				}
				int count = list.size();
				String[] strings = new String[count];
				for (int i = 0; i < count; i++) {
					strings[i] = list.get(i);
				}
				container = (T) strings;
			} else if (container instanceof Map) {
				String string = null;
				Map map = (Map) container;
				while ((string = br.readLine()) != null) {
					int a = string.indexOf("=");
					if (a != -1) {
						map.put(string.substring(0, a), string.substring(a + 1));
					}
				}
			} else if (container instanceof byte[]) {
				byte[] bs = getInputStreamBytes(fis);
				container = (T) bs;
			}
			br.close();
			isr.close();
			fis.close();
		} catch (Exception e1) {
			logger.error("",e1);
		}
		return container;
	}

	/**
	 * 
	 * @param directory
	 *            文件存放目录
	 * @param filename
	 *            文件名
	 * @param encode
	 *            写文件的编码,GBK,Utf-8等
	 * @param objs
	 *            由readFile返回的任何数据，可以是list,map,byte[],int[],
	 *            当为map时文件格式如properties
	 */
	@SuppressWarnings("rawtypes")
	public static void writeFile(String directory, String filename,
			String encode, Object... objs) {
		try {
			if (null != objs && objs.length > 0) {
				File file = new File(directory);
				if (!file.exists()) {
					file.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(directory + "/"
						+ filename);
				OutputStreamWriter osw = new OutputStreamWriter(fos,
						encode == null ? "GBK" : encode);
				BufferedWriter bw = new BufferedWriter(osw);
				if (objs.length > 1) {
					int len = objs.length;
					for (int i = 0; i < len; i++) {
						Object object = objs[i];
						bw.write(object == null ? "" : object.toString());
						if (i != len - 1) {
							bw.newLine();
						}
					}
				} else if (objs[0] instanceof List) {
					List list = (List) objs[0];
					Iterator iterator = list.iterator();
					Object object = null;
					while (true) {
						if (iterator.hasNext()) {
							object = iterator.next();
							bw.write(null == object ? "" : object.toString());
						}
						if (iterator.hasNext()) {
							bw.newLine();
						} else {
							break;
						}
					}
				} else if (objs[0] instanceof String[]) {
					String[] objects = (String[]) objs[0];
					int len = objects.length;
					for (int i = 0; i < len; i++) {
						String object = objects[i];
						bw.write(object == null ? "" : object);
						if (i != len - 1) {
							bw.newLine();
						}
					}
				} else if (objs[0] instanceof Map) {
					Map map = (Map) objs[0];
					int size = map.size();
					int count = 0;
					for (Object key : map.keySet()) {
						Object value = map.get(key);
						bw.write((key == null ? "" : key.toString().trim()) + "="
								+ (value == null ? "" : value.toString().trim()));
						count++;
						if (count != size) {
							bw.newLine();
						}
					}
				} else if (objs[0] instanceof byte[]) {
					byte[] bs = (byte[]) objs[0];
					fos.write(bs);
				} else {
					bw.write(objs[0].toString());
				}
				bw.close();
				osw.close();
				fos.close();
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}



	/**
	 * 安全地读一个文件，自动识别编码
	 * 
	 * @param <T>
	 * @param filePath
	 *            文件路径
	 * @param container
	 *            数据容器
	 * @return 装满数据的容器
	 */
	public static <T> T readFileSafely(String filePath, T container) {
		String encode = getEncoding(filePath);
		return readFile(filePath, container, encode);
	}

	public static void repOldStr(File file, String oldStr, String newStr) {
		String encode = FileUtil.getEncoding(file.getPath());
		List<String> list = FileUtil.readFile(file.getPath(),
				new ArrayList<String>(), encode);
		List<String> list2 = new ArrayList<String>();
		for (String str : list) {
			str = str.replace(oldStr, newStr);
			list2.add(str);
		}
		FileUtil.writeFile(file.getParent(), file.getName(), encode, list2);
	}

	private static byte[] getInputStreamBytes(InputStream is)
			throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		return data;
	}
	/**
	 * 追加内容到文件尾部
	 * @param filepath
	 * @param content
	 * @return
	 */
	public static boolean appendFile(String filepath,String... content){
		if(content==null||content.length==0){
			return true;
		}
		try {
			FileWriter fw=new FileWriter(filepath,true);
			for(int i=0;i<content.length;i++){
				String s=content[i];
				fw.write(s+"\r\n");
			}
			fw.close();
			return true;
		} catch (Exception e) {
			logger.error("",e);
			return false;
		}
	}
	
	/**
	 * 判断文件的编码格式
	 * 目前能判断UTF-8(有或无BOM) Unicode UTF-16BE gbk五种类型
	 * @param filepath
	 * @return 文件编码格式
	 * @throws Exception
	 */
	public static String getEncoding(String filepath){
		return EncodingDetect.getJavaEncode(filepath);
	}
	/**
	 * 打印出文件生成的build代码
	 * @param file
	 */
	public static void printBuildCode(File file){
		List<String> list=FileUtil.readFileSafely(file.getPath(), new ArrayList<String>());
		System.out.println("List<String> list=new ArrayList<>();");
		for(String s:list){
			System.out.println("list.add(\""+StringUtil.deal(s)+"\");");
		}
	}
	
}
