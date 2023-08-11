package com.xuegao.core.netty.websocketproto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;
/**
 * pb协议解析类
 * @author Administrator
 *
 */
public class PbMsgDefine {
	
	static Logger logger=Logger.getLogger(PbMsgDefine.class);
	
	
	private static Map<Integer, GeneratedExtension<?,?>> codeExtMap=new HashMap<>();
	private static Map<Class<?>, Integer> msgCodeMap=new HashMap<>();
	public static ExtensionRegistry extensionRegistry=ExtensionRegistry.newInstance();
	
	static{
		refreshMsgDefine();
		PbMsg.registerAllExtensions(extensionRegistry);
	}
	
	public static GeneratedExtension<?,?> fetchExtensionByMsgCode(int code){
		return codeExtMap.get(code);
	}
	
	public static int fetchCodeByExtensionType(Class<?> clazz){
		return msgCodeMap.get(clazz);
	}
	
	public static void refreshMsgDefine() {
		codeExtMap.clear();
		msgCodeMap.clear();
		List<Descriptor> descriptors=PbMsg.getDescriptor().getMessageTypes();
		for(Descriptor descriptor:descriptors){
			String name=descriptor.getName();
			if(!"BaseData".equals(name)){
				try {
					Class<?> class1=Class.forName("com.xuegao.core.netty.websocketproto.PbMsg$"+name);
					int EXT_FIELD_NUMBER=class1.getField("EXT_FIELD_NUMBER").getInt(class1);
					GeneratedExtension<?,?> ext=(GeneratedExtension<?,?>) class1.getField("ext").get(class1);
					codeExtMap.put(EXT_FIELD_NUMBER, ext);
					msgCodeMap.put(class1, EXT_FIELD_NUMBER);
				} catch (Exception e) {
					logger.error("解析到无ext拓展的Message:"+name);
				}
			}
		}
		logger.info("--------------Proto协议----------------");
		Map<Integer, String> tempMap=new TreeMap<>();
		for(Entry<Class<?>,Integer> entry:msgCodeMap.entrySet()){
			tempMap.put(entry.getValue(), entry.getKey().getSimpleName());
		}
		for(Entry<Integer, String> entry:tempMap.entrySet()){
			logger.info("\t"+entry.getValue()+"\t=\t"+entry.getKey()+"\t");
		}
	}
	
	public static void init(){
		
	}
	
}
