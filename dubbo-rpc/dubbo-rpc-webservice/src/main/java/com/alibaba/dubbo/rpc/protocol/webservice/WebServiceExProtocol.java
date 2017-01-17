package com.alibaba.dubbo.rpc.protocol.webservice;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.SocketTimeoutException;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.wsdl.service.factory.ReflectionServiceFactoryBean;
import org.springframework.core.annotation.AnnotationUtils;
import com.alibaba.dubbo.common.Constants;
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
import com.alibaba.dubbo.rpc.protocol.webservice.support.CXFService;

/**
 * @author fangzhibin 2015年1月29日 下午2:46:37
 * @version V1.0
 * @modify: {原因} by fangzhibin 2015年1月29日 下午2:46:37
 */
public class WebServiceExProtocol extends AbstractProxyProtocol {
	
	public static final int DEFAULT_PORT = 80;
	private static final Logger logger = LoggerFactory.getLogger(WebServiceExProtocol.class);
	
	private static final String CXF_PLUS_SERVICE_JAXWS = "com.github.cxfplus.jaxws.support.CXFPlusServiceFactoryBean";
	private static final String CXF_PLUS_SERVICE_SIMPLE = "com.github.cxfplus.service.factory.CXFPlusServiceBean";
	private static final String CXF_PLUS_CLIENT_FACTORY = "com.github.cxfplus.jaxws.CXFPlusClientFactoryBean";
	private HttpBinder httpBinder;
	
	public WebServiceExProtocol() {
		super(Fault.class);
	}
	
	public void setHttpBinder(HttpBinder httpBinder) {
		this.httpBinder = httpBinder;
	}
	
	public int getDefaultPort() {
		return DEFAULT_PORT;
	}
	
	protected <T> Runnable doExport(T impl, Class<T> type, URL url) throws RpcException {
		String addr = url.getIp() + ":" + url.getPort();
		HttpServer httpServer = ServerHolder.getInstance().get(addr);
		CXFHandler handler = null;
		if (httpServer == null) {
			httpServer = httpBinder.bind(url, new WebServiceHandler());
			ServerHolder.getInstance().put(addr, httpServer);
		}
		handler = (CXFHandler)httpServer.getHttpHandler();
		final ServerFactoryBean serverFactoryBean;
		Annotation anno = type.getAnnotation(CXFService.class);;
		if (anno != null) {
			boolean useCxfPlus = (Boolean)AnnotationUtils.getValue(anno, "useCxfPlus");
			boolean useJaxWs = (Boolean)AnnotationUtils.getValue(anno, "useJaxWs");
			if (useCxfPlus && useJaxWs) {
				serverFactoryBean = new JaxWsServerFactoryBean(createCxfPlusJaxWs());
			} else if (useJaxWs) {
				serverFactoryBean = new JaxWsServerFactoryBean();
			} else if (useCxfPlus) {
				serverFactoryBean = new ServerFactoryBean(createCxfPlus());
			} else {
				serverFactoryBean = new ServerFactoryBean();
			}
		} else {
			String s = ConfigUtils.getProperty("dubbo.webservie.factory");
			if ("jaxws".equals(s)) {
				serverFactoryBean = new JaxWsServerFactoryBean();
			} else if ("jaxws+".equals(s)) {
				serverFactoryBean = new JaxWsServerFactoryBean(createCxfPlusJaxWs());
			} else {
				serverFactoryBean = new ServerFactoryBean();
			}
		}
		String address = url.getAbsolutePath();
		int pos = address.lastIndexOf("/");
		if(pos > 0) {
			address = address.substring(pos);
		}
		serverFactoryBean.setAddress(address);
		serverFactoryBean.setServiceClass(type);
		serverFactoryBean.setServiceBean(impl);
		if (null != handler) {
			serverFactoryBean.setBus(handler.getBus());
			serverFactoryBean.setDestinationFactory(handler.getTransportFactory());
		}
		final Server server = serverFactoryBean.create();
		return new Runnable() {
			
			public void run() {
				server.destroy();
			}
		};
	}
	
