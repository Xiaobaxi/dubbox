package com.alibaba.dubbo.remoting.http.cxf;

import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.SoapTransportFactory;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.apache.cxf.wsdl11.WSDLEndpointFactory;

/**
 * 在升级到CXF 3.1.5之后，我们发现一个问题，即生成的WSDL中，最后的service/port/address中的地址不见了。
 * 从代码看，和HTTPTransportFactory构造中去除了ExtensionManagerBus有关。
 * 但是怎么调整也无法恢复这个Address信息。最后发现其实bus并没有丢掉，在{@link #createEndpointInfo}方法时新增了bus参数，
 * 因此并不会因为没有bus而无法计算出地址。
 * <p>
 * 跟踪代码发现，新旧版本的区别在于旧版本的EndpointInfo中有一个extensor对象，里面存储了URL地址信息。
 * 而这个extensor对象只有在DestinationFactory同时也实现了WSDLEndpointFactory接口的时候才会生成。
 * <p>
 * 个人看法：这是CXF 3.x在重构过程中出现的兼容性下降甚至是bug。目前暂时通过设计一个复合类的方法使其向下兼容。
 * 期待CXF在后续的版本中解决这个问题。
 * <p>
 * Test on Apache CXF 3.1.6
 * 
 * @author fangzhibin 2016-4-23
 */
final class MyHTTPTransportFactory extends HTTPTransportFactory implements WSDLEndpointFactory{
	private WSDLEndpointFactory fac=new SoapTransportFactory();
	
	/*
	 * (non-Javadoc)
	 * @see org.apache.cxf.wsdl11.WSDLEndpointFactory#createEndpointInfo(org.apache.cxf.Bus, org.apache.cxf.service.model.ServiceInfo, org.apache.cxf.service.model.BindingInfo, java.util.List)
	 */
	@Override
	public EndpointInfo createEndpointInfo(Bus bus, ServiceInfo serviceInfo, BindingInfo b, List<?> extensions) {
		return fac.createEndpointInfo(bus, serviceInfo, b, extensions);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.cxf.wsdl11.WSDLEndpointFactory#createPortExtensors(org.apache.cxf.Bus, org.apache.cxf.service.model.EndpointInfo, org.apache.cxf.service.Service)
	 */
	@Override
	public void createPortExtensors(Bus bus, EndpointInfo ei, Service service) {
		fac.createPortExtensors(bus, ei, service);
	}
	
}
