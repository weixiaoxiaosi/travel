package com.wanjia.domain;

import java.util.List;

public class PageRoute<E> {
    private Integer pageSize;//每页条数
    private Integer totalSize; //总条数
    private Integer totalPage;//总页数
    private Integer currentPage;//当前页数
    private List<E> list;//每页显示数据

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
        totalPage=totalSize%pageSize==0? totalSize/pageSize :totalSize/pageSize+1;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public List<E> getList() {
        return list;
    }

    public void setList(List<E> list) {
        this.list = list;
    }
}
