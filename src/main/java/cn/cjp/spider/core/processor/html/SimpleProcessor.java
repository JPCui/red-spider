package cn.cjp.spider.core.processor.html;

import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.core.config.SpiderConst;
import cn.cjp.spider.core.discovery.Discovery;
import cn.cjp.spider.core.discovery.DiscoveryFactory;
import cn.cjp.spider.core.enums.DenoisingType;
import cn.cjp.spider.core.enums.ParserType;
import cn.cjp.spider.core.http.UserAgents;
import cn.cjp.spider.core.model.Attr;
import cn.cjp.spider.core.model.ParseRuleModel;
import cn.cjp.spider.core.model.SeedDiscoveryRule;
import cn.cjp.spider.core.model.SiteModel;
import cn.cjp.spider.util._99libUtil;
import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 流程图如下：
 * <p>
 *
 * <pre>
 *    ...  --> processor --> ...
 *             /        \
 *            /          \
 *     page parser --> seed discover
 * </pre>
 *
 * @author sucre
 */
@Slf4j
public class SimpleProcessor implements PageProcessor {

    private Site site;

    private final SiteModel siteModel;

    private Discovery discovery;

    /**
     * 声明该处理器是一次性的，不会进行URL发现
     */
    @Setter
    private boolean onceOnly;

    public SimpleProcessor(SiteModel siteModel) {
        discovery = new DiscoveryFactory();
        this.siteModel = siteModel;
        this.site = Site.me()
            .setUserAgent(UserAgents.get())
            .setRetryTimes(3)
            .setSleepTime(3000);

        site.addHeader("User-Agent", UserAgents.get());
        site.setDomain(siteModel.getSiteName());

        // FIXME site.addCookie 不能用？暂时用addHeader代替
        Optional<String> cookieStrOpt = Optional.ofNullable(this.siteModel.getCookieStr());
        if (cookieStrOpt.isPresent()) {
            site.addHeader("Cookie", this.siteModel.getCookieStr());
        }

    }

    @Override
    public void process(Page page) {
        try {
            log.info(String.format("parse url: %s", page.getUrl().get()));
            this._process(page);
        } catch (Exception e) {
            log.error(String.format("parse fail, url=%s", page.getUrl().get()), e);
        }
    }

    private void _process(Page page) {
        // 按照URL对应的解析器进行解析
        SpiderConfig.getParseRule(siteModel.getSiteName(), page.getUrl().get()).ifPresent(parseRule -> {
            final List<Attr> attrs      = parseRule.getAttrs();
            final Attr       parentAttr = parseRule.getParentAttr();
            final int        isList     = parseRule.getIsList();

            Assert.notNull(attrs);

            Selectable root = this.parse(page, ParserType.fromValue(parentAttr.getParserType()));
            root = denosingBefore(root, parseRule);
            if (isList == 1) {
                List<Selectable> domList = this.parse(page, root, parentAttr).nodes();
                List<JSONObject> jsons   = this.parseNodes(page, domList, attrs);
                jsons.forEach(json -> {
                    setDefaultValue(page, siteModel, json);
                });

                page.putField("jsons", jsons);
            } else {
                Selectable dom  = this.parse(page, root, parentAttr);
                JSONObject json = this.parseNode(page, dom, attrs);
                setDefaultValue(page, siteModel, json);

                page.putField("json", json);
            }

            if (siteModel.getSkip() == 1) {
                page.setSkip(true);
            }
        });

        this.findSeeds(page);
    }

    private void setDefaultValue(Page page, SiteModel siteModel, JSONObject json) {
        json.put(SpiderConst.KEY_REFER_URL, page.getUrl().get());
        if (StringUtils.isEmpty(siteModel.getDb())) {
            json.put(SpiderConst.KEY_DB_NAME, siteModel.getSiteName());
        } else {
            json.put(SpiderConst.KEY_DB_NAME, siteModel.getDb());
        }
    }

