package com.alibaba.dubbo.remoting.http;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.container.spring.SpringContainer;
import com.alibaba.dubbo.remoting.http.jetty.JettyHttpBinder;
import com.alibaba.dubbo.remoting.http.servlet.ServletHttpBinder;

/**
 * 判断当前的Dubbo启动所在容器，根据不同的场合去绑定不同的Http请求
 * @author fangzhibin 2015年2月11日 下午2:32:20
 * @version V1.0   
 * @modify: {原因} by fangzhibin 2015年2月11日 下午2:32:20
 */
public class AutoBinder implements HttpBinder {
	
	private HttpBinder binder;
	
	public AutoBinder() {
		//FIXME 这个判断其实还是太准确
		if (SpringContainer.initializing || SpringContainer.getContext() != null) { // 是在单纯的Spring容器中运行
			binder = new JettyHttpBinder();
		} else {
			binder = new ServletHttpBinder();
		}
	}
	
	@Override
	public HttpServer bind(URL url, HttpHandler handler) {
		return binder.bind(url, handler);
	}
}
