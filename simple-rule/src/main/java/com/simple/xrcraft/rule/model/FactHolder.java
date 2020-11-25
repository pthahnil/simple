package com.simple.xrcraft.rule.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author pthahnil
 * @date 2019/11/28 17:27
 */
@Slf4j
@Getter
public class FactHolder implements Serializable {

	private Map<String, SimpleFact> factMap = new HashMap<>();

	public void addFact(String fact, String value) {
		if(factMap.containsKey(fact)){
			log.info("key:{}已经存在，覆盖旧值：{}", fact, factMap.get(fact).getValue());
		}
		factMap.put(fact, new SimpleFact(fact, value));
	}

	public String get(String factKey) {
		if(StringUtils.isBlank(factKey)) {
			return null;
		}
		SimpleFact fact = factMap.get(factKey);
		return null != fact ? fact.getValue() : null;
	}
}
