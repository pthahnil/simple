package com.simple.xrcraft.base.result;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixiaorong on 2017/8/29.
 */
@Data
public class DataGrid<T> extends ErrorCode implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer total = 0;

    private List<T> rows = new ArrayList();

    private Integer totalPage = 0;

    private Integer currentPage = 0;

}
