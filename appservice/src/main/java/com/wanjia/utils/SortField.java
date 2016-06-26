package com.wanjia.utils;


import org.elasticsearch.search.sort.SortOrder;

/**
 * Created by blake on 2016/6/23.
 */
public class SortField {
    private String field ;
    private SortOrder sortOrder ;

    public SortField(String field, SortOrder sortOrder) {
        this.field = field;
        this.sortOrder = sortOrder;
    }

    public SortField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }
}
