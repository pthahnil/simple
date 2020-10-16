package com.simple.xrcraft.common.utils.web.http.extrator;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

/**
 * Created by pthahnil on 2019/5/21.
 */
public class ByteExtrator implements Extrator<byte[]> {

	@Override
	public byte[] extract(HttpEntity entity, String charSet) throws Exception {
		byte[] resp = new byte[0];
		if(null != entity){
			resp = EntityUtils.toByteArray(entity);
		}
		return resp;
	}
}
