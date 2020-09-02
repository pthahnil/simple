package com.simple.xrcraft.config.tx;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pthahnil on 2019/4/2.
 */
//@Aspect
//@Configuration
public class TxConfig {

	@Resource
	private PlatformTransactionManager transactionManager;

	@Bean
	public TransactionInterceptor txAdvice() {
		NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();

        //只读
		RuleBasedTransactionAttribute readOnly = new RuleBasedTransactionAttribute();
		readOnly.setReadOnly(true);
		readOnly.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);

        //当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务
		RuleBasedTransactionAttribute required = new RuleBasedTransactionAttribute();
		required.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
		required.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		Map<String, TransactionAttribute> txMap = new HashMap();

        //事务方法前缀
		txMap.put("add*", required);
		txMap.put("save*", required);
		txMap.put("insert*", required);
		txMap.put("create*", required);
		txMap.put("batch*", required);
		txMap.put("update*", required);
		txMap.put("modify*", required);
		txMap.put("delete*", required);

		//只读事务
		txMap.put("get*", readOnly);
		txMap.put("query*", readOnly);
		txMap.put("find*", readOnly);
		txMap.put("select*", readOnly);

		//其余全部
		txMap.put("*", required);
		source.setNameMap(txMap);

		return new TransactionInterceptor(transactionManager, source);
	}

	@Bean
	public Advisor txAdviceAdvisor(TransactionInterceptor txAdvice) {
		//todo 如需开启，放开Configuration数值，下面切面请更改
		String pointCutExpression = "execution(* com.simple.xrcraft.persist.service.*Service.*(..))";
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(pointCutExpression);

		return new DefaultPointcutAdvisor(pointcut, txAdvice);
	}

}
