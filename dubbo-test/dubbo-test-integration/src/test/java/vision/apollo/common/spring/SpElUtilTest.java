package vision.apollo.common.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:vision/apollo/common/spring/beans.xml" })
public class SpElUtilTest implements ApplicationContextAware {
	// spring配置文件上下文
	ApplicationContext context = null;

	// spring el测试辅助类
	SpElContextBean bean = null;

	// 表达式解析对象
	ExpressionParser exp = null;

	// 标准赋值上下文
	StandardEvaluationContext secontext;

	@Before
	public void setUp() throws Exception {
		bean = context.getBean(SpElContextBean.class);
		secontext = new StandardEvaluationContext(bean);
		exp = new SpelExpressionParser();
	}

	@After
	public void tearDown() throws Exception {
		context = null;
		bean = null;
		secontext = null;
		exp = null;
	}

	/**
	 * 文字表达式测试用例
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSpELLiteralExpression() throws Exception {
		// 定义各种文字表达式
		String[] lELs = { "'hello SpEL'", "1.028E+7", "0x12EF", "true", "null" };
		assertEquals("hello SpEL", exp.parseExpression(lELs[0]).getValue(String.class));
		assertEquals(new Double(10280000), exp.parseExpression(lELs[1]).getValue(Double.class));
		assertEquals(new Integer(4847), exp.parseExpression(lELs[2]).getValue(Integer.class));
		assertTrue(exp.parseExpression(lELs[3]).getValue(Boolean.class));
		assertNull(exp.parseExpression(lELs[4]).getValue());
	}

	/**
	 * 访问属性、数组、集合和 map 测试
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSpELProOrArrayOrIndexEtcExpression() throws Exception {
		// 属性测试。time为SpElUtil类Date型数据，这里调用Date的属性Year
		assertEquals(new Integer(2014), exp.parseExpression("time.Year + 1900").getValue(secontext, Integer.class));
		System.out.println(exp.parseExpression("innerClass").getValue(secontext));
		// 属性测试。innerClass为SpElUtil类中引入的其他类。
		assertEquals(29, exp.parseExpression("innerClass.age").getValue(secontext));
		// 设置SpElUtil类的numbers属性
		bean.setNumbers(Arrays.asList(2, 3, 4, 5, 6, 7, 9));
		// 访问对象属性数组通过索引
		assertEquals(2, exp.parseExpression("numbers[0]").getValue(secontext));
		// 访问map
		assertEquals("string1", exp.parseExpression("maps[1]").getValue(secontext, String.class));
	}

	/**
	 * 内联list测试
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSpELInnerListExpression() throws Exception {
		// 构造list
		List<String> nums = (List<String>) exp.parseExpression("{'a','b','c','d'}").getValue();
		assertEquals(Arrays.asList("a", "b", "c", "d"), nums);
		// 构造List<List<>>
		List listOfLists = (List) exp.parseExpression("{{1,2},{3,4}}").getValue(secontext);
		assertEquals(Arrays.asList(1, 2), listOfLists.get(0));
	}

	/**
	 * Array 构造测试
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSpELArrayConstructionExcpression() throws Exception {
		// 创建没有初始值的数组
		int[] a = (int[]) exp.parseExpression("new int[4]").getValue();
		assertEquals(4, a.length);
		// 创建带有初始值的数组
		int[] b = (int[]) exp.parseExpression("new int[4]{1,2,3,4}").getValue();
		assertEquals(3, b[2]);
		// 创建二维数组
		int[][] c = (int[][]) exp.parseExpression("new int[4][5]").getValue();
		assertEquals(4, c.length);
		assertEquals(5, c[0].length);
	}

	/**
	 * 方法表达式测试
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSpELMethodExcpression() throws Exception {
		// String.replace方法测试
		assertEquals("abC2def", exp.parseExpression("'abcdef'.replace('c','C2')").getValue(String.class));
		// 自定义类方法测试
		assertFalse(exp.parseExpression("innerClass.isGt30ForAge()").getValue(secontext, Boolean.class));
		bean.getInnerClass().setAge(34);
		assertTrue(exp.parseExpression("innerClass.isGt30ForAge()").getValue(secontext, Boolean.class));
		bean.getInnerClass().setAge(29);
	}

	/**
	 * 操作符、正则表达式测试
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSpElOperatorAndRegExpression() throws Exception {
		// 关系操作
		assertTrue(exp.parseExpression("1 == 1").getValue(Boolean.class));
		assertTrue(exp.parseExpression("1 eq 1").getValue(Boolean.class));
		assertTrue(exp.parseExpression("1 > -1").getValue(Boolean.class));
		assertTrue(exp.parseExpression("1 gt -1").getValue(Boolean.class));
		assertTrue(exp.parseExpression("'a' < 'b'").getValue(Boolean.class));
		assertTrue(exp.parseExpression("'a' lt 'b'").getValue(Boolean.class));
		assertTrue(exp.parseExpression(" new Integer(123) instanceof T(Integer) ").getValue(Boolean.class));
		assertTrue(exp.parseExpression("'5.00' matches '^-?\\d+(\\.\\d{2})?$'").getValue(Boolean.class));
		// 逻辑操作
		assertTrue(exp.parseExpression("true and true").getValue(Boolean.class));
		assertTrue(exp.parseExpression("true or false").getValue(Boolean.class));
		assertFalse(exp.parseExpression("innerClass.isGt30ForAge() and true ").getValue(secontext, Boolean.class));
		assertFalse(exp.parseExpression("!innerClass.isGt30ForAge() and false").getValue(secontext, Boolean.class));
		assertTrue(exp.parseExpression("!false").getValue(Boolean.class));
		// 运算操作
		assertEquals(2, exp.parseExpression("1 + 1").getValue());
		assertEquals("ABab", exp.parseExpression("'AB' + 'ab'").getValue(String.class));
		assertEquals(25.0, exp.parseExpression("1 + 2 * 8 div 4 mod 2 + 2 ^ 3 * 3e0").getValue());
		assertEquals(exp.parseExpression("1 + 2 * 8 / 4 % 2 + 2 ^ 3 ").getValue(), exp.parseExpression("1 + 2 * 8 div 4 mod 2 + 2 ^ 3 ").getValue());
	}

	/**
	 * 赋值表达式测试
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testSpelAssignmentExpression() throws Exception {
		Date oldDate = bean.getTime();// 获取当前time属性值
		exp.parseExpression("time").setValue(secontext, new Date(113, 2, 25)); // 为time属性重新赋值
		Date newDate = bean.getTime();// 获取赋值后的time属性值
		assertEquals(2013, exp.parseExpression("time.Year + 1900").getValue(secontext));
		assertNotSame(oldDate, newDate);
		// 或者使用下属方法赋值
		assertEquals("abc", exp.parseExpression("Name = 'abc'").getValue(secontext));
		// 还原time默认，避免后续测试错误
		bean.setTime(oldDate);
		bean.setName("override");
	}

	/**
	 * 类型操作表达式测试
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testSpelTypesExpression() throws Exception {
		Class dateClass = exp.parseExpression("T(java.util.Date)").getValue(Class.class);
		assertEquals("java.util.Date", dateClass.getName());
		assertTrue(exp.parseExpression("T(java.math.RoundingMode).CEILING < T(java.math.RoundingMode).FLOOR").getValue(Boolean.class));
	}

	/**
	 * 构造函数调用测试
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSpelConstructorsExpression() throws Exception {
		SpelTestInnerClass spt = exp.parseExpression("new vision.apollo.common.spring.SpelTestInnerClass('constructTest',23)").getValue(SpelTestInnerClass.class);
		assertEquals(23, spt.getAge());
		assertEquals("constructTest", spt.getName());
	}

	/**
	 * 设置变量测试
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSpelVariablesExpression() throws Exception {
		List<Integer> list = new ArrayList<Integer>();
		list.addAll(Arrays.asList(2, 3, 4, 5, 6, 7, 9));
		secontext.setVariable("list", list);
		List<Integer> vList = (List<Integer>) exp.parseExpression("#list").getValue(secontext);
		assertEquals(vList, list);
		List<Integer> nums = (List<Integer>) exp.parseExpression("#list.?[#this >5]").getValue(secontext); // 获取值大于5的元素集合
		assertEquals(nums, Arrays.asList(6, 7, 9));
	}

	/**
	 * 自定义函数表达式测试
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSpelFunctionExpression() throws Exception {
		StandardEvaluationContext context = new StandardEvaluationContext();
		context.registerFunction("len", SpElContextBean.class.getDeclaredMethod("len", new Class[] { String.class }));
		assertEquals(3, exp.parseExpression("#len('abc')").getValue(context));
	}

	@Test
	public void testSpelBeanExpression() throws Exception {}

	/**
	 * 三元操作测试
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSpelTernaryOperatorExpression() throws Exception {
		assertTrue(exp.parseExpression(" true ? true :false").getValue(Boolean.class));
		assertEquals("is true", exp.parseExpression(" 1 == 1 ? 'is true' :'is false'").getValue(String.class));
	}

	/**
	 * Elvis 操作测试
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSpeleElvisOperatorExpression() throws Exception {
		Expression ex = exp.parseExpression("name?:'name is null'");
		assertEquals("override", ex.getValue(secontext, String.class));
		bean.setName(null);
		assertEquals("name is null", ex.getValue(secontext, String.class));
		bean.setName("override");
	}

	/**
	 * 安全导航操作测试
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSpelSafeNavOperatorExpression() throws Exception {
		assertEquals("innerClass", exp.parseExpression("innerClass?.name").getValue(secontext, String.class));
		SpelTestInnerClass temp = bean.getInnerClass();
		bean.setInnerClass(null);
		// 使用这种表达式可以避免抛出空指针异常
		assertNull(exp.parseExpression("innerClass?.name").getValue(secontext, String.class));
		bean.setInnerClass(temp);
	}

	/**
	 * 集合选择表达式测试
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSpelCollectionSelectExpression() throws Exception {
		bean.setNumbers(Arrays.asList(2, 3, 4, 5, 6, 7, 9));
		List<Integer> nums = (List<Integer>) exp.parseExpression("numbers.?[#this >5]").getValue(secontext);
		assertEquals(nums, Arrays.asList(6, 7, 9));
		// 获取第一个元素
		assertEquals(6, exp.parseExpression("numbers.^[#this > 5]").getValue(secontext));
		// 获取最后一个元素
		assertEquals(9, exp.parseExpression("numbers.$[#this > 5]").getValue(secontext));
		Map<Integer, String> maps = (Map<Integer, String>) exp.parseExpression("maps.?[value == 'string3' ]").getValue(secontext);
		Map<Integer, String> tmap = new HashMap<Integer, String>();
		tmap.put(3, "string3");
		assertEquals(maps, tmap);
		Map<Integer, String> mapk = (Map<Integer, String>) exp.parseExpression("maps.?[key > 2 and key < 4 ]").getValue(secontext);
		assertEquals(mapk, tmap);
	}

	/**
	 * 投影表达式测试
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSpelProjectionExpression() throws Exception {
		bean.setNumbers(Arrays.asList(2, 3, 4, 5, 6));
		assertEquals(Arrays.asList(5, 6, 7, 8, 9), exp.parseExpression("numbers.![#this+3]").getValue(secontext));
		List<Integer> keys = (List<Integer>) exp.parseExpression("maps.![key]").getValue(secontext);
		assertEquals(keys, Arrays.asList(1, 2, 3, 4));
		List<String> mapv = (List<String>) exp.parseExpression("maps.![value]").getValue(secontext);
		assertEquals(mapv, Arrays.asList("string1", "string2", "string3", "String4"));
		List<Boolean> mapK = (List<Boolean>) exp.parseExpression("maps.![key > 2 and value !='String4']").getValue(secontext);
		assertEquals(mapK, Arrays.asList(false, false, true, false));
	}

	/**
	 * 模板语言测试
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSpelTemplate() throws Exception {
		assertEquals(" this is a test 4", exp.parseExpression(" this is a test #{ maps.![key].get(3)}", new TemplateParserContext()).getValue(secontext, String.class));
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
}
