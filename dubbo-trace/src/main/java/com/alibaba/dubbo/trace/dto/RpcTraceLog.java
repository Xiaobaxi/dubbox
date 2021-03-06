package com.alibaba.dubbo.trace.dto;


import com.alibaba.dubbo.trace.EventType;
import com.alibaba.dubbo.trace.TraceConstants;

/**
 * Created by xiaobaxi on 2017/7/31.
 */
public class RpcTraceLog {

    // 日志事件类型
    private EventType eventType;
    // 具体的span日志内容
    private String log;

    /**
     * 不可主动new
     */
    private RpcTraceLog() {

    }

    public static RpcTraceLog buildRpcTraceLog(EventType eventType, String log) {
        RpcTraceLog rpcTraceLog = new RpcTraceLog();
        rpcTraceLog.setEventType(eventType);
        rpcTraceLog.setLog(log);
        return rpcTraceLog;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.eventType.symbol());
        sb.append(TraceConstants.VERTICAL_LINE);
        sb.append(this.log);
        return sb.toString();
    }

    /**
     * 根据字符串解析成RpcTraceLog
     * @param line
     * @return
     */
    public static RpcTraceLog parseRpcTraceLog(String line) {
        String[] detail = line.split(TraceConstants.VERTICAL_LINE_SPLIT);
        return buildRpcTraceLog(EventType.valueOf(detail[0]), detail[1]);
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
