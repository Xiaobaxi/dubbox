package com.alibaba.dubbo.remoting.http.cxf;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.Bus;
import org.apache.cxf.transport.http.DestinationRegistry;
import org.apache.cxf.transport.servlet.servicelist.ServiceListGeneratorServlet;

/**
 * 
 * @author fangzhibin 2015年1月29日 上午10:14:23
 * @version V1.0   
 * @modify: {原因} by fangzhibin 2015年1月29日 上午10:14:23
 */
public class CXFServiceListGeneratorServlet extends ServiceListGeneratorServlet {
	
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 7720999347741061265L;
	
	public CXFServiceListGeneratorServlet(DestinationRegistry destinationRegistry, Bus bus) {
		super(destinationRegistry, bus);
	}
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("stylesheet") != null) {
			return;
		}
		super.service(request, response);
	}
}
