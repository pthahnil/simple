package com.simple.xrcraft.common.evaluate;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author pthahnil
 * @date 2020/12/11 10:43
 */
@Data
public class PossibleConditions<T> {

	private String chain;

	private Predicate<T> predicate;

	/**
	 * @see ConditionOperation
	 */
	private String operation = ConditionOperation.RETURN;

	/** return value */
	private Function<T, T> function;

	/** 校验顺序 */
	private List<String> orders = new ArrayList<>();

	private Map<String, String> orderValueMap = new HashMap<>();

	private boolean setUpSucc = false;

	private static final Pattern pattern = Pattern.compile("(\\w+:\\w+->)+(\\w+:\\w+)");

	public PossibleConditions(String chain, Predicate<T> predicate, Function<T, T> function) {
		this.chain = chain;
		this.predicate = predicate;
		this.function = function;
		setChain(chain);
	}

	public PossibleConditions() {
	}

	/**
	 * 设置chain，同时解析
	 * @param chain
	 */
	public void setChain(String chain) {
		orders.clear();
		orderValueMap.clear();
		if(StringUtils.isBlank(chain)){
			return;
		}
		Matcher matcher = pattern.matcher(chain);
		if(!matcher.matches()){
			return;
		}

		String[] caseses = chain.split("->");
		for (String casese : caseses) {
			String[] segs = casese.split(":");
			orders.add(segs[0]);
			orderValueMap.put(segs[0], segs[1]);
		}
		this.setSetUpSucc(true);
		this.chain = chain;
	}

	/**
	 * 解析
	 * @param params
	 * @return
	 */
	public boolean evaluate(Map<String, Object> params){
		if(!setUpSucc){
			return false;
		}
		List<String> orders = this.getOrders();
		Map<String, String> caseses = this.getOrderValueMap();

		boolean maches = false;
		for (String order : orders) {
			Object fact = params.get(order);
			String factVal = null == fact ? null : fact.toString();
			String caseVal = caseses.get(order);

			maches = factVal.equals(caseVal);
			if(!maches){
				break;
			}
		}
		return maches;
	}
}
