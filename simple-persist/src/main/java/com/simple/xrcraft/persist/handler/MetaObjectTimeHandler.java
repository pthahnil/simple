package com.simple.xrcraft.persist.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @description:
 * @author pthahnil
 * @date 2020/3/5 9:01
 */
public class MetaObjectTimeHandler implements MetaObjectHandler {

	@Override
	public void insertFill(MetaObject metaObject) {
		Date today = new Date();
		this.strictInsertFill(metaObject, "create_time", Date.class, today);
		this.strictInsertFill(metaObject, "update_time", Date.class, today);
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		Date today = new Date();
		this.strictInsertFill(metaObject, "update_time", Date.class, today);
	}
}
