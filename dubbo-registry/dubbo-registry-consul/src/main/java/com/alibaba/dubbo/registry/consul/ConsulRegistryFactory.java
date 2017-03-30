package com.alibaba.dubbo.registry.consul;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.registry.Registry;
import com.alibaba.dubbo.registry.RegistryFactory;

/**
 * ZookeeperRegistryFactory.
 * 
 * @author fangzhibin
 */
public class ConsulRegistryFactory implements RegistryFactory {
	
	public Registry getRegistry(URL url) {
        return new ConsulRegistry(url);
    }

}