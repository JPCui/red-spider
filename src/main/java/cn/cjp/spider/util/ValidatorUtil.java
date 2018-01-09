package cn.cjp.spider.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 验证java bean的工具类
 */
public class ValidatorUtil {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> Set<ConstraintViolation<T>> validate(T t) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);
        return constraintViolations;
    }

    public static <T> void validateThrowable(T t) throws Throwable {
        List<String> vs = validateWithTemplateByParam(t);
        if (!vs.isEmpty()) {
            ValidationException ex = new ValidationException(vs.toString());
            throw ex;
        }
    }

    /**
     * <pre>
     *     @NotNull(message="%s不能为空")
     *     private String abc;
     *
     *     get:
     *
     *     abc不能为空
     *
     * </pre>
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> List<String> validateWithTemplateByParam(T t) {
        Set<ConstraintViolation<T>> cSet = validate(t);
        List<String> msgs = new ArrayList<>();
        cSet.forEach(c -> {
            String msgTpl = c.getMessage();
            String prop = c.getPropertyPath().toString();
            msgs.add(prop + msgTpl);
        });
        return msgs;
    }
}
