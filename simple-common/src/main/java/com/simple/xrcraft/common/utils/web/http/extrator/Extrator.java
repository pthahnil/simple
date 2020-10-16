package com.simple.xrcraft.common.utils.web.http.extrator;

import org.apache.http.HttpEntity;

/**
 * Created by pthahnil on 2019/5/21.
 */
public interface Extrator<T> {

	T extract(HttpEntity entity, String charSet) throws Exception;

}
