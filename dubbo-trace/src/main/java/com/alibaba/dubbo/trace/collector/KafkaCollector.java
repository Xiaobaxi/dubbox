package com.alibaba.dubbo.trace.collector;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.trace.EventType;
import com.alibaba.dubbo.trace.dapper.Annotation;
import com.alibaba.dubbo.trace.dapper.EndPoint;
import com.alibaba.dubbo.trace.dapper.Span;
import com.alibaba.dubbo.trace.dto.RpcTraceLog;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobaxi on 2017/7/31.
 */
public class KafkaCollector implements Collector {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaCollector.class);

    @Override
    public void collect(Span span) {
        LOGGER.info(RpcTraceLog.buildRpcTraceLog(EventType.rpc_trace, JSON.toJSONString(span)).toString());
    }

    public static void main(String[] args) {
        List<Annotation> annotations = new ArrayList<Annotation>();
        EndPoint endPoint = new EndPoint();
        endPoint.setIp("192.168.199.136");
        endPoint.setPort(1245);
        Annotation annotation = new Annotation();
        annotation.setValue("anno");
        annotation.setTimestamp(56565464L);
        annotation.setEndPoint(endPoint);
        Annotation annotation2 = new Annotation();
        annotation2.setValue("anno2");
        annotation2.setTimestamp(565654642L);
        annotations.add(annotation);
        annotations.add(annotation2);
        Span span = new Span();
        span.setAnnotations(annotations);
        span.setId("123456");
        span.setName("span");
        span.setSample(true);
        span.setTraceId("654321");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 20000; ++i) {
            String json = JSON.toJSONString(span);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);

    }
}
