package com.alibaba.dubbo.config.parse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;

/**
 * 通过ServletContext获取tomcat配置并解析
 * @author fangzhibin 2015年5月28日 下午4:13:45
 * @version V1.0   
 * @modify: {原因} by fangzhibin 2015年5月28日 下午4:13:45
 */
public class TomcatConfigParse {
	
	private static final Logger log = LoggerFactory.getLogger(TomcatConfigParse.class);
	
	/**
	 * 通过ServletContext解析tomcat的配置
	 * @author fangzhibin 2015年5月28日 下午4:13:53
	 * @param servletContext
	 * @return true：解析成功，端口地址有效，false：解析失败，端口地址无效
	 * @modify: {原因} by fangzhibin 2015年5月28日 下午4:13:53
	 */
	public static boolean parse(ServletContext servletContext) {
		boolean flag = false;
		try {
			Object applicationContext = FieldUtils.readField(servletContext, "context", true);
			if (null != applicationContext) {
				Object ctx = FieldUtils.readField(applicationContext, "context", true);
				if (null != ctx) {
					Object parent = MethodUtils.invokeExactMethod(ctx, "getParent");
					if (null != parent) {
						Object pparent = MethodUtils.invokeExactMethod(parent, "getParent");
						if (null != pparent) {
							Object service = MethodUtils.invokeExactMethod(pparent, "getService");
							if (null != service) {
								Object[] connectors = (Object[])MethodUtils.invokeExactMethod(service, "findConnectors");
								if (null != connectors && connectors.length > 0) {
									for (Object connector : connectors) {
										String protocol = (String)MethodUtils.invokeExactMethod(connector, "getProtocol");
										Integer port = (Integer)MethodUtils.invokeExactMethod(connector, "getPort");
										if (StringUtils.startsWithIgnoreCase(protocol, "HTTP/")) {
											Boolean SSLEnabled = (Boolean)MethodUtils.invokeExactMethod(connector, "getAttribute", "SSLEnabled");
											if (null != SSLEnabled && SSLEnabled.booleanValue()) {
												WebContainer.getInstance().setPortSSL(port);
												flag = true;
											} else {
												WebContainer.getInstance().setPortHTTP(port);
												flag = true;
											}
										} else if (StringUtils.startsWithIgnoreCase(protocol, "AJP/")) {
											WebContainer.getInstance().setPortAJP(port);
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("解析tomcat配置异常", e);
		}
		return flag;
	}
	
	/**
	 * 获取tomcat6的web发布端口,上诉parse方式由于servletContext的原因导致在tomcat6下解析不到端口
	 * @author fangzhibin 2015年6月16日 上午8:59:07
	 * @param protocol
	 * @param scheme
	 * @return
	 * @modify: {原因} by fangzhibin 2015年6月16日 上午8:59:07
	 */
	public static String getTomcat6HttpPort(String protocol, String scheme) {  
		if(null == protocol || null == scheme){
			return null;
		}
		ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
		if(null != mBeanServers && mBeanServers.size() > 0){
			MBeanServer mBeanServer = mBeanServers.get(0); 
			try{
				Set<ObjectName> names = mBeanServer.queryNames(new ObjectName("Catalina:type=Connector,*"), null);
				if(null != names){
					Iterator<ObjectName> it = names.iterator();  
				    ObjectName oname = null; 
				    while (it.hasNext()){
				    	oname = (ObjectName)it.next();  
				        String pvalue = (String)mBeanServer.getAttribute(oname, "protocol");  
				        String svalue = (String)mBeanServer.getAttribute(oname, "scheme");  
				        if (protocol.equals(pvalue) && scheme.equals(svalue)){
				        	Object sport = mBeanServer.getAttribute(oname, "port");
				        	if(null != sport && sport instanceof Integer)
				        		return sport.toString();
				        }  
				    }  
				}
			}catch (Exception e){  
		    	log.error(e.getMessage(), e);
		        return null;  
		    }  
			
		}
	    return null;  
	 }
}