    /**
     * 发现新URL
     */
    private void findSeeds(Page page) {
        if (onceOnly) {
            return;
        }
        final List<SeedDiscoveryRule> seedDiscoveries = siteModel.getSeedDiscoveries();
        if (seedDiscoveries != null) {
            seedDiscoveries.forEach(seedDiscovery -> {
                discovery.discover(page, seedDiscovery);
            });
        }
    }

    private List<JSONObject> parseNodes(Page page, List<Selectable> domList, List<Attr> attrs) {
        List<JSONObject> jsons = new ArrayList<>();
        domList.forEach(dom -> {
            JSONObject json = this.parseNode(page, dom, attrs);
            jsons.add(json);
        });
        return jsons;
    }

    /**
     * @param dom 文档节点
     * @param attrs 属性解析列表
     */
    private JSONObject parseNode(Page page, Selectable dom, List<Attr> attrs) {
        JSONObject json = new JSONObject();
        attrs.forEach(attr -> {
            try {

                Selectable selectable = this.parse(page, dom, attr);
                if (attr.getField().equals("tags")) {
                    log.info(attr.toString());
                }

                Object result = null;
                if (attr.isHasEmbeddedAttr()) {
                    result = parseNodeWithEmbeddedAttr(page, selectable, attr);
                } else {
                    result = parseNodeWithoutEmbeddedAttr(selectable, attr);
                }

                // 去噪
                result = denosing(page, attr, result);

                json.put(attr.getField(), result);

                if (attr.isUnique()) {
                    // 设置唯一键
                    // TODO 应优化
                    json.put(SpiderConst.KEY_UNIQUE, attr.getField());
                }
            } catch (RuntimeException e) {
                log.error(String.format("parse fail, field=%s, parserPath=%s, dom=%s", attr.getField(),
                                        attr.getParserPath(), dom.toString()));
                throw e;
            }
        });
        return json;
    }

    private Selectable denosingBefore(Selectable selectable, ParseRuleModel parseRuleModel) {

        int[] denoisingTypes = parseRuleModel.getDenoisingTypes();
        if (denoisingTypes != null) {
            for (int denoisingBeforeType : denoisingTypes) {
                DenoisingType denoisingType = DenoisingType.fromValue(denoisingBeforeType);
                switch (denoisingType) {
                    case _99LIB_TEXT_REGEX_REMOVE:
                        String txt = selectable.get();
                        txt = txt.replaceAll("<acronym>((?=[\\s\\S])[^<]*)</acronym>", "");
                        txt = txt.replaceAll("<bdo>((?=[\\s\\S])[^<]*)</bdo>", "");
                        txt = txt.replaceAll("<big>((?=[\\s\\S])[^<]*)</big>", "");
                        txt = txt.replaceAll("<cite>((?=[\\s\\S])[^<]*)</cite>", "");
                        txt = txt.replaceAll("<code>((?=[\\s\\S])[^<]*)</code>", "");
                        txt = txt.replaceAll("<dfn>((?=[\\s\\S])[^<]*)</dfn>", "");
                        txt = txt.replaceAll("<kbd>((?=[\\s\\S])[^<]*)</kbd>", "");
                        txt = txt.replaceAll("<q>((?=[\\s\\S])[^<]*)</q>", "");
                        txt = txt.replaceAll("<s>((?=[\\s\\S])[^<]*)</s>", "");
                        txt = txt.replaceAll("<samp>((?=[\\s\\S])[^<]*)</samp>", "");
                        txt = txt.replaceAll("<strike>((?=[\\s\\S])[^<]*)</strike>", "");
                        txt = txt.replaceAll("<tt>((?=[\\s\\S])[^<]*)</tt>", "");
                        txt = txt.replaceAll("<u>((?=[\\s\\S])[^<]*)</u>", "");
                        txt = txt.replaceAll("<var>((?=[\\s\\S])[^<]*)</var>", "");
                        selectable = new Html(txt);
                        break;

                    default:
                        break;
                }
            }
        }
        return selectable;
    }

