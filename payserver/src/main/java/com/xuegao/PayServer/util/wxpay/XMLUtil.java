package com.xuegao.PayServer.util.wxpay;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class XMLUtil {
	static Logger logger=Logger.getLogger(XMLUtil.class);
   /**
    * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
    * @param strxml
    * @return
    * @throws JDOMException
    * @throws IOException
    */
   public static Map doXMLParse(String strxml) throws JDOMException, IOException {
      strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");

      if(null == strxml || "".equals(strxml)) {
         return null;
      }
      // [第一层保护 xxe攻击]
      strxml = filterXXE(strxml);
      
      logger.info("strxml filterXXE Post:"+strxml);
      Map m = new HashMap();

      InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
      SAXBuilder builder = new SAXBuilder();
      
      // [底层保护 xxe攻击]
      builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
      builder.setFeature("http://xml.org/sax/features/external-general-entities", false);
      builder.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      
      
      Document doc = builder.build(in);
      Element root = doc.getRootElement();
      List list = root.getChildren();
      Iterator it = list.iterator();
      while(it.hasNext()) {
         Element e = (Element) it.next();
         String k = e.getName();
         String v = "";
         List children = e.getChildren();
         if(children.isEmpty()) {
            v = e.getTextNormalize();
         } else {
            v = XMLUtil.getChildrenText(children);
         }

         m.put(k, v);
      }

      //关闭流
      in.close();

      return m;
   }

   /**
    * 获取子结点的xml
    * @param children
    * @return String
    */
   public static String getChildrenText(List children) {
      StringBuffer sb = new StringBuffer();
      if(!children.isEmpty()) {
         Iterator it = children.iterator();
         while(it.hasNext()) {
            Element e = (Element) it.next();
            String name = e.getName();
            String value = e.getTextNormalize();
            List list = e.getChildren();
            sb.append("<" + name + ">");
            if(!list.isEmpty()) {
               sb.append(XMLUtil.getChildrenText(list));
            }
            sb.append(value);
            sb.append("</" + name + ">");
         }
      }

      return sb.toString();
   }

   /**
    * 获取xml编码字符集
    * @param strxml
    * @return
    * @throws IOException
    * @throws JDOMException
    */
   public static String getXMLEncoding(String strxml) throws JDOMException, IOException {
//      InputStream in = HttpClientUtil.String2Inputstream(strxml);
	   InputStream in=new ByteArrayInputStream(strxml.getBytes());   
      SAXBuilder builder = new SAXBuilder();
      Document doc = builder.build(in);
      in.close();
      return (String)doc.getProperty("encoding");
   }
   
   /**
    * 防止 XXE漏洞 注入实体攻击
    * 过滤 过滤用户提交的XML数据
    *       过滤关键词：<!DOCTYPE和<!ENTITY，或者SYSTEM和PUBLIC。
    */
	public static String filterXXE(String xmlStr) {
		xmlStr = xmlStr.replace("DOCTYPE", "").replace("SYSTEM", "").replace("ENTITY", "").replace("PUBLIC", "");
		return xmlStr;

	}
}