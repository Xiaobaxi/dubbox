package com.alibaba.dubbo.trace.dapper;


import com.alibaba.dubbo.trace.TraceConstants;

/**
 * Created by xiaobaxi on 2017/7/31.
 */
public enum ExceptionType {

    EXCEPTION(TraceConstants.DUBBO_EXCEPTION, TraceConstants.EXCEPTION),
    TIMEOUTEXCEPTION(TraceConstants.DUBBO_TIMEOUTEXCEPTION, TraceConstants.EXCEPTION);

    private String symbol;

    private String label;

    private ExceptionType(String symbol, String label) {
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
