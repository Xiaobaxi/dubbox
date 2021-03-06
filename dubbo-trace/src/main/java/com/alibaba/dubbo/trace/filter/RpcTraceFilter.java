package com.alibaba.dubbo.trace.filter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.dubbo.trace.TraceConstants;
import com.alibaba.dubbo.trace.dapper.BinaryAnnotation;
import com.alibaba.dubbo.trace.dapper.EndPoint;
import com.alibaba.dubbo.trace.dapper.ExceptionType;
import com.alibaba.dubbo.trace.dapper.Span;
import com.alibaba.dubbo.trace.trace.Tracer;

import java.util.concurrent.TimeoutException;

/**
 * Created by xiaobaxi on 2017/7/31.
 */
@Activate(group = {com.alibaba.dubbo.common.Constants.PROVIDER, com.alibaba.dubbo.common.Constants.CONSUMER})
public class RpcTraceFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcTraceFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        long start = System.currentTimeMillis();
        RpcContext context = RpcContext.getContext();
        boolean isConsumerSide = context.isConsumerSide();
        boolean isProviderSide = context.isProviderSide();
        String methodName = context.getMethodName();
        RpcInvocation rpcInvocation = (RpcInvocation) invocation;

        //　设计的serviceId
        String serviceId = context.getUrl().getServiceInterface() + "_" + methodName;

        Tracer tracer = Tracer.getInstance();

        EndPoint endPoint = tracer.buildEndPoint(context.getLocalAddressString(), context.getLocalPort());

        Span span = null;
        try {
            if (isConsumerSide) {
                // 如果是消费者
                Span parentSpan = tracer.getParentSpan();
                if (null == parentSpan) {
                    // 如果parentSpan为null, 表示该Span为root span
                    span = tracer.newSpan(methodName, serviceId);
                } else {
                    // 叶子span
                    span = tracer.buildSpan(parentSpan.getTraceId(), parentSpan.getId(), tracer.generateSpanId(), methodName, parentSpan.getSample(), serviceId);
                }
            } else if (isProviderSide) {
                // 如果是生产者
                String traceId = AttachmentUtil.getAttachment(rpcInvocation, TraceConstants.TRACE_ID);
                String parentId = AttachmentUtil.getAttachment(rpcInvocation, TraceConstants.PARENT_ID);
                String spanId = AttachmentUtil.getAttachment(rpcInvocation, TraceConstants.SPAN_ID);
                boolean isSample = traceId != null && AttachmentUtil.getAttachmentBoolean(rpcInvocation, TraceConstants.SAMPLE);
                span = tracer.buildSpan(traceId, parentId, spanId, methodName, isSample, serviceId);
            }

            // 调用具体业务逻辑之前处理
            this.invokeBefore(span, endPoint, start, isConsumerSide, isProviderSide);
            // 传递附件到RpcInvocation, 传递到下游，确保下游能够收到traceId等相关信息，保证请求能够标记到正确的traceId
            this.setAttachment(rpcInvocation, span);
            // 执行具体的业务或者下游的filter
            Result result = invoker.invoke(invocation);

            if (result.hasException()) {
                // 如果在请求过程中发生了异常, 需要进行异常的处理和相关annotation的记录
                this.processException(endPoint, result.getException().getMessage(), ExceptionType.EXCEPTION);
            }

            return result;
        } catch (RpcException e) {
            if (null != e.getCause() && e.getCause() instanceof TimeoutException) {
                // 执行该filter发生异常，如果异常是com.alibaba.dubbo.remoting.TimeoutException
                this.processException(endPoint, e.getMessage(), ExceptionType.TIMEOUTEXCEPTION);
            } else {
                // 其他异常
                this.processException(endPoint, e.getMessage(), ExceptionType.EXCEPTION);
            }
            // 将异常抛出去
            throw e;
        } finally {
            // 调用具体业务逻辑之后处理
            if (null != span) {
                long end = System.currentTimeMillis();
                this.invokeAfter(span, endPoint, end, isConsumerSide, isProviderSide);
            }
        }

    }

    /**
     * 处理异常，构造Span的BinaryAnnotation
     * @param endPoint
     * @param message
     * @param type
     */
    private void processException(EndPoint endPoint, String message, ExceptionType type) {
        BinaryAnnotation exAnnotation = new BinaryAnnotation();
        exAnnotation.setKey(type.label());
        exAnnotation.setValue(message);
        exAnnotation.setType(type.symbol());
        exAnnotation.setEndPoint(endPoint);
        // add到span
        Tracer tracer = Tracer.getInstance();
        tracer.addBinaryAnntation(exAnnotation);
    }

    /**
     * 将Span的相关值设置到RpcInvocation的attachment中, 然后传递到下游
     * @param invocation
     * @param span
     */
    private void setAttachment(RpcInvocation invocation, Span span) {
        if (span.getSample() && null != span) {
            // 如果进行采样
            invocation.setAttachment(TraceConstants.TRACE_ID, span.getTraceId() == null ? null : String.valueOf(span.getTraceId()));
            invocation.setAttachment(TraceConstants.SPAN_ID, span.getId() == null ? null : String.valueOf(span.getId()));
            invocation.setAttachment(TraceConstants.PARENT_ID, span.getParentId() == null ? null : String.valueOf(span.getParentId()));
            invocation.setAttachment(TraceConstants.SAMPLE, span.getSample() == null ? null : String.valueOf(span.getSample()));
        }

    }

    /**
     * 调用具体逻辑之前，记录相关的annotation、设置对应的parentSpan
     * @param span
     * @param endPoint
     * @param isConsumerSide
     * @param isProviderSide
     */
    private void invokeBefore(Span span, EndPoint endPoint, long start, boolean isConsumerSide, boolean isProviderSide) {
        Tracer tracer = Tracer.getInstance();
        if (isConsumerSide && span.getSample()) {
            // 如果是消费者, ClientSend
            tracer.clientSend(span, endPoint, start);
        } else if (isProviderSide) {
            // 如果是提供者
            if (span.getSample()) {
                // ServerReceive
                tracer.serverReceive(span, endPoint, start);
            }
            // 将该span作为parentSpan设置到ThreadLocal中
            tracer.setParentSpan(span);
        }
    }

    /**
     * 调用具体逻辑之后，记录相关的annotation、去除对应的parentSpan
     * @param span
     * @param endPoint
     * @param end
     * @param isConsumerSide
     * @param isProviderSide
     */
    private void invokeAfter(Span span, EndPoint endPoint, long end, boolean isConsumerSide, boolean isProviderSide) {
        Tracer tracer = Tracer.getInstance();
        if (isConsumerSide && span.getSample()) {
            // 如果是消费者, ClientReceive
            tracer.clientReceive(span, endPoint, end);
        } else if (isProviderSide) {
            // 如果是提供者
            if (span.getSample()) {
                // ServerSend
                tracer.serverSend(span, endPoint, end);
            }
            tracer.removeParentSpan();
        }
    }
}
