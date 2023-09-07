package cn.cjp.spider.core.model;

import java.util.List;

import java.util.Map;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 目标页爬取规则定义
 *
 * @author sucre
 */
@Data
public class SiteModel {

    @NotNull
    private String siteName;

    private String domain;

    private String description;

    private String db;

    private boolean isRecycle = false;

    private String cookieStr;
    private List<Cookie> cookies;

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

    /**
     * 跳过存储
     *
     * @deprecated 没有必要了，通过配置pipeline来决定是否存储以及存储方式
     */
    private int skip = 1;

    private boolean autoStartup = false;

}
