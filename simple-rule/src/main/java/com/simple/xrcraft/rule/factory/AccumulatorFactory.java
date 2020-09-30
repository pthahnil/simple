package com.simple.xrcraft.rule.factory;

import com.simple.xrcraft.rule.accumulator.Accumulator;
import com.simple.xrcraft.rule.accumulator.MaxAccumulator;
import com.simple.xrcraft.rule.accumulator.MinAccumulator;
import com.simple.xrcraft.rule.accumulator.RejectAccumulator;
import com.simple.xrcraft.rule.accumulator.SumAccumulator;
import com.simple.xrcraft.rule.constants.ResultAccumulateType;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author pthahnil
 * @date 2020/9/30 11:16
 */
public class AccumulatorFactory {

	private static Map<String, Accumulator> svcPool = new HashMap<>();
	static {
		try {
			svcPool.put(ResultAccumulateType.SUM.getType(), new SumAccumulator());
			svcPool.put(ResultAccumulateType.REJECT.getType(), new RejectAccumulator());
			svcPool.put(ResultAccumulateType.MIN.getType(), new MinAccumulator());
			svcPool.put(ResultAccumulateType.MAX.getType(), new MaxAccumulator());
		} catch (Exception e) {}
	}
	private static AccumulatorFactory factory = new AccumulatorFactory();

	public Accumulator getSvc(String accType) {
		return svcPool.get(accType);
	}

	public static AccumulatorFactory instance() {
		return factory;
	}

}