	private ReflectionServiceFactoryBean createCxfPlus() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) cl = this.getClass().getClassLoader();
		try {
			Class<?> cls = cl.loadClass(CXF_PLUS_SERVICE_SIMPLE);
			return (ReflectionServiceFactoryBean)cls.newInstance();
		} catch (Exception e) {
			logger.warn("create CXF plus Service failure, please makesure there is cxf-plus library in classpath.", e);
			return new ReflectionServiceFactoryBean();
		}
	}
	
	private JaxWsServiceFactoryBean createCxfPlusJaxWs() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) cl = this.getClass().getClassLoader();
		try {
			Class<?> cls = cl.loadClass(CXF_PLUS_SERVICE_JAXWS);
			return (JaxWsServiceFactoryBean)cls.newInstance();
		} catch (Exception e) {
			logger.warn("create CXF plus Service failure, please makesure there is cxf-plus library in classpath.", e);
			return new JaxWsServiceFactoryBean();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T doRefer(final Class<T> serviceType, final URL url) throws RpcException {
		ClientProxyFactoryBean proxyFactoryBean;
		Annotation anno = serviceType.getAnnotation(CXFService.class);
		if (anno != null) {
			boolean useCxfPlus = (Boolean)AnnotationUtils.getValue(anno, "useCxfPlus");
			boolean useJaxWs = (Boolean)AnnotationUtils.getValue(anno, "useJaxWs");
			if (useCxfPlus && useJaxWs) {
				proxyFactoryBean = new JaxWsProxyFactoryBean(getCxfPlusClientFactory());
			} else if (useJaxWs) {
				proxyFactoryBean = new JaxWsProxyFactoryBean();
			} else if (useCxfPlus) {
				proxyFactoryBean = new ClientProxyFactoryBean(new ClientFactoryBean(createCxfPlus()));
			} else {
				proxyFactoryBean = new ClientProxyFactoryBean();
			}
		} else {
			String s = ConfigUtils.getProperty("dubbo.webservie.factory");
			if ("jaxws".equals(s)) {
				proxyFactoryBean = new JaxWsProxyFactoryBean();
			} else if ("jaxws+".equals(s)) {
				proxyFactoryBean = new JaxWsProxyFactoryBean(getCxfPlusClientFactory());
			} else {
				proxyFactoryBean = new ClientProxyFactoryBean();
			}
		}
		
		String address = url.setProtocol("http").toIdentityString();
		if (logger.isDebugEnabled()) {
			logger.debug("creating " + proxyFactoryBean.getClass().getName() + " to:" + address);
		}
		//为了解决在调用不同系统的服务时，由于系统的contextPath获取不到服务的情况，默认在webservice跟restful的protocol中显示的配置contextPath
		//eg. <dubbo:protocol name="webservice" port="8087" server="auto" contextpath="apollo-web" />
		String[] tuple = new String[2];
		int pos = address.lastIndexOf("/");
        if(pos > 0) {
        	tuple[0] = address.substring(0, pos);
        	tuple[1] = address.substring(pos);
        }
		StringBuilder fullAddress = new StringBuilder();
		fullAddress.append(tuple[0]);
		fullAddress.append(getDefaultPath());
		fullAddress.append(tuple[1]);
		//获取到http://ip:port/contextPath/services/serviceName这样的一个wsdl地址
		proxyFactoryBean.setAddress(fullAddress.toString());
		proxyFactoryBean.setServiceClass(serviceType);
        //proxyFactoryBean.setBus(handler.getBus());
		T ref = (T)proxyFactoryBean.create();
		Client proxy = ClientProxy.getClient(ref);  
		HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
		HTTPClientPolicy policy = new HTTPClientPolicy();
		policy.setConnectionTimeout(url.getParameter(Constants.CONNECT_TIMEOUT_KEY, Constants.DEFAULT_CONNECT_TIMEOUT));
		policy.setReceiveTimeout(url.getParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT));
		conduit.setClient(policy);
		return ref;
	}
	
	private ClientFactoryBean getCxfPlusClientFactory() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) cl = this.getClass().getClassLoader();
		try {
			Class<?> cls = cl.loadClass(CXF_PLUS_CLIENT_FACTORY);
			return (ClientFactoryBean)cls.newInstance();
		} catch (Exception e) {
			logger.warn("create CXF plus Service failure, please makesure there is cxf-plus library in classpath.", e);
			return new ClientFactoryBean();
		}
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
	
	protected int getErrorCode(Throwable e) {
		if (e instanceof Fault) {
			e = e.getCause();
		}
		if (e instanceof SocketTimeoutException) {
			return RpcException.TIMEOUT_EXCEPTION;
		} else if (e instanceof IOException) {
			return RpcException.NETWORK_EXCEPTION;
		}
		return super.getErrorCode(e);
	}
}
