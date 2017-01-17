package com.alibaba.dubbo.rpc.protocol.webservice.support;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE}) 
@Retention(RUNTIME)
public @interface CXFService {
	
	/**
	 * 使用JAX-WS注解进行发布
	 * @return
	 */
	boolean useJaxWs() default false;
	
	/**
	 * 使用CXF-Plus的框架在CXF基础上进行泛型解析和发布
	 * @return
	 */
	boolean useCxfPlus() default false; 
}

