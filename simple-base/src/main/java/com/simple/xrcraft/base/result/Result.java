package com.simple.xrcraft.base.result;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by lixiaorong on 2017/8/29.
 */
@Data
public class Result<T> extends ErrorCode implements Serializable {

    private static final long serialVersionUID = 1L;

    private T data;
}
