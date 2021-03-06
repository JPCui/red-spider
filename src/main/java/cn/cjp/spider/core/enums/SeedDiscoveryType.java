package cn.cjp.spider.core.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 种子发现规则的类型
 * 
 * @author sucre
 *
 */
public enum SeedDiscoveryType {

    /**
     * 尝试所有方法
     */
    COMMON(0, "通用"),

    /**
     * 
     * 在所有 a 标签中找出匹配的链接
     * 
     * http://xxx.com/?page=(.*)
     * 
     */
    HTML_NORMAL_PAGING(10, "HTML_NORMAL_PAGING"),

    /**
     * 在json接口上，获取分页字段，在原基础上+1
     * 
     * http://xxx.com/xxx.json?page=(.*)
     */
    JSON_NORMAL_PAGING(20, "JSON_NORMAL_PAGING"),

    /**
     * 使用正则表达式
     */
    REGEX(30, "REGEX"),

    ;

    private final int code;
    private final String message;

    public static SeedDiscoveryType fromValue(final int code) {
        for (SeedDiscoveryType salaryUnit : SeedDiscoveryType.values()) {
            if (code == salaryUnit.getValue()) {
                return salaryUnit;
            }
        }

        return null;
    }

    public static List<SeedDiscoveryType> fromValues(final Iterable<Integer> codes) {

        List<SeedDiscoveryType> jobTags = new ArrayList<>();

        for (Integer code : codes) {

            final SeedDiscoveryType jobTag = fromValue(code);

            if (jobTag != null) {
                jobTags.add(jobTag);
            }
        }

        return jobTags;
    }

    SeedDiscoveryType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getValue() {
        return code;
    }

    public String getDescription() {
        return message;
    }

}
