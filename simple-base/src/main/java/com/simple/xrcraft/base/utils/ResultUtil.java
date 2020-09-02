package com.simple.xrcraft.base.utils;

import com.simple.xrcraft.base.constants.ResultCode;
import com.simple.xrcraft.base.result.Result;

/**
 * Created by pthahnil on 2019/4/11.
 */
public class ResultUtil {

	public static Result success(){
		return success(null);
	}

	public static <T> Result<T> success(T t){
		return success(t, "操作成功");
	}

	public static Result success(String msg){
		return success(null, msg);
	}

	public static <T> Result<T> success(T t, String msg){
		Result result = new Result();
		result.setResultCode(ResultCode.SUCCESS);
		result.setData(t);
		result.setMsg(msg);
		return result;
	}

	public static Result fail(){
		return fail(null, "操作失败");
	}

	public static Result fail(String errDesc){
		return fail(null, errDesc);
	}

	public static Result fail(String errCode, String errDesc){
		Result result = new Result();
		result.setResultCode(ResultCode.FAIL);
		result.setErrorCode(errCode);
		result.setMsg(errDesc);
		return result;
	}

}
