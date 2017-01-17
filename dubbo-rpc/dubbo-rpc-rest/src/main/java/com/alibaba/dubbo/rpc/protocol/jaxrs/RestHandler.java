package com.alibaba.dubbo.rpc.protocol.jaxrs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.dubbo.remoting.http.cxf.CXFHandler;
import com.alibaba.dubbo.rpc.RpcContext;

/**
 * @author fangzhibin 2015年1月29日 上午10:38:18
 * @version V1.0
 * @modify: {原因} by fangzhibin 2015年1月29日 上午10:38:18
 */
public class RestHandler extends CXFHandler {
	
	@Override
	public void setRemoteAddress(HttpServletRequest request, HttpServletResponse response) {
		RpcContext.getContext().setRemoteAddress(request.getRemoteAddr(), request.getRemotePort());
	}
}
