package cn.cjp.spider.core.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 种子发现规则定义
 *
 * @author sucre
 */
@Getter
@Setter
public class SeedDiscoveryRule {

    /**
     * 规则
     */
    private String  pattern;
    private String  urlTemplate;
    private Integer type;

}
