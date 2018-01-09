package cn.cjp.spider.util;

import cn.cjp.spider.core.model.PageModel;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class ValidatorUtilTest {

    @Test
    public void test() {
        PageModel pageModel = new PageModel();
        Set<ConstraintViolation<PageModel>> vs = ValidatorUtil.validate(pageModel);
        vs.forEach(v->{
            System.out.println(v);
            System.out.println(v.getMessage());
            System.out.println(v.getMessageTemplate());
            System.out.println(v.getPropertyPath());
        });

        System.out.println(ValidatorUtil.validateWithTemplateByParam(pageModel));
    }


}
