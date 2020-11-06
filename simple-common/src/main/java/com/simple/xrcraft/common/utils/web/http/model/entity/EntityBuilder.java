package com.simple.xrcraft.common.utils.web.http.model.entity;

import org.apache.http.HttpEntity;

/**
 * @description:
 * @author pthahnil
 * @date 2020/11/6 10:09
 */
public interface EntityBuilder<T> {

	HttpEntity build(T t, String charSet);

}
