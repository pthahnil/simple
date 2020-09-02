package com.simple.xrcraft.base.result;

import com.simple.xrcraft.base.constants.ResultCode;
import lombok.Data;

/**
 * Created by lixiaorong on 2017/8/29.
 */
@Data
public class ErrorCode {

    private Integer resultCode;

    private String errorCode;

    private String msg;

    public boolean isSuccess(){
        return ResultCode.SUCCESS == this.resultCode;
    }
}
