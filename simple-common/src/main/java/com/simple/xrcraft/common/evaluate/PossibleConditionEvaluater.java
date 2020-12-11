package com.simple.xrcraft.common.evaluate;

import lombok.Data;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @description:
 * @author pthahnil
 * @date 2020/12/11 14:25
 */
@Data
public class PossibleConditionEvaluater<T> {

	private List<T> values;

	private Map<String, Object> facts;

	private List<PossibleConditions<T>> conditions;

	Map<String,Function<T, Object>> paramsGenerator;

	public T getValueNeeded() {
		List<T> valuesNeedValidate = this.values;
		for (T t : valuesNeedValidate) {

			Map<String, Object> params = fillMap(t);

			PossibleConditions outComNeeded = null;
			for (PossibleConditions com : conditions) {
				boolean maches = com.evaluate(params);
				if(!maches){
					continue;
				} else {
					outComNeeded = com;
					break;
				}
			}
			if(null != outComNeeded && ConditionOperation.RETURN.equals(outComNeeded.getOperation())){
				return (T) outComNeeded.getFunction().apply(t);
			}
		}
		return null;
	}

	private Map<String, Object> fillMap(T t){
		Map<String, Object> params = null;
		if(MapUtils.isNotEmpty(getFacts())){
			params = new HashMap<>();
			params.putAll(getFacts());
		}
		if(null != paramsGenerator){
			for (Map.Entry<String, Function<T, Object>> entry : paramsGenerator.entrySet()) {
				params.put(entry.getKey(), entry.getValue().apply(t));
			}
		}
		return params;
	}

}
