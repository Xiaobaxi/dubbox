package com.alibaba.dubbo.config.parse;

import javax.servlet.ServletContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;

/**
 * 通过ServletContext解析jetty的配置
 * @author fangzhibin 2015年5月28日 下午4:14:28
 * @version V1.0   
 * @modify: {原因} by fangzhibin 2015年5月28日 下午4:14:28
 */
public class JettyConfigParse {
	
	private static final Logger log = LoggerFactory.getLogger(JettyConfigParse.class);
	
	/**
	 * 通过ServletContext解析jetty的配置
	 * @author fangzhibin 2015年5月28日 下午4:14:20
	 * @param servletContext
	 * @return true：解析成功，端口地址有效，false：解析失败，端口地址无效
	 * @modify: {原因} by fangzhibin 2015年5月28日 下午4:14:20
	 */
	public static boolean parse(ServletContext servletContext) {
		boolean flag = false;
		try {
			log.info(servletContext.getServletContextName());
			Object handler = MethodUtils.invokeExactMethod(servletContext, "getContextHandler");
			if(null != handler){
				Object server = MethodUtils.invokeExactMethod(handler, "getServer");
				if(null != server){
					Object[] connectors = (Object[])MethodUtils.invokeExactMethod(server, "getConnectors");
					if(null != connectors && connectors.length > 0){
						for(Object connector : connectors){
							if(StringUtils.equalsIgnoreCase(connector.getClass().getName(), "org.eclipse.jetty.server.nio.SelectChannelConnector")){
								WebContainer.getInstance().setPortHTTP((Integer)MethodUtils.invokeExactMethod(connector, "getPort"));
								flag = true;
							} else if(StringUtils.equalsIgnoreCase(connector.getClass().getName(), "org.eclipse.jetty.server.ssl.SslSelectChannelConnector")){
								WebContainer.getInstance().setPortSSL((Integer)MethodUtils.invokeExactMethod(connector, "getPort"));
								flag = true;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("解析jetty配置异常", e);
		}
		return flag;
	}
}
