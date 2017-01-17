/**
 * Copyright 1999-2014 dangdang.com.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.rpc.protocol.jaxrs;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.remoting.http.HttpBinder;
import com.alibaba.dubbo.remoting.http.HttpServer;
import com.alibaba.dubbo.remoting.http.cxf.CXFHandler;
import com.alibaba.dubbo.remoting.http.cxf.ServerHolder;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.protocol.AbstractProxyProtocol;

/**
 * @author lishen
 */
public class RestProtocol extends AbstractProxyProtocol {
	
	public static final int DEFAULT_PORT = 80;
	private static final Logger logger = LoggerFactory.getLogger(RestProtocol.class);
	// 调试开关,打出报文
	private boolean trace;
	private HttpBinder httpBinder;
	
	public RestProtocol() {
		super(Fault.class);
	}
	
	public void setHttpBinder(HttpBinder httpBinder) {
		this.httpBinder = httpBinder;
	}
	
	@Override
	public int getDefaultPort() {
		return DEFAULT_PORT;
	}
	
	@Override
	protected <T> Runnable doExport(T impl, Class<T> type, URL url) throws RpcException {
		String addr = url.getIp() + ":" + url.getPort();
		HttpServer httpServer = ServerHolder.getInstance().get(addr);
		CXFHandler handler = null;
		if (httpServer == null) {
			httpServer = httpBinder.bind(url, new RestHandler());
			ServerHolder.getInstance().put(addr, httpServer);
		}
		handler = (CXFHandler)httpServer.getHttpHandler();
		final JAXRSServerFactoryBean serverFactoryBean = new JAXRSServerFactoryBean();
		// "/rest"+
		String prefix = ConfigUtils.getProperty("dubbo.restful.basepath");
		if (StringUtils.isEmpty(prefix)) {
			prefix = "/rest";
		} else if (prefix.length() == 1) {
			prefix = "";
		}
		boolean useJsonWithClassNames = "true".equalsIgnoreCase(ConfigUtils.getProperty("dubbo.restful.fastjson.writeclass"));
		serverFactoryBean.setProvider(new FastJSONProvider(true, useJsonWithClassNames));
		String path = url.getAbsolutePath();
		int pos = path.lastIndexOf("/");
        if(pos > 0) {
        	path = path.substring(pos);
        }
		serverFactoryBean.setAddress(prefix + path);
		serverFactoryBean.setResourceClasses(type);
		serverFactoryBean.setResourceProvider(type, new SingletonResourceProvider(impl));
		if (logger.isInfoEnabled()) {
			logger.info("Publishing JAX-RS Service " + type.getName() + "  " + impl);
		}
		if (trace) {
			serverFactoryBean.getInInterceptors().add(new LoggingInInterceptor());
			serverFactoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
		}
		if (null != handler) {
			serverFactoryBean.setBus(handler.getBus());
			serverFactoryBean.setDestinationFactory(handler.getTransportFactory());
		}
		serverFactoryBean.create();
		// logger.info("发布了" + serverFactoryBean.getAddress());
		return new Runnable() {
			
			public void run() {
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T> T doRefer(Class<T> type, URL url) throws RpcException {
		JAXRSClientFactoryBean proxyFactoryBean = new JAXRSClientFactoryBean();
		// 对于rest额外增加一个配置dubbo.restful.basepath来区分webservice的发布地址，没有配置默认为rest
		String prefix = ConfigUtils.getProperty("dubbo.restful.basepath");
		if (StringUtils.isEmpty(prefix)) {
			prefix = "/rest";
		} else if (prefix.length() == 1) {
			prefix = "";
		}
		String address = url.setProtocol("http").toIdentityString();
		//为了解决在调用不同系统的服务时，由于系统的contextPath获取不到服务的情况，默认在webservice跟restful的protocol中显示的配置contextPath
		//eg. <dubbo:protocol name="jaxrs" port="8087" server="auto" contextpath="apollo-web" />
		String[] tuple = new String[2];
		int pos = address.lastIndexOf("/");
        if(pos > 0) {
        	tuple[0] = address.substring(0, pos);
        	tuple[1] = address.substring(pos);
        }
		StringBuilder fullAddress = new StringBuilder();
		fullAddress.append(tuple[0]);
		fullAddress.append(getDefaultPath());
		fullAddress.append(prefix);
		fullAddress.append(tuple[1]);
		proxyFactoryBean.setAddress(fullAddress.toString());
		proxyFactoryBean.setServiceClass(type);
		proxyFactoryBean.setProvider(new FastJSONProvider(true, false));
		if (trace) {
			proxyFactoryBean.getInInterceptors().add(new LoggingInInterceptor());
			proxyFactoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
		}
		// proxyFactoryBean.setBus(CXFHandler.getBus());
		T client = (T)proxyFactoryBean.create();
		return client;
	}
	
	/*
	 * 获取默认的扩展path，在dubbo.properties中默认为/services/*,通过以上获取到defaultPath为/services
	 */
	private String getDefaultPath() {
		String defaultPath = ConfigUtils.getProperty("dubbo.jetty.server.path");
		if (StringUtils.isEmpty(defaultPath)) {
			defaultPath = "";
		} else {
			if (!defaultPath.startsWith("/")) {
				defaultPath = "/" + defaultPath;
			}
			defaultPath = defaultPath.replace("/*", "");
		}
		return defaultPath;
	}
}
