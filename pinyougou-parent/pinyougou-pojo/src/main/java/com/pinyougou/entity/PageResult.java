package com.pinyougou.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PageResult
 * @Description //TODO
 * @Author shenliang
 * @Date 2019/3/11 14:55
 * @Version 1.0
 **/
public class PageResult<T> implements Serializable {

    private long total;//总记录数
    private List<T> rows;//当前页结果

    public PageResult() {
    }

    public PageResult(long total, List<T> rows) {
        super();
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
