/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.remoting.http.jetty;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.thread.QueuedThreadPool;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.remoting.http.HttpHandler;
import com.alibaba.dubbo.remoting.http.servlet.DispatcherServlet;
import com.alibaba.dubbo.remoting.http.support.AbstractHttpServer;

public class JettyHttpServer extends AbstractHttpServer {

	private static final Logger logger = LoggerFactory.getLogger(JettyHttpServer.class);

	private Server server;

	public JettyHttpServer(URL url, final HttpHandler handler){
        super(url, handler);
        DispatcherServlet.addHttpHandler(url.getPort(), handler);
        
        int threads = url.getParameter(Constants.THREADS_KEY, Constants.DEFAULT_THREADS);
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setDaemon(true);
        threadPool.setMaxThreads(threads);
        threadPool.setMinThreads(threads);

        SelectChannelConnector connector = new SelectChannelConnector();
        if (! url.isAnyHost() && NetUtils.isValidLocalHost(url.getHost())) {
            connector.setHost(url.getHost());
        }
        connector.setPort(url.getPort());

        server = new Server();
        server.setThreadPool(threadPool);
        server.addConnector(connector);
        
    	ContextHandler ch=new ContextHandler(getContext());
		{
			ServletHolder servletHolder = new ServletHolder(new DispatcherServlet());
			servletHolder.setInitOrder(2);
			ServletHandler servletHandler = new ServletHandler();
			String defaultPath = ConfigUtils.getProperty("dubbo.jetty.server.path");
			if (StringUtils.isEmpty(defaultPath)) {
				defaultPath = "/*";
			}
			servletHandler.addServletWithMapping(servletHolder, defaultPath);
			ch.addHandler(servletHandler);
		}
		
		Handler h = server.getHandler();
		if (h instanceof HandlerCollection) {
			((HandlerCollection)h).addHandler(ch);
		} else if (h == null) {
			server.setHandler(ch);
		} else {
			HandlerCollection hs = new HandlerCollection();
			hs.addHandler(h);
			hs.addHandler(ch);
			server.setHandler(hs);
		}
		try {
			server.start();
		} catch (Exception e) {
			throw new IllegalStateException("Failed to start jetty server on " + url.getAddress() + ", cause: " + e.getMessage(), e);
		}
    }

	private String getContext() {
		String defaultContext = ConfigUtils.getProperty("dubbo.jetty.server.context");
		if (StringUtils.isEmpty(defaultContext)) {
			return "";
		} else if (!defaultContext.startsWith("/")) {
			return  "/" + defaultContext;
		}else{
			return defaultContext;
		}
	}

	public void close() {
		super.close();
		if (server != null) {
			try {
				server.stop();
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}
		}
	}

}