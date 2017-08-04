package com.alibaba.dubbo.trace.dapper;


import com.alibaba.dubbo.trace.TraceConstants;

/**
 * Created by xiaobaxi on 2017/7/31.
 */
public enum AnnotationType {

    CS(TraceConstants.CS_KEY, TraceConstants.CS_VALUE),
    SR(TraceConstants.SR_KEY, TraceConstants.SR_VALUE),
    SS(TraceConstants.SS_KEY, TraceConstants.SS_VALUE),
    CR(TraceConstants.CR_KEY, TraceConstants.CR_VALUE);

    private String symbol;

    private String label;

    private AnnotationType(String symbol, String label) {
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
