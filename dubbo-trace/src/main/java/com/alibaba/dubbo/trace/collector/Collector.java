package com.alibaba.dubbo.trace.collector;


import com.alibaba.dubbo.trace.dapper.Span;

/**
 * Created by xiaobaxi on 2017/7/31.
 */
public interface Collector {

    /**
     * 采集
     */
    void collect(Span span);
}
