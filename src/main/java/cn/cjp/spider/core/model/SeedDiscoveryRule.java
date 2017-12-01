package cn.cjp.spider.core.model;

/**
 * 种子发现规则定义
 * 
 * @author sucre
 *
 */
public class SeedDiscoveryRule {

    /**
     * 规则
     */
    private String pattern;

    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

}
