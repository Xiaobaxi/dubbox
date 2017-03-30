package com.alibaba.dubbo.registry.consul;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.registry.NotifyListener;
import com.alibaba.dubbo.registry.support.FailbackRegistry;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * consul registry
 *
 * @author fangzhibin
 */
public class ConsulRegistry extends FailbackRegistry {

	private static final Logger logger = LoggerFactory.getLogger(ConsulRegistry.class);
	ConsulClient consulClient;

	private static final int DEFAULT_CONSUL_PORT = 8500;

	private final ConcurrentMap<String, NotifyListenerConsulWrapper> notifyListenerConsulWrappers = new ConcurrentHashMap<String, NotifyListenerConsulWrapper>();

	public ConsulRegistry(URL url) {
		super(url);
		if (url.isAnyHost()) {
			throw new IllegalStateException("registry address == null");
		}
		consulClient = new ConsulClient(url.getHost(), url.getPort(DEFAULT_CONSUL_PORT));
	}

	@Override
	public boolean isAvailable() {
		return !consulClient.getDatacenters().getValue().isEmpty();
	}

	@Override
	protected void doRegister(URL url) {
		NewService consulService = new NewService();
		consulService.setAddress(url.toFullString());
		consulService.setPort(url.getPort(DEFAULT_CONSUL_PORT));
		consulService.setId(convertConsulSerivceId(url));
		consulService.setName(url.getServiceInterface());
		consulClient.agentServiceRegister(consulService);
	}

	@Override
	protected void doUnregister(URL url) {
		consulClient.agentServiceDeregister(convertConsulSerivceId(url));
	}

	@Override
	protected void doSubscribe(URL url, NotifyListener listener) {
		String serviceName = url.getServiceInterface();
		NotifyListenerConsulWrapper wrapper = new NotifyListenerConsulWrapper(listener, consulClient, serviceName);
		if (notifyListenerConsulWrappers.isEmpty()) {
			ServiceLookupThread lookupThread = new ServiceLookupThread();
			lookupThread.setDaemon(true);
			lookupThread.start();
		}
		if (!notifyListenerConsulWrappers.containsKey(serviceName)) {
			notifyListenerConsulWrappers.put(serviceName, wrapper);
			wrapper.sync();
		}
	}

	@Override
	protected void doUnsubscribe(URL url, NotifyListener listener) {
		notifyListenerConsulWrappers.remove(url.getServiceInterface());
	}

	public static String convertConsulSerivceId(URL url) {
		if (url == null) {
			return null;
		}
		return convertServiceId(url.getHost(), url.getPort(DEFAULT_CONSUL_PORT), url.getPath());
	}

	public static String convertServiceId(String host, int port, String path) {
		return host + ":" + port + "-" + path;
	}

	private class ServiceLookupThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					sleep(15000);
					synchronized (notifyListenerConsulWrappers) {
						for (NotifyListenerConsulWrapper wrapper : notifyListenerConsulWrappers.values()) {
							wrapper.sync();
						}
					}
				} catch (Throwable e) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException ignored) {
						logger.warn(ignored.getMessage());
					}
				}
			}
		}
	}
}
