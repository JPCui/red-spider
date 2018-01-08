package cn.cjp.spider.core.processor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.cjp.spider.core.discovery.CommonDiscovery;
import cn.cjp.spider.core.discovery.Discovery;
import cn.cjp.spider.core.enums.ParserType;
import cn.cjp.spider.core.http.UserAgents;
import cn.cjp.spider.core.model.Attr;
import cn.cjp.spider.core.model.PageModel;
import cn.cjp.spider.core.model.SeedDiscoveryRule;
import cn.cjp.utils.Assert;
import cn.cjp.utils.StringUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.CssSelector;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 流程图如下：
 * <p>
 * <pre>
 *    ...  --> processor --> ...
 *             /        \
 *            /          \
 *     page parser --> seed discover
 * </pre>
 *
 * @author sucre
 */
public class SimpleProcessor implements PageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleProcessor.class);

    private Site site = Site.me().setUserAgent(UserAgents.get()).setRetryTimes(3).setSleepTime(3000);

    private PageModel pageModel;

    private Discovery discovery;

    public SimpleProcessor() {
        discovery = new CommonDiscovery();
    }

    @Override
    public void process(Page page) {
        LOGGER.debug("process: " + page.toString());
        final List<Attr> attrs = pageModel.getAttrs();
        final Integer isList = pageModel.getIsList();
        final Attr parentAttr = pageModel.getParentAttr();
        final Integer skip = pageModel.getSkip();

        Assert.assertNotNull(attrs);

        page.putField("db", pageModel.getDb());

        if (isList == 1) {
            Selectable root = this.parse(page, ParserType.fromValue(parentAttr.getParserType()));
            List<Selectable> domList = this.parse(root, parentAttr).nodes();
            List<JSONObject> jsons = this.parseNodes(domList, attrs);

            page.putField("jsons", jsons);
        } else {
            Selectable root = this.parse(page, ParserType.fromValue(parentAttr.getParserType()));
            Selectable dom = this.parse(root, parentAttr);
            JSONObject json = this.parseNode(dom, attrs);

            page.putField("json", json);
        }

        this.findSeeds(page);

        if (skip == 1) {
            page.setSkip(true);
        }
    }

    /**
     * 发现新URL
     *
     * @param page
     */
    private void findSeeds(Page page) {
        final List<SeedDiscoveryRule> seedDiscoveries = pageModel.getSeedDiscoveries();
        if (seedDiscoveries != null) {
            seedDiscoveries.forEach(seedDiscovery -> {
                discovery.discover(page, seedDiscovery);
            });
        }
    }

    public List<JSONObject> parseNodes(List<Selectable> domList, List<Attr> attrs) {
        List<JSONObject> jsons = new ArrayList<>();
        domList.forEach(dom -> {
            JSONObject json = this.parseNode(dom, attrs);
            jsons.add(json);
        });
        return jsons;
    }

    /**
     * @param dom   文档节点
     * @param attrs 属性解析列表
     * @return
     */
    public JSONObject parseNode(Selectable dom, List<Attr> attrs) {
        JSONObject json = new JSONObject();
        attrs.forEach(attr -> {
            Selectable selectable = this.parse(dom, attr);
            if (attr.isMulti()) {
                selectable.all().stream().forEach(String::trim);
                List<String> values = selectable.all();
                json.put(attr.getField(), values);
            } else {
                String value = selectable.get().trim();
                json.put(attr.getField(), value);
            }
        });
        return json;
    }

    /**
     * 解析Page -> Selectable (Html / Json)
     *
     * @param page
     * @param parserType
     * @return
     */
    private Selectable parse(Page page, ParserType parserType) {
        Selectable value = null;

        if (parserType != null) {
            switch (parserType) {
                case XPATH: {
                    value = page.getHtml();
                    break;
                }
                case DOM: {
                    value = page.getHtml();
                    break;
                }
                case JSON: {
                    value = page.getJson();
                    break;
                }
                case REGEX: {
                    value = page.getHtml();
                    break;
                }
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return value;
    }

    /**
     * 可嵌套的解析方法
     *
     * @param dom
     * @param attr
     * @return
     */
    private Selectable parse(Selectable dom, Attr attr) {
        if (StringUtil.isEmpty(attr.getParserPath())) {
            return dom;
        }

        Selectable value = null;
        ParserType parserType = ParserType.fromValue(attr.getParserType());
        if (parserType != null) {
            switch (parserType) {
                case XPATH: {
                    value = dom.xpath(attr.getParserPath());
                    break;
                }
                case DOM: {
                    value = dom.css(attr.getParserPath(), attr.getParserPathAttr());
                    break;
                }
                case JSON: {
                    value = dom.jsonPath(attr.getParserPath());
                    break;
                }
                case REGEX: {
                    value = dom.regex(attr.getParserPath());
                    break;
                }
                default:
                    throw new UnsupportedOperationException();
            }
        }

        // 嵌套
        return attr.getNested() == null ? value : this.parse(value, attr.getNested());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public PageModel getPageModel() {
        return pageModel;
    }

    public void setPageModel(PageModel pageModel) {
        this.pageModel = pageModel;
    }

}
