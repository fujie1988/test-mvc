package com.lianjia.test.dao.base;

import java.util.Date;
import java.util.List;

/**
 * Created by helloworld on 2017/3/17.
 */
public class Pagination<T> {
    private static final long serialVersionUID = 5901004750314020189L;
    /**
     * 默认的每页数据量（pageSize）
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    /**
     * 需分页的数据总量
     */
    protected int totalCount;
    /**
     * 每页数据量
     */
    protected int pageSize = DEFAULT_PAGE_SIZE;
    /**
     * 当前页码
     */
    protected int currentPage;
    /**
     * 获取该页的数据列表
     */
    protected List<T> list;
    /**
     *
     */
    protected int startIndex;

    public Pagination() {
        super();
    }

    public Pagination(int currentPage, int pageSize) {
        if (currentPage <= 0)
            currentPage = 1;
        if (pageSize <= 0)
            pageSize = DEFAULT_PAGE_SIZE;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.startIndex = (currentPage - 1) * pageSize;
    }

    public Pagination(int totalCount) {
        this(totalCount, totalCount);
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getStartIndex() {
        return this.startIndex;
    }


    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getTotalPage() {
        return totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
    }

    public Date getServerTime(){
        return new Date();
    }

}
