package vision.apollo.common.spring;

import org.junit.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class MySpringElTest {
	ExpressionParser exp=new SpelExpressionParser();
	@Test
	public void testBean(){
		System.out.println(eval("2+2"));
		
	}
	
	Object eval(String expression){
		SpElContextBean bean=new SpElContextBean();
		StandardEvaluationContext secontext=new StandardEvaluationContext(bean);
		return exp.parseExpression(expression).getValue(secontext);
	}
	
	
}
