package com.simple.xrcraft.common.utils.validate;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by pthahnil on 2019/6/13.
 */
public class BeanValidator {

	private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	/**
	 * 校验字段
	 * @param t
	 * @param <T>
	 * @return
	 */
	public static <T> String validate(T t) {
		Set<ConstraintViolation<T>> violations = validator.validate(t);

		return violations.stream()
				.filter(via -> StringUtils.isNotBlank(via.getMessage()))
				.map(via -> via.getMessage())
				.collect(Collectors.joining(","));  //全部字段消息返回
//				.findFirst().orElse(null);  //返回单个字段消息
	}

}
