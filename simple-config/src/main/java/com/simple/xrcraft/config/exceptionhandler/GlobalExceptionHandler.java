package com.simple.xrcraft.config.exceptionhandler;

import com.simple.xrcraft.base.result.Result;
import com.simple.xrcraft.base.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 拦截未知的运行时异常
	 */
	@ExceptionHandler(RuntimeException.class)
	public Result notFount(RuntimeException e) {
		log.error("运行时异常:", e);
		return ResultUtil.fail("运行时异常:" + e.getMessage());
	}

	/**
	 * 系统异常
	 */
	@ExceptionHandler(Exception.class)
	public Result handleException(Exception e) {
		log.error(e.getMessage(), e);
		return ResultUtil.fail("服务器错误，请联系管理员");
	}

}
