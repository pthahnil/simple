package com.simple.xrcraft.common.utils.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Created by pthahnil on 2019/1/28.
 */
public class XmlUtils {

	private static XmlMapper xmlMapper = new XmlMapper();
	static {
		xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		xmlMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		xmlMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		xmlMapper.configure( ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true );

		//java 8 日期时间格式
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
		xmlMapper.registerModule(javaTimeModule);
	}

	/**
	 * obj转成xml
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static String toxml(Object obj) throws Exception {
		return xmlMapper.writeValueAsString(obj);
	}

	/**
	 * XML 转成bean
	 * @param xml
	 * @param beanClass
	 * @return
	 * @throws Exception
	 */
	public static <T> T xmlToObj(String xml, Class<T> beanClass) throws Exception {
		return xmlMapper.readValue(xml,beanClass);
	}

}
