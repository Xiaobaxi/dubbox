package com.alibaba.dubbo.trace;

/**
 * Created by xiaobaxi on 2017/7/31.
 */
public class TraceConstants {

    public static final String VERTICAL_LINE = "|";

    public static final String VERTICAL_LINE_SPLIT = "\\|";

    // annotation type相关
    public static final String CS_KEY = "cs";
    public static final String CS_VALUE = "client send";
    public static final String SR_KEY = "sr";
    public static final String SR_VALUE = "server receive";
    public static final String SS_KEY = "ss";
    public static final String SS_VALUE = "server send";
    public static final String CR_KEY = "cr";
    public static final String CR_VALUE = "client receive";

    // node property
    public static final String CLIENT_KEY = "c";
    public static final String CLIENT_VALUE = "client";
    public static final String SERVER_KEY = "s";
    public static final String SERVER_VALUE = "server";

    // RpcInvocation attachment和捕捉annotation中的exception相关
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String PARENT_ID = "parentId";
    public static final String SAMPLE = "isSample";
    public static final String EXCEPTION = "exception";
    public static final String DUBBO_EXCEPTION = "dubbo.exception";
    public static final String DUBBO_TIMEOUTEXCEPTION = "dubbo.timeoutexception";

    // 日志类型
    public static final String EVENT_TYPE_NORMAL = "normal";
    public static final String EVENT_TYPE_INVOKE_INTERFACE = "invoke_interface";
    public static final String EVENT_TYPE_MIDDLEWARE_OPT = "middleware_opt";
    public static final String EVENT_TYPE_JOB_EXECUTE = "job_execute";
    public static final String EVENT_TYPE_CUSTOM_LOG = "custom_log";
    public static final String EVENT_TYPE_RPC_TRACE = "rpc_trace";
    public static final String EVENT_TYPE_THIRDPARTY_CALL = "thirdparty_call";

}
