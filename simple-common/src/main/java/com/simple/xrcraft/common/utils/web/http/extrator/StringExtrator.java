package com.simple.xrcraft.common.utils.web.http.extrator;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

/**
 * Created by pthahnil on 2019/5/21.
 */
public class StringExtrator implements Extrator<String> {

	@Override
	public String extract(HttpEntity entity)  throws Exception {
		String resp = null;
		if(null != entity){
			resp = EntityUtils.toString(entity, "utf-8");
		}
		return resp;
	}
}
