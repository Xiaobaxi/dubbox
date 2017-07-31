package com.alibaba.dubbo.registry.consul;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.registry.Registry;
import com.alibaba.dubbo.registry.support.AbstractRegistryFactory;

/**
 * consul registry factory
 *
 * @author fangzhibin
 */
public class ConsulRegistryFactory extends AbstractRegistryFactory {

    public Registry createRegistry(URL url) {
        return new ConsulRegistry(url);
    }

}
