package com.simple.xrcraft.rule.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author pthahnil
 * @date 2019/11/28 17:27
 */
@Getter
public class FactHolder implements Serializable {

	private Set<SimpleFact> facts = new HashSet<>();

	public void addFact(String fact, String value) {
		facts.add(new SimpleFact(fact, value));
	}

	public String get(String factKey) {
		if(StringUtils.isBlank(factKey)) {
			return null;
		}
		SimpleFact fact = facts.stream().filter(fac -> factKey.equals(fac.getFact())).findAny().orElse(null);
		return null != fact ? fact.getValue() : null;
	}
}
