package cn.cjp.spider.core.model;

import java.util.List;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 目标页爬取规则定义
 *
 * @author sucre
 */
@Data
public class PageModel {

    @NotNull
    private String siteName;

    private String description;

    private String db;

    @NotNull
    private String url;

    @NotNull
    private String urlPattern;

    @NotNull
    private List<SeedDiscoveryRule> seedDiscoveries;

    /**
     * 为了遵循 webmagic 的设计，同一站点的url队列使用同一个，所以把规则全部定义到一个page model里，由processor觉得该使用哪一个parse rule
     */
    @NotNull
    private List<String> parseRules;

    private List<ParseRuleModel> parseRuleModels;

    private int isList;

    private int skip = 1;

}
