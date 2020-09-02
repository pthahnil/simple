package com.simple.xrcraft.rule.factory;

import com.simple.xrcraft.base.intf.TriFunction;
import com.simple.xrcraft.rule.constants.OperationEnum;
import com.simple.xrcraft.rule.operate.BaseOperator;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author pthahnil
 * @date 2019/11/29 10:23
 */
public class OperationFactory {

	private static Map<String, TriFunction<BaseOperator, String, String, Boolean>> svcPool = new HashMap<>();
	static {
		try {
			svcPool.put(OperationEnum.GT.getOperation(), (op, fact, tgt) -> {try {return op.gt(fact, tgt);} catch (Exception e) {}return null;});
			svcPool.put(OperationEnum.GE.getOperation(), (op, fact, tgt) -> {try {return op.ge(fact, tgt);} catch (Exception e) {}return null;});
			svcPool.put(OperationEnum.LT.getOperation(), (op, fact, tgt) -> {try {return op.lt(fact, tgt);} catch (Exception e) {}return null;});
			svcPool.put(OperationEnum.LE.getOperation(), (op, fact, tgt) -> {try {return op.le(fact, tgt);} catch (Exception e) {}return null;});
			svcPool.put(OperationEnum.EQ.getOperation(), (op, fact, tgt) -> {try {return op.eq(fact, tgt);} catch (Exception e) {}return null;});
			svcPool.put(OperationEnum.NE.getOperation(), (op, fact, tgt) -> {try {return op.ne(fact, tgt);} catch (Exception e) {}return null;});
			svcPool.put(OperationEnum.IN.getOperation(), (op, fact, tgt) -> {try {return op.in(fact, tgt);} catch (Exception e) {}return null;});
		} catch (Exception e) {}
	}
	private static OperationFactory factory = new OperationFactory();

	public TriFunction<BaseOperator, String, String, Boolean> getSvc(String operation) {
		return svcPool.get(operation);
	}

	public static OperationFactory instance() {
		return factory;
	}
}
