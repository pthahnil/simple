package com.simple.xrcraft.common.utils.bean.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * @description: 将json串{}原样返回，不解析
 * @author pthahnil
 * @date 22018/6/20 16:51
 */
public class StringDeserializer extends JsonDeserializer<String> {

	@Override
	public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		JsonNode node = jp.getCodec().readTree(jp);
		String retVal = null;
		if(node.isTextual()) {
			retVal = node.asText();
		} else {
			retVal = node.toString();
		}
		return retVal;
	}
}
