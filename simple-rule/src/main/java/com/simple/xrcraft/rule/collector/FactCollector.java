package com.simple.xrcraft.rule.collector;

import java.util.Map;

/**
 * @description:
 * @author pthahnil
 * @date 2020/11/25 9:03
 */
public interface FactCollector {

	/**
	 * collect data needed
	 * @param params
	 * @return
	 */
	Object collect(Map<String, Object> params);

	/**
	 * factCollector key
	 * @return
	 */
	String factKey();
}
