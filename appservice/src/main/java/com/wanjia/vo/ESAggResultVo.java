package com.wanjia.vo;

/**
 * Created by blake on 2016/7/5.
 */
public class ESAggResultVo {
    private Object key ;
    private Object count ;
    private ESAggResultVo vo ;

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getCount() {
        return count;
    }

    public void setCount(Object count) {
        this.count = count;
    }

    public ESAggResultVo getVo() {
        return vo;
    }

    public void setVo(ESAggResultVo vo) {
        this.vo = vo;
    }
}
