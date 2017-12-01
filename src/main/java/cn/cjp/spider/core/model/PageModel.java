package cn.cjp.spider.core.model;

import java.util.List;

/**
 * 目标页爬取规则定义
 * 
 * @author sucre
 *
 */
public class PageModel {

    private String url;

    /**
     * @deprecated use {@link #seedDiscoveries}
     */
    private String findSeedPattern;

    private List<SeedDiscoveryRule> seedDiscoveries;

    private Attr parentAttr;

    private List<Attr> attrs;

    private Integer skip;

    private Integer isList;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Attr getParentAttr() {
        return parentAttr;
    }

    public void setParentAttr(Attr parentAttr) {
        this.parentAttr = parentAttr;
    }

    public List<Attr> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<Attr> attrs) {
        this.attrs = attrs;
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    public Integer getIsList() {
        return isList;
    }

    public void setIsList(Integer isList) {
        this.isList = isList;
    }

    public String getFindSeedPattern() {
        return findSeedPattern;
    }

    public void setFindSeedPattern(String findSeedPattern) {
        this.findSeedPattern = findSeedPattern;
    }

    public List<SeedDiscoveryRule> getSeedDiscoveries() {
        return seedDiscoveries;
    }

    public void setSeedDiscoveries(List<SeedDiscoveryRule> seedDiscoveries) {
        this.seedDiscoveries = seedDiscoveries;
    }

}
