package com.simple.xrcraft.base.intf;

/**
 * Created by lixiaorong on 2018/4/27.
 */
@FunctionalInterface
public interface TriFunction<T, U, R, B> {

	B apply(T t, U u, R r);

}
