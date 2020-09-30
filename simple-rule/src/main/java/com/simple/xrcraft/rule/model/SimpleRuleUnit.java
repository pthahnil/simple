package com.simple.xrcraft.rule.model;

import com.simple.xrcraft.rule.constants.DataTypeEnum;
import com.simple.xrcraft.rule.constants.ResultAccumulateType;
import com.simple.xrcraft.rule.constants.ResultValidateType;
import com.simple.xrcraft.rule.model.result.SimpleRuleUnitResult;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author pthahnil
 * @date 2019/12/5 14:17
 */
@Data
public class SimpleRuleUnit implements Serializable {

	/**
	 * 结果获取方式
	 * @see ResultAccumulateType
	 */
	private String resultAccululateType;

	/**
	 * 结果判定方式
	 * @see ResultValidateType
	 */
	private String resultValidateType;

	/** 规则集通过后的结果 */
	private String estimatedValue;

	/** 一个unit内所有的规则 */
	private List<SimpleRule> rules;

	/** 规则集结果 */
	private SimpleRuleUnitResult ruleUnitResult = new SimpleRuleUnitResult();

	/** 树形结构分叉 */
	private Integer treeBranches = 1;

	private Integer priority = 1;

	/** 数据格式, 同 rules下所有的规则的数据类型
	 * @see DataTypeEnum
	 */
	private String dataType;

	//================以下为规则集合所有的东西========================
	/** 一个unit内所有的规则 */
	private List<SimpleRuleUnit> ruleUnits;

	/**是否最底层的rule，下面只有一些细则，而不是规则组*/
	private Boolean isLeaf = true;

	/** 一设置规则，自动升级成组 */
	public void setRuleUnits(List<SimpleRuleUnit> ruleUnits) {
		this.ruleUnits = ruleUnits;
		if(null != ruleUnits && ruleUnits.size() > 0){
			this.isLeaf = false;
			/**树分叉/深度+1*/
			Integer subBranch = ruleUnits.stream().map(SimpleRuleUnit::getTreeBranches).reduce(Integer::max).get();
			this.setTreeBranches(subBranch + 1);
		}
	}

	public void addRule(SimpleRule rule) throws Exception {
		if(null == rule){
			return;
		}
		if(rules == null) {
			rules = new ArrayList<>();
		}
		String ruledataType = rule.getDataType();

		if(StringUtils.isBlank(ruledataType)){
			rule.setDataType(this.dataType);
		} else if(StringUtils.isNotBlank(ruledataType) && !ruledataType.equals(this.dataType)){
			throw new Exception("dataType uncompatable");
		}

		rules.add(rule);
	}

	public void setRules(List<SimpleRule> rules) {
		this.rules = rules;
	}
}
