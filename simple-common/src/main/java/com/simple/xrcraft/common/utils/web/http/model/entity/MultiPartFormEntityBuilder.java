package com.simple.xrcraft.common.utils.web.http.model.entity;

import com.simple.xrcraft.common.utils.web.http.model.MultipartPartSegment;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author pthahnil
 * @date 2020/11/6 10:11
 */
public class MultiPartFormEntityBuilder implements EntityBuilder<List<MultipartPartSegment>> {

	@Override
	public HttpEntity build(List<MultipartPartSegment> segs, String charSet) {
		HttpEntity entity = null;

		if(CollectionUtils.isNotEmpty(segs)){
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
			for (MultipartPartSegment seg : segs) {

				ContentBody body = null;

				Object value = seg.getValue();
				String key = seg.getKey();
				if(null == value){
					continue;
				}

				ContentType contentType = ContentType.create("text/plain", seg.getCharSet());
				boolean isPrimitive = ClassUtils.isPrimitiveOrWrapper(value.getClass());
				if(isPrimitive || value instanceof String){
					body = new StringBody(value.toString(), contentType);
				} else if(value instanceof File) {
					body = new FileBody(((File)value));
				} else if(value instanceof byte[]) {
					body = new ByteArrayBody(((byte[])value), key);
				} else if(value instanceof InputStream) {
					body = new InputStreamBody(((InputStream)value), key);
				}
				if(null != body) {
					multipartEntityBuilder.addPart(key, body);
				}
			}
			entity = multipartEntityBuilder.build();
		}

		return entity;
	}

}
