package com.minis.test.entity;

import java.util.List;

public class PageResult<T> {
    private int page;
    private int size;
    private int pages; // total pages
    private long total; // total records
    private List<T> list;

    public PageResult() {}

    public PageResult(int page, int size, long total, List<T> list) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.list = list;
        this.pages = size > 0 ? (int) ((total + size - 1) / size) : 0;
    }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
}
