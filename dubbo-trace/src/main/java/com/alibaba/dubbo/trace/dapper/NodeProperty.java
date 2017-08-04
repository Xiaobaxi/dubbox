package com.alibaba.dubbo.trace.dapper;


import com.alibaba.dubbo.trace.TraceConstants;

/**
 * Created by xiaobaxi on 2017/7/31.
 */
public enum NodeProperty {

    C(TraceConstants.CLIENT_KEY, TraceConstants.CLIENT_VALUE),
    S(TraceConstants.SERVER_KEY, TraceConstants.SERVER_VALUE);

    private String symbol;

    private String label;

    private NodeProperty(String symbol, String label) {
        this.symbol = symbol;
        this.label = label;
    }

    public String symbol() {
        return this.symbol;
    }

    public String label() {
        return this.label;
    }
}
