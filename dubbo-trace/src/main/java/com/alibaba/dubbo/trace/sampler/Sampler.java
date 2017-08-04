package com.alibaba.dubbo.trace.sampler;

/**
 * Created by xiaobaxi on 2017/7/31.
 */
public interface Sampler {

    /**
     * 是否采集
     * @return
     */
    boolean isCollect();
}
