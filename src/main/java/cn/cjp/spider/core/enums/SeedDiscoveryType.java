package cn.cjp.spider.core.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 种子发现规则的类型
 *
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
     * 分页URL策略：从HTML中过滤所有a标签，找出匹配的href链接
     *
     * http://xxx.com/?page=(.*)
     *
     */
    PAGING_STRATEGY_MATCH_A_HREF_IN_HTML(10, "MATCH_A_HREF_IN_HTML"),

    /**
     * 分页URL策略：分页数据隐藏在URL上，在原基础上+1
     *
     * http://xxx.com/xxx.json?page=(.*)
     */
    PAGING_STRATEGY_PAGE_NUM_ON_URL(20, "JSON_NORMAL_PAGING"),

    /**
     * 使用正则表达式
     */
//    REGEX(30, "REGEX"),

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
