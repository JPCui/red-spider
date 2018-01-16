package cn.cjp.spider.core.model;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

/**
 * 解析规则
 */
@Data
public class ParseRuleModel {

    private String type = "PARSE_RULE";

    /**
     * 唯一
     */
    @NotEmpty
    private String ruleName;

    /**
     * URL规则，符合该规则的文档，由当前解析规则解析
     */
    @NotEmpty
    private String urlPattern;

    @NotNull
    private List<Attr> attrs;

    @NotNull
    private Attr parentAttr;

    /**
     * 是否是列表页
     */
    private int isList;


}
