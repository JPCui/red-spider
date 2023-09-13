package cn.cjp.spider;

import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpElTest {

    @Test
    public void test() {
        JSONObject params = new JSONObject();
        params.put("name", "jj");

        JSONObject ctx = new JSONObject();
        ctx.put("id", "10001");
        ctx.put("params", (params));

//        System.out.println(new SpELEngine().eval("#params.name", ctx));

        SpelExpressionParser    parser            = new SpelExpressionParser();
        final EvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setVariable("id", "xxx");
        evaluationContext.setVariable("params", params);

        Assert.assertEquals(parser.parseExpression("#id").getValue(evaluationContext), "xxx");
        Assert.assertEquals(parser.parseExpression("#params['name']").getValue(evaluationContext), "jj");
        Assert.assertEquals(parser.parseExpression("#params['name'] + ' hello'").getValue(evaluationContext), "jj hello");
    }

}
