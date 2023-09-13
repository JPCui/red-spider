package cn.cjp.spider.core.processor;

import cn.cjp.spider.core.enums.ParserType;
import cn.cjp.spider.core.model.Attr;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

public class XicidailiProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    /**
     * 方便查找dom列表
     */
    private Attr parentAttr;

    private List<Attr> attrs;

    private int skip = 1;

    private int isList = 0;

    @Override
    public void process(Page page) {

        if (isList == 1) {
            List<Selectable> domList = this
                .parse(this.parse(page, ParserType.fromValue(parentAttr.getParserType())), parentAttr).nodes();
            List<JSONObject> jsons = this.parseNodes(domList, attrs);

            page.putField("jsons", jsons);
        } else {
            Selectable dom  = this.parse(this.parse(page, ParserType.fromValue(parentAttr.getParserType())), parentAttr);
            JSONObject json = this.parseNode(dom, attrs);

            page.putField("json", json);
        }

        if (skip == 1) {
            page.setSkip(true);
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
     *
     */
    public JSONObject parseNode(Selectable dom, List<Attr> attrs) {
        JSONObject json = new JSONObject();
        attrs.forEach(attr -> {
            String value = this.parse(dom, attr).get();
            json.put(attr.getField(), value);
        });
        return json;
    }

    /**
     * 解析Page -> Selectable (Html / Json)
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
                case JSON:
                case JSON_ARRAY: {
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

    private Selectable parse(Selectable dom, Attr attr) {
        Selectable value = null;

        ParserType parserType = ParserType.fromValue(attr.getParserType());
        if (parserType != null) {
            switch (parserType) {
                case XPATH: {
                    value = dom.xpath(attr.getParserPath());
                    break;
                }
                case DOM: {
                    value = dom.css(attr.getParserPath(), "text");
                    break;
                }
                case JSON:
                case JSON_ARRAY: {
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
        return value;
    }

    /**
     * @deprecated
     */
    public static void main(String[] args) {

        String url = "http://www.xicidaili.com/nn";

        Attr parentAttr = new Attr("#ip_list tr:gt(1)", "", ParserType.DOM);

        List<Attr> attrs = new ArrayList<>();
        attrs.add(new Attr("td:eq(1)", "ip", ParserType.DOM));
        attrs.add(new Attr("td:eq(2)", "port", ParserType.DOM));
        attrs.add(new Attr("td:eq(5)", "protocol", ParserType.DOM));

        int skip = 1;

        int isList = 1;

        XicidailiProcessor processor = new XicidailiProcessor();
        processor.setAttrs(attrs);
        processor.setIsList(isList);
        processor.setParentAttr(parentAttr);
        processor.setSkip(skip);

        Spider.create(processor).addUrl(url).addPipeline(new ConsolePipeline()).run();
    }

    @Override
    public Site getSite() {
        return site;
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

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getIsList() {
        return isList;
    }

    public void setIsList(int isList) {
        this.isList = isList;
    }

    public void setSite(Site site) {
        this.site = site;
    }

}
