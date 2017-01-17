package com.alibaba.dubbo.config.parse;

import javax.servlet.ServletContext;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;

/**
 * web容器的配置解析
 * @author fangzhibin 2015年5月28日 下午4:12:55
 * @version V1.0   
 * @modify: {原因} by fangzhibin 2015年5月28日 下午4:12:55
 */
public class WebContainer {
	
	private static final Logger log = LoggerFactory.getLogger(WebContainer.class);
	private static final WebContainer INSTANCE = new WebContainer();
	// 普通http访问端口
	private Integer portHTTP;
	// https访问端口
	private Integer portSSL;
	// ajp访问端口
	private Integer portAJP;
	// 上下文
	private String ctxPath = "";
	
	private WebContainer() {
	}
	
	public static WebContainer getInstance() {
		return INSTANCE;
	}
	
	/**
	 * 根据类型自动解析web容器配置
	 * @author fangzhibin 2015年5月28日 下午4:13:03
	 * @param servletContext
	 * @modify: {原因} by fangzhibin 2015年5月28日 下午4:13:03
	 */
	public void parse(ServletContext servletContext) {
		if (null != servletContext) {
			ctxPath = servletContext.getContextPath();
			if (StringUtils.equalsIgnoreCase(servletContext.getClass().getName(), "org.apache.catalina.core.ApplicationContextFacade")) {
				boolean result = TomcatConfigParse.parse(servletContext);
				if (!result) {
					log.warn("系统初始化时没有解析到tomcat的端口配置信息，使用默认的配置信息");
				}
			} else if (StringUtils.equalsIgnoreCase(servletContext.getClass().getName(), "org.eclipse.jetty.webapp.WebAppContext$Context")) {
				boolean result = JettyConfigParse.parse(servletContext);
				if (!result) {
					log.warn("系统初始化时没有解析到jetty的端口配置信息，使用默认的配置信息");
				}
			}
		}
	}
	
	public Integer getPortHTTP() {
		return portHTTP;
	}
	
	public Integer getPortSSL() {
		return portSSL;
	}
	
	public Integer getPortAJP() {
		return portAJP;
	}
	
	public String getCtxPath() {
		return ctxPath;
	}
	
	public void setCtxPath(String ctxPath) {
		this.ctxPath = ctxPath;
	}
	
	public void setPortHTTP(Integer portHTTP) {
		this.portHTTP = portHTTP;
	}
	
	public void setPortSSL(Integer portSSL) {
		this.portSSL = portSSL;
	}
	
	public void setPortAJP(Integer portAJP) {
		this.portAJP = portAJP;
	}
}
