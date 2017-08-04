package com.alibaba.dubbo.trace;

/**
 * Created by xiaobaxi on 2017/7/31.
 */
public enum EventType {

    normal(TraceConstants.EVENT_TYPE_NORMAL, "正常入库日志"),
    invoke_interface(TraceConstants.EVENT_TYPE_INVOKE_INTERFACE, "api调用"),
    middleware_opt(TraceConstants.EVENT_TYPE_MIDDLEWARE_OPT, "中间件操作"),
    job_execute(TraceConstants.EVENT_TYPE_JOB_EXECUTE, "job执行状态"),
    custom_log(TraceConstants.EVENT_TYPE_CUSTOM_LOG, "自定义埋点日志"),
    rpc_trace(TraceConstants.EVENT_TYPE_RPC_TRACE, "rpc trace跟踪日志"),
    thirdparty_call(TraceConstants.EVENT_TYPE_THIRDPARTY_CALL, "第三方系统调用");

    private String symbol;

    private String label;

    private EventType(String symbol, String label) {
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