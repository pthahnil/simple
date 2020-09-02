package com.simple.xrcraft.common.utils.bean;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by pthahnil on 2019/7/31.
 */
@Slf4j
public class JaxbUtils {

	/**
	 * JavaBean转换成xml
	 * @param obj
	 * @param encoding
	 * @return
	 */
	public static String toXml(Object obj, String encoding) throws Exception {
		String result = null;
		try {
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

			StringWriter writer = new StringWriter();
			marshaller.marshal(obj, writer);
			result = writer.toString();
		} catch (Exception e) {
			log.error("obj -> xml error", e);
			throw e;
		}

		return result;
	}

	/**
	 * xml转换成JavaBean
	 * @param xml
	 * @param c
	 * @return
	 */
	public static <T> T toObj(String xml, Class<T> c) throws Exception {
		T t = null;
		try {
			JAXBContext context = JAXBContext.newInstance(c);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			t = (T) unmarshaller.unmarshal(new StringReader(xml));
		} catch (Exception e) {
			log.error("xml -> obj error", e);
			throw e;
		}
		return t;
	}

}
