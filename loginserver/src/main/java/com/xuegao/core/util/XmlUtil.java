package com.xuegao.core.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

public class XmlUtil {
	
	static Logger logger=Logger.getLogger(XmlUtil.class);
	
	/**
	 * xml string-->JavaBean  @XmlRootElement @XmlAttribute
	 * @param xml
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T>T xmlToBean(String xml,Class<T> clazz){
		T t = null;
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller=context.createUnmarshaller();
			t=(T)unmarshaller.unmarshal(new StringReader(xml));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return t;
	}
	/**
	 * JavaBean --> xml string
	 * @param t
	 * @param prettyFormat
	 * @return
	 */
	public static <T>String beanToXml(T t,boolean prettyFormat){
	    JAXBContext context;
	    Marshaller marshaller;
	    StringWriter writer = new StringWriter();
	    try {
	      context = JAXBContext.newInstance(t.getClass());
	      marshaller = context.createMarshaller();
	      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, prettyFormat);
	      marshaller.marshal(t, writer);
	    } catch (Exception e) {
	      logger.error(e.getMessage(),e);
	    }
	    return writer.toString();
	}
	
	public static <T>String beanToXml(T t){
	    JAXBContext context;
	    Marshaller marshaller;
	    StringWriter writer = new StringWriter();
	    try {
	      context = JAXBContext.newInstance(t.getClass());
	      marshaller = context.createMarshaller();
	      marshaller.marshal(t, writer);
	    } catch (Exception e) {
	      logger.error(e.getMessage(),e);
	    }
	    return writer.toString();
	}

}
