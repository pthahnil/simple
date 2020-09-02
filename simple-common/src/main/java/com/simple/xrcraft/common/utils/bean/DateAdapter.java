package com.simple.xrcraft.common.utils.bean;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pthahnil on 2019/7/31.
 */
public class DateAdapter extends XmlAdapter<String, Date> {

	private static DateFormat longFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private static DateFormat shortFormat = new SimpleDateFormat("yyyyMMdd");

	@Override
	public String marshal(Date date) throws Exception {
		if(null == date){
			return null;
		}
		return longFormat.format(date);
	}

	@Override
	public Date unmarshal(String dateStr) throws Exception {
		if(null == dateStr || "".equals(dateStr.trim())){
			return null;
		}
		dateStr = dateStr.replaceAll("\\D", "");
		DateFormat format = null;
		if(dateStr.length() == 8){
			format = shortFormat;
		} else if(dateStr.length() >= 14) {
			dateStr = dateStr.substring(0, 14);
			format = longFormat;
		}
		if(null == format){
			return null;
		}
		return format.parse(dateStr);
	}
}