    /**
     * 去噪
     */
    private Object denosing(Page page, Attr attr, Object result) {
        DenoisingType denoisingType = DenoisingType.fromValue(attr.getDenoisingType());
        if (denoisingType != null) {
            switch (denoisingType) {
                case _99LIB_SECTIONS: {
                    if (result instanceof List) {
                        result = _99libUtil.extractValidSections((List<?>) result,
                                                                 page.getHtml().css("meta[name=client]", "content").get());
                    }
                    break;
                }
                default:
                    break;
            }
        }
        return result;
    }

    /**
     * TODO 重构
     */
    private Object parseNodeWithEmbeddedAttr(Page page, Selectable selectable, Attr attr) {
        Object result = null;
        if (attr.isHasMultiValue()) {
            List<Selectable> domList = this.parse(page, selectable, attr).nodes();
            List<JSONObject> jsons   = this.parseNodes(page, domList, attr.getEmbeddedAttrs());
            result = jsons;
        } else {
            Selectable dom  = this.parse(page, selectable, attr);
            JSONObject json = this.parseNode(page, dom, attr.getEmbeddedAttrs());
            result = json;
        }
        return result;
    }

    private Object parseNodeWithoutEmbeddedAttr(Selectable selectable, Attr attr) {
        Object result = null;
        if (attr.isHasMultiValue()) {
            if (attr.getFilterRepeat() == 1) {
                // 去重（还要保证顺序不变）
                List<String> values = selectable.all().stream().filter(s -> !StringUtils.isEmpty(s))
                    .collect(Collectors.toList());
                // json.put(attr.getField(), values);
                result = values;
            } else {
                List<String> values = selectable.all().stream().filter(s -> !StringUtils.isEmpty(s))
                    .collect(Collectors.toList());
                // json.put(attr.getField(), values);
                result = values;
            }
        } else {
            String value = selectable.get();
            if (value != null) {
                value = value.trim();
            }
            // json.put(attr.getField(), value);
            result = value;
        }
        return result;
    }

    /**
     * 解析Page -> Selectable (Html / Json)
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
     */
    private Selectable parse(Page page, Selectable dom, Attr attr) {
        try {
            Selectable value      = null;
            ParserType parserType = ParserType.fromValue(attr.getParserType());
            if (parserType != null) {
                switch (parserType) {
                    case BASE: {
                        value = new PlainText(attr.getDefaultValue());
                        break;
                    }
                    case URL_PATTERN: {
                        String  url     = page.getUrl().get();
                        String  regex   = attr.getParserPath();
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
                        // TODO JacksonUtil.toJson(dom.css("#content div",
                        // "allText").all());
                        value = dom.css(attr.getParserPath(), attr.getParserPathAttr());
                        break;
                    }
                    case JSON: {
                        // webmagic自带的Selector，解析json后的数据会变成PlainText，所以下次使用的时候还需要转换为Json
                        var t = dom;
                        if (PlainText.class.equals(t.getClass())) {
                            t = new Json(t.get());
                        }
                        value = t.jsonPath(attr.getParserPath());
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
            if (attr.getNested() != null) {
                value = this.parse(page, value, attr.getNested());
            }
            if (log.isDebugEnabled()) {
                String t = attr.getField();
                if (t.length() > 30) {
                    t = t.substring(0, 30);
                }
                if (log.isDebugEnabled()) {
                    String _1 = t;
                    if (_1.length() > 30) {
                        _1 = _1.substring(0, 30);
                    }
                    String _2 = null;
                    if (value != null) {
                        _2 = value.toString();
                        if (_2.length() > 30) {
                            _2 = _2.substring(0, 30);
                        }
                    }
                    log.debug(String.format("found attr %s : %s", _1, _2));
                }
            }
            return value;
        } catch (Throwable t) {
            log.error(String.format("parser fail, page=%s, dom=%s, attr=%s", page.getRequest().getUrl(), dom.toString(),
                                    JSON.toJSONString(attr)));
            throw t;
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

}
