package com.alibaba.dubbo.trace.filter;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.RpcInvocation;

/**
 * Created by xiaobaxi on 2017/7/31.
 */
public class AttachmentUtil {
    /**
     * 将RpcInvocation的attachment数据转换成Long
     * @param invocation
     * @param key
     * @return
     */
    public static String getAttachment(RpcInvocation invocation, String key) {
        String value = invocation.getAttachment(key);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value;
    }

    /**
     * 将RpcInvocation的attachment数据转换成Boolean
     * @param invocation
     * @param key
     * @return
     */
    public static Boolean getAttachmentBoolean(RpcInvocation invocation, String key) {
        String value = invocation.getAttachment(key);
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return Boolean.valueOf(value);
    }
}
