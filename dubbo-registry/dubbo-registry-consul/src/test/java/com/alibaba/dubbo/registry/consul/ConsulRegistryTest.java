package com.alibaba.dubbo.registry.consul;

import com.alibaba.dubbo.common.URL;
import org.junit.Assert;
import org.junit.Test;

/**
 * consulRegistryTest
 *
 * @author fangzhibin
 */
public class ConsulRegistryTest {

	URL registryUrl = URL.valueOf("consul://10.6.130.124:8500");
	ConsulRegistry registry = new ConsulRegistry(registryUrl);

	@Test
	public void testIsAvailable() {
		Assert.assertTrue(registry.isAvailable());
	}

	@Test
	public void testRegister() {
	}

	@Test
	public void testDeregister() {
	}

}