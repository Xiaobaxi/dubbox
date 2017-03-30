package vision.apollo.common.spring;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class SpElContextBean {
	private Date time;

	private String name;

	private SpelTestInnerClass innerClass = new SpelTestInnerClass("innerClass", 29);

	private List<Integer> numbers;

	private Map<Integer, String> maps;

	public static Object sayHelloForSpEl(String express) {
		ExpressionParser exp = new SpelExpressionParser();
		Expression ex = exp.parseExpression(express);
		return ex.getValue();
	}

	/**
	 * 自定义方法
	 * 
	 * @param string
	 * @return
	 */
	public static int len(String string) {
		return string.length();
	}

	public Map<Integer, String> getMaps() {
		return maps;
	}

	public void setMaps(Map<Integer, String> maps) {
		this.maps = maps;
	}

	public List<Integer> getNumbers() {
		return numbers;
	}

	public void setNumbers(List<Integer> numbers) {
		this.numbers = numbers;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SpelTestInnerClass getInnerClass() {
		return innerClass;
	}

	public void setInnerClass(SpelTestInnerClass innerClass) {
		this.innerClass = innerClass;
	}
}

class SpelTestInnerClass {
	private String name;

	private int age;

	public SpelTestInnerClass(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public boolean isGt30ForAge() {
		return this.age > 30;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}