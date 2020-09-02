package com.simple.xrcraft.common.utils.bean.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @description: 保留两位小数
 * @author pthahnil
 * @date 2020/6/10 16:51
 */
public class DecimalSerializer extends JsonSerializer<BigDecimal> {

	private DecimalFormat df = new DecimalFormat("#0.00");

	@Override
	public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException {
		if(value != null) {
			gen.writeNumber(df.format(value));
		}
	}

}
