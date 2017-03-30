package vision.apollo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import vision.apollo.common.indexcode.Region;
import vision.apollo.common.indexcode.RegionService;

/**
 * 使用Junit方式来启动Dobbo服务
 * 
 * 在Spring-test中启动的服务包括 ZooKeeper、Dobbu、Jetty
 * 
 * @author jiyi
 * 
 */
@ContextConfiguration({ "classpath:applicationContext-common.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class DobboStartTest  {
	private static ApplicationContext client;

	@Test
	public void testImportService() {
		RegionService service = (RegionService) getClient().getBean("regionService");
		
		Region region = service.getRegionById("330100");
		System.out.println(region);
		Assert.assertEquals("杭州市",region.getName());
	}

	private ApplicationContext getClient() {
		if(client==null){
			client= new ClassPathXmlApplicationContext("spring/import-common.xml");
		}
		return client;
	}
}
