package com.simple.xrcraft.common.utils.web.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pthahnil on 2019/4/10.
 */
@Slf4j
public class NetworkUtil {

	/**
	 * get ip from a request
	 * @param request
	 * @return
	 */
	public final static String getIpAddress(HttpServletRequest request)  {
		String[] headers = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
		String ipAddr = null;

		String unknowHeader = "unknown";

		for (String header : headers) {
			String headerVal = request.getHeader(header);
			if(StringUtils.isBlank(headerVal) || unknowHeader.equalsIgnoreCase(headerVal)){
				continue;
			}
			if(headerVal.contains(",")){
				String[] vals = headerVal.split(",");
				for (String val : vals) {
					if(unknowHeader.equalsIgnoreCase(val)){
						continue;
					} else {
						ipAddr = val;
						break;
					}
				}
			} else {
				ipAddr = headerVal;
			}
		}
		return StringUtils.isBlank(ipAddr) ? request.getRemoteAddr() : ipAddr;
	}

	/**
	 * 获取请求参数
	 * @param request
	 * @return
	 */
	public static Map<String, String> getRequestParams(HttpServletRequest request){
		Map<String, String[]> params = request.getParameterMap();
		Map<String, String> info = new HashMap<>();
		if(MapUtils.isNotEmpty(params)){
			for (Map.Entry<String, String[]> entry : params.entrySet()) {
				String key = entry.getKey();

				String[] values = entry.getValue();
				String value = null;
				if(null != values && values.length > 0){
					value = values[0];
				}
				info.put(key,value);
			}
		}
		return info;
	}
}
