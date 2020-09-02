package com.simple.xrcraft.rule.factory;

import com.simple.xrcraft.rule.constants.DataTypeEnum;
import com.simple.xrcraft.rule.operate.BaseOperator;
import com.simple.xrcraft.rule.operate.operator.BooleanOperator;
import com.simple.xrcraft.rule.operate.operator.DateOperator;
import com.simple.xrcraft.rule.operate.operator.DateTimeOperator;
import com.simple.xrcraft.rule.operate.operator.NumberOperator;
import com.simple.xrcraft.rule.operate.operator.StringOperator;
import com.simple.xrcraft.rule.operate.operator.TimeOperator;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author pthahnil
 * @date 2019/11/29 10:23
 */
public class OperatorFactory {

	private static Map<String, BaseOperator> svcPool = new HashMap<>();
	static {
		try {
			svcPool.put(DataTypeEnum.BOOLEAN.getType(), BooleanOperator.class.newInstance());
			svcPool.put(DataTypeEnum.DATE.getType(), DateOperator.class.newInstance());
			svcPool.put(DataTypeEnum.DTTM.getType(), DateTimeOperator.class.newInstance());
			svcPool.put(DataTypeEnum.NUMBER.getType(), NumberOperator.class.newInstance());
			svcPool.put(DataTypeEnum.STRING.getType(), StringOperator.class.newInstance());
			svcPool.put(DataTypeEnum.TIME.getType(), TimeOperator.class.newInstance());
		} catch (Exception e) {}
	}
	private static OperatorFactory factory = new OperatorFactory();

	public BaseOperator getSvc(String dataType) {
		return svcPool.get(dataType);
	}

	public static OperatorFactory instance() {
		return factory;
	}

}
