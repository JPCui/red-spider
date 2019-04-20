package cn.cjp.spider.util;

import cn.cjp.spider.core.model.SiteModel;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class ValidatorUtilTest {

    @Test
    public void test() {
        SiteModel                           siteModel = new SiteModel();
        Set<ConstraintViolation<SiteModel>> vs        = ValidatorUtil.validate(siteModel);
        vs.forEach(v->{
            System.out.println(v);
            System.out.println(v.getMessage());
            System.out.println(v.getMessageTemplate());
            System.out.println(v.getPropertyPath());
        });

        System.out.println(ValidatorUtil.validateWithTemplateByParam(siteModel));
    }


}
