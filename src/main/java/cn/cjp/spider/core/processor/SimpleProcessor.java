package cn.cjp.spider.core.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;

import cn.cjp.spider.core.enums.PageType;
import cn.cjp.spider.core.enums.ParserType;
import cn.cjp.spider.core.model.Attr;
import cn.cjp.utils.Assert;
import cn.cjp.utils.StringUtil;
import cn.cjp.utils.URLUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

public class SimpleProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    private PageType pageType = PageType.HTML;

    /**
     * 方便查找dom列表
     */
    private Attr parentAttr;

    private List<Attr> attrs;

    private int skip = 1;

    private int isList = 0;

    private String findSeedPattern;

    @Override
    public void process(Page page) {
        Assert.assertNotNull(attrs);

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
        if (StringUtil.isEmpty(findSeedPattern)) {
            return;
        }

        List<String> foundUrlList = new ArrayList<>();

        switch (pageType) {
        case JSON:
            Pattern pattern = Pattern.compile(this.findSeedPattern);
            Matcher matcher = pattern.matcher(page.getUrl().get());
            if (matcher.find()) {
                // System.out.println(matcher.groupCount());
                // System.out.println(matcher.group(1));
                // System.out.println(matcher.group(2));
                String pageNumStr = matcher.group(2);
                if (pageNumStr != null) {
                    int pageNum = Integer.parseInt(pageNumStr);
                    String foundUrl = matcher.replaceFirst(Integer.toString(pageNum + 1));
                    foundUrlList.add(foundUrl);
                }
            }
            break;
        case HTML:
            List<String> allUrls = page.getHtml().$("a", "href").all();
            allUrls.forEach(url -> {
                String currUrl = url;
                if (currUrl.startsWith("/")) {
                    // 相对路径的处理
                    currUrl = URLUtil.relative(page.getUrl().get(), url);
                }
                if (currUrl.matches(this.findSeedPattern)) {
                    foundUrlList.add(currUrl);
                }
            });
            break;
        default:
            throw new UnsupportedOperationException();
        }

        page.addTargetRequests(foundUrlList);
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
     * @param attrs
     * @return
     */
    public JSONObject parseNode(Selectable dom, List<Attr> attrs) {
        JSONObject json = new JSONObject();
        attrs.forEach(attr -> {
            String value = this.parse(dom, attr).get().trim();
            json.put(attr.getField(), value);
        });
        return json;
    }

    /**
     * 解析Page -> Selectable (Html / Json)
     * 
     * @param dom
     * @param attr
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

    public String getFindSeedPattern() {
        return findSeedPattern;
    }

    public void setFindSeedPattern(String findSeedPattern) {
        this.findSeedPattern = findSeedPattern;
    }
}
