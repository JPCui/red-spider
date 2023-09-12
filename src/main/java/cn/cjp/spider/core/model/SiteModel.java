package cn.cjp.spider.core.model;

import cn.cjp.spider.core.pipeline.AbstractPipeline;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * 目标页爬取规则定义
 *
 * @author sucre
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SiteModel {

    @NotNull
    private String siteName;

    private String domain;

    private String description;

    private String db;

    private boolean isRecycle = false;
    @ApiModelProperty("循环cron")
    private String  recycleCron;

    private String       cookieStr;
    private List<Cookie> cookies;

    /**
     * 初始种子
     */
    @NotNull
    private String url;

    @NotNull
    private String urlPattern;

    @NotNull
    private List<SeedDiscoveryRule> seedDiscoveries;

    private List<PipelineDomain> pipelines;

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
    /**
     * @deprecated 该字段迁移到domain
     */
    private boolean started = false;

}
