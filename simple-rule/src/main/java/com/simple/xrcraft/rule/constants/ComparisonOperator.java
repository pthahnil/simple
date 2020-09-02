package com.simple.xrcraft.rule.constants;

import lombok.Getter;

/**
 * Created by pthahnil on 2019/9/27.
 */
@Getter
public enum ComparisonOperator {
	EQ("==", "==", "等于", "1"),
	NE("!=", "!=", "不等于", "1"),
	IN("in", "in", "in", "1"),
	LO_RO("(a,b)", ">;<","前开后开", "2"),
	LO_RC("(a,b]", ">;<=","前开后毕", "2"),
	LC_RO("[a,b)", ">=;<","前闭后开", "2"),
	LC_RC("[a,b]", ">=;<=", "前闭后毕", "2"),
	;
	String oper;//前段，数据库存储的运算符号
	String simb;//存储的运算符号对应的真正的运算符
	String desc;//描述
	String compType;//1 正常，2 区间

	ComparisonOperator(String oper, String simb, String desc, String compType) {
		this.oper = oper;
		this.simb = simb;
		this.desc = desc;
		this.compType = compType;
	}

	public static ComparisonOperator getCompOper(String oper){

		if(null == oper){
			return null;
		}

		ComparisonOperator[] allVal = ComparisonOperator.values();
		for (ComparisonOperator comparisonOperator : allVal) {
			if(comparisonOperator.getOper().equals(oper)){
				return comparisonOperator;
			}
		}
		return null;
	}

}
