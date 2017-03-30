package vision.apollo.common.indexcode;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import jef.tools.StringUtils;

/**
 * 中国行政区服务的测试类
 * @author jiyi
 */
public class RegionSereviceTest {
	
	private DefaultRegionService service = new DefaultRegionService();
	
	@Test
	public void testService() {
		// 打印出行政区总数
		System.out.println(service.getAll().size());
		Assert.assertEquals(3257, service.getAll().size());
		// 按树型结构打印
		printTree(service.getRoot(), 0);
		{
			// 按code获取地区
			Region r = service.getRegionById("330100");
			Assert.assertEquals("杭州市", r.getName());
			Assert.assertEquals(13, r.getChildren().size()); // 杭州下属的13个区县
		}
		{
			// 找出所有以编码3302开头的行政区
			List<Region> regions = service.getRegionStartsWith("3302");
			System.out.println(regions);
			Assert.assertEquals(12, regions.size()); // 宁波和下属的11个区县
		}
		{
			//
			List<Region> regions = service.findRegion("HZ");// 查找所有名称以HZ开头的行政区，
			System.out.println(regions);
			Assert.assertEquals(21, regions.size()); // 共有21个
		}
		{
			List<Region> regions = service.findRegion("HZ", Region.LEVEL_CITY);// 查找所有名称以HZ开头的地级行政区，
			System.out.println(regions);
			Assert.assertEquals(6, regions.size()); // 共有6个
		}
		{
			List<Region> regions = service.findRegion("HZ", "330000", false);// 查找浙江省范围内查找所有名称以HZ开头的地级行政区，
			System.out.println(regions);
			Assert.assertEquals(2, regions.size()); // 共有2个： 杭州，湖州
		}
		{
			List<Region> regions = service.findRegion("CS", "330000", false);// 查找在浙江省范围内查找所有名称以CS开头的地级行政区，
			Assert.assertEquals(0, regions.size()); // 找不到
			regions = service.findRegion("CS", "330000", true); // 在浙江省范围内查找所有名称以CS开头的行政区，
			System.out.println(regions);
			Assert.assertEquals(1, regions.size()); // 共有1个： 常山县
		}
		{
			List<Region> regions = service.findRegion("北", "000000", false);// 在全国范围内找以带有北字的省级行政区
			System.out.println(regions);
			Assert.assertEquals(3, regions.size()); // 共有3个：北京市,河北省, 湖北省
		}
		{
			List<Region> regions = service.findRegion("姚", "000000", true);// 在全国范围内找以带有姚字的所有行政区
			System.out.println(regions);
			Assert.assertEquals(3, regions.size()); // 共有3个：余姚市 姚安县 大姚县
		}
		{
			List<Region> regions = service.findRegion("3201", "000000", true);// 在全国范围内找以3201开头的行政区
			System.out.println(regions);
			Assert.assertEquals(14, regions.size()); // 南京市和下属区县，共14个
		}
	}
	
	@Test
	public void srarch2() {
		{
			List<Region> regions = service.findRegion("", "000000", false);// 查找全國所有的以及行政區，不遞歸
			System.out.println(regions);
		}
		{
			List<Region> regions = service.findRegion("", 1);// 查找全國所有的一級行政區
			System.out.println(regions);
		}
	}
	
	private void printTree(Region node, int i) {
		System.out.print(StringUtils.repeat(' ', i * 2));
		System.out.println(node);
		for (Region r : node.getChildren()) {
			printTree(r, i + 1);
		}
	}
}
