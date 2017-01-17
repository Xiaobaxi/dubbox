package com.alibaba.dubbo.remoting.http.cxf;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.bus.extension.ExtensionManagerBus;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.apache.cxf.transport.http.HttpDestinationFactory;
import org.apache.cxf.transport.servlet.ServletController;
import org.apache.cxf.transport.servlet.ServletDestinationFactory;

import com.alibaba.dubbo.remoting.http.HttpHandler;
import com.alibaba.dubbo.remoting.http.servlet.DispatcherServlet;

/**
 * @author fangzhibin 2015年1月29日 上午10:38:03
 * @version V1.0
 * @modify: {原因} by fangzhibin 2015年1月29日 上午10:38:03
 */
public abstract class CXFHandler implements HttpHandler {

	private volatile ServletController servletController;
	private final ExtensionManagerBus bus = new ExtensionManagerBus();
	private MyHTTPTransportFactory transportFactory;

	public CXFHandler() {
		transportFactory = new MyHTTPTransportFactory();
		bus.setExtension(new ServletDestinationFactory(), HttpDestinationFactory.class);
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (servletController == null) {
			DispatcherServlet httpServlet = DispatcherServlet.getInstance();
			if (httpServlet == null) {
				response.sendError(500, "No such DispatcherServlet instance.");
				return;
			}
			synchronized (this) {
				if (servletController == null) {
					servletController = new ServletController(transportFactory.getRegistry(), httpServlet.getServletConfig(), new CXFServiceListGeneratorServlet(transportFactory.getRegistry(), bus));
				}
			}
		}
		setRemoteAddress(request, response);
		// RpcContext.getContext().setRemoteAddress(request.getRemoteAddr(),
		// request.getRemotePort());
		request.getContextPath();
		servletController.invoke(request, response);
	}

	public ExtensionManagerBus getBus() {
		return bus;
	}

	public HTTPTransportFactory getTransportFactory() {
		return transportFactory;
	}

	public abstract void setRemoteAddress(HttpServletRequest request, HttpServletResponse response);
}
