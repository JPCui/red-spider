package cn.cjp.spider.core.processor;

import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.core.config.SpiderConst;
import cn.cjp.spider.core.discovery.CommonDiscovery;
import cn.cjp.spider.core.discovery.Discovery;
import cn.cjp.spider.core.enums.ParserType;
import cn.cjp.spider.core.http.UserAgents;
import cn.cjp.spider.core.model.Attr;
import cn.cjp.spider.core.model.PageModel;
import cn.cjp.spider.core.model.SeedDiscoveryRule;
import cn.cjp.utils.Assert;
import cn.cjp.utils.StringUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        List<String> parseRules = pageModel.getParseRules();

        // 按照URL对应的解析器进行解析
        SpiderConfig.getParseRule(pageModel.getSiteName(), page.getUrl().get()).ifPresent(parseRule -> {
            final List<Attr> attrs = parseRule.getAttrs();
            final Attr parentAttr = parseRule.getParentAttr();
            final int isList = parseRule.getIsList();

            Assert.assertNotNull(attrs);

            if (isList == 1) {
                Selectable root = this.parse(page, ParserType.fromValue(parentAttr.getParserType()));
                List<Selectable> domList = this.parse(page, root, parentAttr).nodes();
                List<JSONObject> jsons = this.parseNodes(page, domList, attrs);
                jsons.forEach(json -> {
                    setDefaultValue(page, pageModel, json);
                });

                page.putField("jsons", jsons);
            } else {
                Selectable root = this.parse(page, ParserType.fromValue(parentAttr.getParserType()));
                Selectable dom = this.parse(page, root, parentAttr);
                JSONObject json = this.parseNode(page, dom, attrs);
                setDefaultValue(page, pageModel, json);

                page.putField("json", json);
            }

            if (parseRule.getSkip() == 1) {
                page.setSkip(true);
            }
        });

        this.findSeeds(page);
    }

    private void setDefaultValue(Page page, PageModel pageModel, JSONObject json) {
        json.put(SpiderConst.KEY_REFER_URL, page.getUrl().get());
        if (StringUtil.isEmpty(pageModel.getDb())) {
            json.put(SpiderConst.KEY_DB_NAME, pageModel.getSiteName());
        } else {
            json.put(SpiderConst.KEY_DB_NAME, pageModel.getDb());
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

    public List<JSONObject> parseNodes(Page page, List<Selectable> domList, List<Attr> attrs) {
        List<JSONObject> jsons = new ArrayList<>();
        domList.forEach(dom -> {
            JSONObject json = this.parseNode(page, dom, attrs);
            jsons.add(json);
        });
        return jsons;
    }

    /**
     * @param dom   文档节点
     * @param attrs 属性解析列表
     * @return
     */
    public JSONObject parseNode(Page page, Selectable dom, List<Attr> attrs) {
        JSONObject json = new JSONObject();
        attrs.forEach(attr -> {
            try {
                Selectable selectable = this.parse(page, dom, attr);
                if (attr.isMulti()) {
                    List<String> values =
                            selectable.all().stream().filter(s -> StringUtil.isEmpty(s)).collect(Collectors.toList());
                    json.put(attr.getField(), values);
                } else {
                    String value = selectable.get().trim();
                    json.put(attr.getField(), value);
                }
            } catch (RuntimeException e) {
                LOGGER.error(String.format("parse fail, field=%s, parserPath=%s, dom=%s", attr.getField(), attr.getParserPath(), dom.toString()));
                throw e;
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
                case DOM:
                case REGEX:
                case XPATH: {
                    value = page.getHtml();
                    break;
                }
                case JSON: {
                    value = page.getJson();
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
    private Selectable parse(Page page, Selectable dom, Attr attr) {
        Selectable value = null;
        ParserType parserType = ParserType.fromValue(attr.getParserType());
        if (parserType != null) {
            switch (parserType) {
                case BASE: {
                    value = new PlainText(attr.getDefaultValue());
                    break;
                }
                case URL_PATTERN: {
                    String url = page.getUrl().get();
                    String regex = attr.getParserPath();
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(url);
                    if (matcher.find()) {
                        value = new PlainText(matcher.group(1));
                    }
                    break;
                }
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
        return attr.getNested() == null ? value : this.parse(page, value, attr.getNested());
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
