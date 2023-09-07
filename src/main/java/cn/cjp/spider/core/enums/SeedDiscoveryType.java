package cn.cjp.spider.core.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 种子发现规则的类型
 *
 * @author sucre
 */
public enum SeedDiscoveryType {

    /**
     * 尝试所有方法
     */
    COMMON(0, "通用"),

    /**
     * 在所有 a 标签中找出匹配的链接
     *
     * http://xx.com/?page=(.*)
     */
    HTML_NORMAL_PAGING(10, "HTML_NORMAL_PAGING"),

    /**
     * 当前url页码+1
     *
     * 比如：匹配出当前url的页码，http://xx.com/xx.json?page=9，那么下一页则是 10
     */
    PAGE_INDEX_INCREMENT(20, "PAGE_INDEX_INCREMENT"),

    /**
     * 使用正则表达式
     */
    REGEX(30, "REGEX"),

    /**
     * 提供url正则 + 从抓取的字段中提取参数，比如：
     * <pre>
     *     {
     *       "url": "https://api.xx.com/main?id={id}",
     *       "type": "40"
     *     }
     * </pre>
     * 那么就会将当前页面抓取的id，替换到pattern的{id}中，形成下次要抓取的url
     */
    URL_TEMPLATE(40, "URL_TEMPLATE"),

    ;

    private final int    code;
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
