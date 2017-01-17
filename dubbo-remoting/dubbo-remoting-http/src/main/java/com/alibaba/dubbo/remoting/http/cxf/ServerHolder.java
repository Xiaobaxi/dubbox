package com.alibaba.dubbo.remoting.http.cxf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.alibaba.dubbo.remoting.http.HttpServer;

/**
 * 
 * @author fangzhibin 2015年1月29日 上午10:35:09
 * @version V1.0   
 * @modify: {原因} by fangzhibin 2015年1月29日 上午10:35:09
 */
public class ServerHolder {
	
	static ServerHolder instance = new ServerHolder();
	
	public static ServerHolder getInstance() {
		return instance;
	}
	
	private final Map<String, HttpServer> serverMap = new ConcurrentHashMap<String, HttpServer>();
	
	public HttpServer get(String str) {
		return serverMap.get(str);
	}
	
	public HttpServer put(String key, HttpServer value) {
		return serverMap.put(key, value);
	}
}
